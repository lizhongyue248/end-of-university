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