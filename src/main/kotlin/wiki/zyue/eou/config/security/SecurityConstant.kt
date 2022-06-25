package wiki.zyue.eou.config.security

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

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

@RedisHash("token", timeToLive = 604800)
data class AuthenticationResponse(
  @Id
  var token: String,
  var username: String,
  var expire: Long,
  val roles: List<String>
)

const val LOGIN_URL = "/login"
const val REGISTER_URL = "/register"
const val CODE_URL = "/code/**"
const val STATIC_RESOURCE = "/docs/**"

const val DEFAULT_ROLE = "USER"
