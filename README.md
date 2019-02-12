[ ![Download](https://api.bintray.com/packages/click-llc/Click_Android_SDK/click_android_msdk_uzbekistan/images/download.svg?version=0.0.5-alpha) ](https://bintray.com/click-llc/Click_Android_SDK/click_android_msdk_uzbekistan/0.0.5-alpha/link)

# Click Mobile SDK 

Эта библиотека позволяет встроить прием платежей с помощью Click в мобильные приложения на Android. Для правильного функционирования библиотеки, пользователь должен быть подключен к Click Merchant по схеме Shop API.

<img src="/screens/2.png" width="30%"> <img src="/screens/3.png" width="30%">

В этом репозитории лежит код Click Mobile SDK и пример приложения, которое его интегрирует.
* [Код библиотеки](https://github.com/click-llc/android-msdk/tree/master/mobilesdk)
* [Код демо-приложения, которое интегрирует Click Mobile SDK](https://github.com/click-llc/android-msdk/tree/master/app)

### Подключение через Gradle
Для подключения библиотеки необходимо прописать зависимости в build.gradle модуля:

```groovy

dependencies {
	implementation 'uz.click.mobilesdk:clickmsdk:${last.version}'
}

```
### Необходимые разрешения

Следующее разрешение необходимо указать в `AndroidManifest` 

```xml
  <uses-permission android:name="android.permission.INTERNET" />
```

### Использование библиотеки

Для начала работы с диалогом приема платежей CLICK надо обратиться к классу `ClickMerchant`. В `ClickMerchant` передается класс конфигурации `ClickMerchantConfig`

Поля `ClickMerchantConfig`:

Обязательные:
* serviceId (Long) - идентификатор сервиса или товара поставщика
* merchantId (Long) - идентификатор поставщика
* merchantUserId (Long) - идентификатор мерчанта в системе поставщиков
* amount (Double) - сумма оплаты
* locale (String) - локализация языка диалога ("UZ", "RU", "EN")

Необязательные:
* productName (String) - название товара
* productDescription (String) - описание товара
* requestId (String) - идентификатор платежа. Используется, если необходимо проверка статуса платежа. 
* transactionParam (String) – параметр транзакции, характеризующий за что платить клиент
* communalParam (String) – дополнительный параметр
* paymentOption (PaymentOptionEnum) – способ оплаты (USSD, BANK_CARD)

Пример:

```java
   ClickMerchantConfig config = ClickMerchantConfig.Builder()
            .serviceId(12345)
            .merchantId(12345)
            .amount(50000.0)
            .locale("UZ")
            .option(PaymentOptionEnum.USSD)
            .productName("Название продкута или услуги")
            .productDescription("Описание продукта или услуги")
            .merchantUserId(12345)
            .build()
```
Для того, чтобы вызвать диалог оплаты с помощью CLICK, надо использовать следующий метод:

```java
ClickMerchant.init(supportFragmentManager, config,
            new ClickMerchantListener {
                @Override
                void onRequestIdGet(String requestId) {

                }

                @Override 
                void onFailure() {
	   
                }

                @Override
                void onSuccess(Long paymentId) {

                }
                
                @Override
                void onInvoiceCancelled() {

                }
                
            }
)
```

`onRequestIdGet` - срабатывает при успешном получении идентификатора платежа с сервера

`onFailure` - срабатывает при неуспешной оплате счета

`onSuccess` - срабатывает при успешной оплате счета

`onInvoiceCancelled` - срабатывает при отмене выставленного счета

Примеры использования:

<img src="/screens/1.png" width="15%"> <img src="/screens/2.png" width="15%"> <img src="/screens/3.png" width="15%"> <img src="/screens/4.png" width="15%"> <img src="/screens/5.png" width="15%">

### Документация Click Merchant Manager

Для работы с библиотекой необходимо получить идентификатор платежа.

Чтобы получить идентификатор платежа, надо отправить инициализирующий запрос:

```java
public void sendInitialRequest(
    Long serviceId, Long merchantId,
    Double amount, String transactionParam, String communalParam,
    Long merchantUserId, String language, ResponseListener<InitialResponse> listener
)
```
Этот запрос возвращает идентификатор платежа(`requestId`), который после будет использоваться для проведения платежа

### Выставление счета

С помощью этой библиотеки можно выставить счет двумя способами. Первый способ выставить счет по телефону номеру в системе CLICK, второй способ выставить счет по банковской карте.

Выставление счета по телефону номеру в системе CLICK:
```java
public void paymentByUSSD(String requestId, String phoneNumber, ResponseListener<InvoiceResponse> listener) 
```

Выставление счета по банковской карте:
```java
public void paymentByCard(String requestId, String cardNumber, String expireDate,  ResponseListener<InvoiceResponse> listener) 
```

### Подтверждение платежа

Выставленный счет по банковскей карте надо будет подтвердить с помощью SMS-кода.

```java
public void confirmPaymentByCard(
        String requestId,
        String confirmCode,
        ResponseListener<ConfirmPaymentByCardResponse> listener
)
```

### Проверка статуса платежа

Зная идентификатор платежа, можно узнать его статус

```java
public void checkPaymentByRequestId(String requestId, ResponseListener<CheckoutResponse> listener) 
```
Возможные статусы:

| Значение      |   Описание    | 
| ------------- |:-------------:|
| < 0     | ошибка |
| 0     | платеж создан      | 
| 1 | обрабатывается      |
| 2 | успешно оплачен      |

Для каждого метода существует аналог с поддержкой `RxJava`
