package uz.click.mobilesdk.core

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.squareup.moshi.Moshi
import io.reactivex.Single
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import uz.click.mobilesdk.BuildConfig
import uz.click.mobilesdk.core.callbacks.ResponseListener
import uz.click.mobilesdk.core.data.*
import uz.click.mobilesdk.core.errors.ServerNotAvailableException
import uz.click.mobilesdk.utils.ErrorUtils
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
class ClickMerchantManager {

    companion object {
        var logs = false
        private const val CONNECT_TIME_OUT: Long = 10 * 1000 // 10 second
        private const val READ_TIME_OUT: Long = 10 * 1000 // 10 second
        private const val WRITE_TIME_OUT: Long = 10 * 1000 // 10 second
        private val JSON = MediaType.parse("application/json; charset=utf-8")
        private const val INIT_URL = BuildConfig.API_ENDPOINT + "checkout/prepare"
        private const val CHECKOUT_URL = BuildConfig.API_ENDPOINT + "checkout/retrieve"
        private const val INVOICE_URL = BuildConfig.API_ENDPOINT + "checkout/invoice"
        private const val CARD_PAYMENT_URL = BuildConfig.API_ENDPOINT + "checkout/payment"
        private const val CARD_PAYMENT_CONFIRM_URL = BuildConfig.API_ENDPOINT + "checkout/verify"
    }

    private var okClient: OkHttpClient
    private var moshi = Moshi.Builder().build()
    var invoiceCancelled = false

    init {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1
        val okhttpClientBuilder = OkHttpClient.Builder()
        okhttpClientBuilder.dispatcher(dispatcher)
        okhttpClientBuilder.addInterceptor(loggingInterceptor())
        okhttpClientBuilder
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
        okClient = okhttpClientBuilder.build()
    }

    private fun loggingInterceptor(): Interceptor {
        val logging = HttpLoggingInterceptor()
        if (logs)
            logging.level = HttpLoggingInterceptor.Level.BODY
        else
            logging.level = HttpLoggingInterceptor.Level.NONE
        return logging
    }

