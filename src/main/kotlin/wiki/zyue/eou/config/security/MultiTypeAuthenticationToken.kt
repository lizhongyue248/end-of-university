package wiki.zyue.eou.config.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.util.Assert

/**
 * 2022/1/2 18:34:08
 * @author echo
 */
class MultiTypeAuthenticationToken(
  private val principal: Any,
  private val credentials: Any,
  grantedAuthorities: Collection<GrantedAuthority>? = null
) : AbstractAuthenticationToken(grantedAuthorities), Authentication {

  private lateinit var type: AuthenticationType

  init {
    if (grantedAuthorities == null) {
      Assert.isTrue(
        !isAuthenticated,
        "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead"
      )
      super.setAuthenticated(false)
    } else {
      super.setAuthenticated(true)
    }
  }

  override fun getCredentials() = credentials

  override fun getPrincipal() = principal

  fun setType(type: AuthenticationType): MultiTypeAuthenticationToken {
    this.type = type
    return this
  }

  fun getType() = this.type
}