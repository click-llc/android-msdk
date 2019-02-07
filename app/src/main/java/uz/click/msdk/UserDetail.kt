package uz.click.msdk

data class UserDetail(
    val id: Long,
    var requestId: String,
    var paymentId: Long?,
    var paid: Boolean
)