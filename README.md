[ ![Download](https://api.bintray.com/packages/click-llc/Click_Android_SDK/click_android_msdk_uzbekistan/images/download.svg?version=0.0.3-alpha) ](https://bintray.com/click-llc/Click_Android_SDK/click_android_msdk_uzbekistan/0.0.3-alpha/link)

# Click Mobile SDK 

Эта библиотека позволяет встроить прием платежей с помощью Click в мобильные приложения на Android. Для правильного функционирования библиотеки, пользователь должен быть подключен к Click Merchant по схеме Shop API.

<img src="/screens/2.png" width="30%"> <img src="/screens/3.png" width="30%">

### Подключение через Gradle
Для подключения библиотеки пропишите зависимости в build.gradle модуля:

```groovy

dependencies {
	implementation 'uz.click.mobilesdk:clickmsdk:${last.version}'
}

```
### Необходимые разрешения

Следующий разрешение необходимо указать в `AndroidManifest` файле 

```xml
  <uses-permission android:name="android.permission.INTERNET" />
```

### Использование библиотеки

Для начала работы с диалогом приема платежей Click надо обратиться к классу `ClickMerchant`. В `ClickMerchant` передается класс конфигурации `ClickMerchantConfig`

Поля `ClickMerchantConfig`:

Обязательные:
* serviceId (Long) - идентификатор сервиса или товара поставщика
* merchantId (Long) - идентификатор поставщика
* merchantUserId (Long) - идентификатор пользователя в системе поставщиков
* amount (Double) - сумма оплаты
* locale (String) - локализация языка диалога ("UZ", "RU", "EN")

Необязательные:
* productName (String) - название товара
* productDescription (String) - описание товара
* requestId (String) - идентификатор платежа. С помощью него можно вывести на диалог результаты оплаты с таким идентификатором
* transactionParam (String) – параметр транзакции, характеризующий за что платить клиент
* communalParam (String) – допольнительный параметр
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
Для того, чтобы вызвать диалог надо использовать следующий метод:

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

Для работы с библиотекой вам нужно получить идентификатор платежа.

Чтобы получить идентификатор платежа надо отправить инициализирующий запрос:

```java
public void sendInitialRequest(
    Long serviceId, Long merchantId,
    Double amount, String transactionParam, String communalParam,
    Long merchantUserId, String language, ResponseListener<InitialResponse> listener
)
```
Этот запрос возвращает идентификатор платежа(`requestId`), с помощью которого будет выполнятеся почти все последующие операции.

### Выставление счета

С помощью этой библиотеки можно выставить счет двумя способами. Первый способ выставить счет по мобильному номеру, второй способ выставить счет по банковской карте.

Выставление счета по мобильному номеру:
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