    fun sendInitialRequest(
        serviceId: Long, merchantId: Long,
        amount: Double, transactionParam: String?, communalParam: String?,
        merchantUserId: Long, language: String, listener: ResponseListener<InitialResponse>
    ) {
        val initRequest = InitialRequest(
            serviceId,
            merchantId,
            amount,
            transactionParam,
            communalParam,
            "",
            merchantUserId,
            language,
            ""
        )
        val adapter = moshi.adapter<InitialRequest>(InitialRequest::class.java)
        val body = RequestBody.create(JSON, adapter.toJson(initRequest))
        val request = Request.Builder()
            .url(INIT_URL)
            .addHeader("Accept", "application/json")
            .addHeader("Content-type", "application/json")
            .post(body)
            .build()

        okClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {

                    if (response.body() == null) {
                        listener.onFailure(ServerNotAvailableException(response.code(), response.message()))
                        return
                    }

                    response.body()?.let {
                        val initialResponse = moshi.adapter<InitialResponse>(InitialResponse::class.java)
                            .fromJson(it.string())

                        when (initialResponse?.errorCode) {
                            0 -> {
                                listener.onSuccess(initialResponse)
                            }
                            else -> {
                                listener.onFailure(
                                    ErrorUtils.getException(
                                        initialResponse?.errorCode,
                                        initialResponse?.errorNote
                                    )
                                )
                            }
                        }
                    }

                } else {
                    listener.onFailure(
                        ServerNotAvailableException(response.code(), response.message())
                    )
                }
            }
        })
    }


    fun checkPaymentByRequestIdContinuously(requestId: String, listener: ResponseListener<CheckoutResponse>) {
        val request = Request.Builder()
            .url("$CHECKOUT_URL/$requestId")
            .addHeader("Accept", "application/json")
            .addHeader("Content-type", "application/json")
            .get()
            .build()

        okClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {

                    if (response.body() == null) {
                        listener.onFailure(ServerNotAvailableException(response.code(), response.message()))
                        return
                    }

                    response.body()?.let {
                        val checkoutResponse = moshi.adapter<CheckoutResponse>(CheckoutResponse::class.java)
                            .fromJson(it.string())
                        if (checkoutResponse?.payment != null) {
                            when {
                                checkoutResponse.payment.paymentStatus < 0 -> {
                                    listener.onSuccess(checkoutResponse)
                                }
                                checkoutResponse.payment.paymentStatus == 0 || checkoutResponse.payment.paymentStatus == 1 -> {
                                    if (!invoiceCancelled) {
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            call.clone().enqueue(this)
                                        }, 1000)
                                    } else {
                                        call.cancel()
                                        invoiceCancelled = false
                                    }
                                }
                                checkoutResponse.payment.paymentStatus == 2 -> {
                                    listener.onSuccess(checkoutResponse)
                                }
                            }
                        }
                    }

                } else listener.onFailure(
                    ServerNotAvailableException(response.code(), response.message())
                )
            }
        })
    }

    fun checkPaymentByRequestId(requestId: String, listener: ResponseListener<CheckoutResponse>) {
        val request = Request.Builder()
            .url("$CHECKOUT_URL/$requestId")
            .addHeader("Accept", "application/json")
            .addHeader("Content-type", "application/json")
            .get()
            .build()

        okClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.body() == null) {
                    listener.onFailure(ServerNotAvailableException(response.code(), response.message()))
                    return
                }

                response.body()?.let {
                    if (response.isSuccessful) {
                        val checkoutResponse = moshi.adapter<CheckoutResponse>(CheckoutResponse::class.java)
                            .fromJson(it.string())
                        checkoutResponse?.let {
                            listener.onSuccess(checkoutResponse)
                        }
                    } else listener.onFailure(
                        ServerNotAvailableException(response.code(), response.message())
                    )
                }
            }
        })
    }


    fun paymentByUSSD(requestId: String, phoneNumber: String, listener: ResponseListener<InvoiceResponse>) {

        val invoice = InvoiceRequest(requestId, phoneNumber)

        val adapter = moshi.adapter<InvoiceRequest>(InvoiceRequest::class.java)
        val body = RequestBody.create(JSON, adapter.toJson(invoice))
        val request = Request.Builder()
            .url(INVOICE_URL)
            .addHeader("Accept", "application/json")
            .addHeader("Content-type", "application/json")
            .post(body)
            .build()
        okClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {

                    if (response.body() == null) {
                        listener.onFailure(ServerNotAvailableException(response.code(), response.message()))
                        return
                    }

                    response.body()?.let {

                        val invoiceResponse = moshi.adapter<InvoiceResponse>(InvoiceResponse::class.java)
                            .fromJson(it.string())

                        when (invoiceResponse?.errorCode) {
                            0 -> {
                                listener.onSuccess(invoiceResponse)
                            }
                            else -> {
                                listener.onFailure(
                                    ErrorUtils.getException(
                                        invoiceResponse?.errorCode,
                                        invoiceResponse?.errorNote
                                    )
                                )
                            }
                        }
                    }

                } else {
                    listener.onFailure(
                        ServerNotAvailableException(response.code(), response.message())
                    )
                }
            }
        })
    }

    fun paymentByCard(
        requestId: String,
        cardNumber: String,
        expireDate: String,
        listener: ResponseListener<CardPaymentResponse>
    ) {

        val payment = CardPaymentRequest(requestId, cardNumber, expireDate)

        val adapter = moshi.adapter<CardPaymentRequest>(CardPaymentRequest::class.java)
        val body = RequestBody.create(JSON, adapter.toJson(payment))
        val request = Request.Builder()
            .url(CARD_PAYMENT_URL)
            .addHeader("Accept", "application/json")
            .addHeader("Content-type", "application/json")
            .post(body)
            .build()
        okClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    if (response.body() == null) {
                        listener.onFailure(ServerNotAvailableException(response.code(), response.message()))
                        return
                    }

                    response.body()?.let {

                        val cardPaymentResponse =
                            moshi.adapter<CardPaymentResponse>(CardPaymentResponse::class.java)
                                .fromJson(it.string())
                        when (cardPaymentResponse?.errorCode) {
                            0 -> {
                                listener.onSuccess(cardPaymentResponse)
                            }
                            else -> {
                                listener.onFailure(
                                    ErrorUtils.getException(
                                        cardPaymentResponse?.errorCode,
                                        cardPaymentResponse?.errorNote
                                    )
                                )
                            }
                        }
                    }

                } else listener.onFailure(
                    ServerNotAvailableException(response.code(), response.message())
                )
            }
        })
    }

    fun confirmPaymentByCard(
        requestId: String,
        confirmCode: String,
        listener: ResponseListener<ConfirmPaymentByCardResponse>
    ) {
        val confirm = ConfirmPaymentByCardRequest(requestId, confirmCode)

        val adapter = moshi.adapter<ConfirmPaymentByCardRequest>(ConfirmPaymentByCardRequest::class.java)
        val body = RequestBody.create(JSON, adapter.toJson(confirm))
        val request = Request.Builder()
            .url(CARD_PAYMENT_CONFIRM_URL)
            .addHeader("Accept", "application/json")
            .addHeader("Content-type", "application/json")
            .post(body)
            .build()
        okClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    if (response.body() == null) {
                        listener.onFailure(ServerNotAvailableException(response.code(), response.message()))
                        return
                    }

                    response.body()?.let {

                        val confirmResponse =
                            moshi.adapter<ConfirmPaymentByCardResponse>(ConfirmPaymentByCardResponse::class.java)
                                .fromJson(it.string())
                        when (confirmResponse?.errorCode) {
                            0 -> {
                                listener.onSuccess(confirmResponse)
                            }
                            else -> {
                                listener.onFailure(
                                    ErrorUtils.getException(
                                        confirmResponse?.errorCode,
                                        confirmResponse?.errorNote
                                    )
                                )
                            }
                        }
                    }

                } else listener.onFailure(
                    ServerNotAvailableException(response.code(), response.message())
                )
            }

        })
    }

    fun checkPayment(serviceId: String, paymentId: String, listener: ResponseListener<CheckPaymentResponse>) {
        val request = Request.Builder()
            .url("https://api.click.uz/v2/merchant/payment/status/$serviceId/$paymentId")
            .addHeader("Accept", "application/json")
            .addHeader("Content-type", "application/json")
            .get()
            .build()
        okClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    if (response.body() == null) {
                        listener.onFailure(ServerNotAvailableException(response.code(), response.message()))
                        return
                    }

                    response.body()?.let {

                        val checkResponse =
                            moshi.adapter<CheckPaymentResponse>(CheckPaymentResponse::class.java)
                                .fromJson(it.string())
                        when (checkResponse?.errorCode) {
                            0 -> {
                                listener.onSuccess(checkResponse)
                            }
                            else -> {
                                listener.onFailure(
                                    ErrorUtils.getException(checkResponse?.errorCode, checkResponse?.errorNote)
                                )
                            }
                        }
                    }

                } else listener.onFailure(
                    ServerNotAvailableException(response.code(), response.message())
                )
            }
        })
    }

    fun sendInitialRequestRx(
        serviceId: Long, merchantId: Long,
        amount: Double, transactionParam: String?, communalParam: String?,
        merchantUserId: Long, language: String
    ): Single<InitialResponse> {
        return Single.create<InitialResponse> {
            sendInitialRequest(serviceId, merchantId, amount, transactionParam, communalParam,
                merchantUserId, language,
                object : ResponseListener<InitialResponse> {
                    override fun onFailure(e: Exception) {
                        it.onError(e.cause!!)
                    }

                    override fun onSuccess(response: InitialResponse) {
                        it.onSuccess(response)
                    }
                }
            )
        }
    }

    fun checkPaymentByRequestIdRx(requestId: String): Single<CheckoutResponse> {
        return Single.create<CheckoutResponse> {
            checkPaymentByRequestId(requestId,
                object : ResponseListener<CheckoutResponse> {
                    override fun onFailure(e: Exception) {
                        it.onError(e.cause!!)
                    }

                    override fun onSuccess(response: CheckoutResponse) {
                        it.onSuccess(response)
                    }
                }
            )
        }
    }

    fun paymentByUSSDRx(requestId: String, phoneNumber: String): Single<InvoiceResponse> {
        return Single.create<InvoiceResponse> {
            paymentByUSSD(requestId, phoneNumber,
                object : ResponseListener<InvoiceResponse> {
                    override fun onFailure(e: Exception) {
                        it.onError(e.cause!!)
                    }

                    override fun onSuccess(response: InvoiceResponse) {
                        it.onSuccess(response)
                    }
                }
            )
        }
    }

    fun paymentByCardRx(
        requestId: String,
        cardNumber: String,
        expireDate: String
    ): Single<CardPaymentResponse> {
        return Single.create<CardPaymentResponse> {
            paymentByCard(requestId, cardNumber, expireDate,
                object : ResponseListener<CardPaymentResponse> {
                    override fun onFailure(e: Exception) {
                        it.onError(e.cause!!)
                    }

                    override fun onSuccess(response: CardPaymentResponse) {
                        it.onSuccess(response)
                    }
                }
            )
        }
    }

    fun confirmPaymentByCardRx(
        requestId: String,
        confirmCode: String
    ): Single<ConfirmPaymentByCardResponse> {
        return Single.create<ConfirmPaymentByCardResponse> {
            confirmPaymentByCard(requestId, confirmCode,
                object : ResponseListener<ConfirmPaymentByCardResponse> {
                    override fun onFailure(e: Exception) {
                        it.onError(e.cause!!)
                    }

                    override fun onSuccess(response: ConfirmPaymentByCardResponse) {
                        it.onSuccess(response)
                    }
                }
            )
        }
    }

    fun checkPaymentRx(serviceId: String, paymentId: String): Single<CheckPaymentResponse> {
        return Single.create<CheckPaymentResponse> {
            checkPayment(serviceId, paymentId,
                object : ResponseListener<CheckPaymentResponse> {
                    override fun onFailure(e: Exception) {
                        it.onError(e.cause!!)
                    }

                    override fun onSuccess(response: CheckPaymentResponse) {
                        it.onSuccess(response)
                    }
                }
            )
        }
    }
}