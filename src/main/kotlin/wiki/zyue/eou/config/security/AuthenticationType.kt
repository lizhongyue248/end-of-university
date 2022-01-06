package wiki.zyue.eou.config.security

/**
 * 2022/1/2 22:56:58
 * @author echo
 */
enum class AuthenticationType {
  PHONE,
  EMAIL,
  PASSWORD;

  fun isEmail(): Boolean = this == EMAIL

  fun isPhone(): Boolean = this == PHONE

  fun isPassword(): Boolean = this == PASSWORD

}

data class LoginParam(
  var username: String,
  var password: String,
  var type: AuthenticationType
)

data class AuthenticationResponse(
  var username: String,
  var token: String,
  var expire: Long,
  val authorities: List<String>
)

const val LOGIN_URL = "/login"
const val REGISTER_URL = "/register"
const val CODE_URL = "/code/**"
const val DEFAULT_ROLE = "USER"
const val STATIC_RESOURCE = "/static/**"