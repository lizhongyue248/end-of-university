package wiki.zyue.eou.config.security

import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsChecker
import org.springframework.security.core.userdetails.UsernameNotFoundException
import reactor.core.publisher.Mono
import wiki.zyue.eou.service.AuthService
import javax.naming.AuthenticationNotSupportedException

/**
 * 2022/1/2 16:30:18
 * @author echo
 */
class MultiTypeAuthenticationManager(
  private val authService: AuthService
) : ReactiveAuthenticationManager {
  private val userDetailsChecker: UserDetailsChecker = AccountStatusUserDetailsChecker()

  override fun authenticate(authentication: Authentication): Mono<Authentication> {
    if (!supports(authentication)) {
      return Mono.error { AuthenticationNotSupportedException("Do not support authentication type.") }
    }
    val token = authentication as MultiTypeAuthenticationToken
    val userDetailsMono = when (token.getType()) {
      AuthenticationType.PHONE, AuthenticationType.EMAIL -> authService.findByEmailOrPhone(
        token.getType(),
        token.principal.toString(),
        token.credentials.toString()
      )
      else -> authService.authorizationPassword(token.principal.toString(), token.credentials.toString())
    }
    return userDetailsMono.switchIfEmpty(Mono.error { UsernameNotFoundException("Authentication user not found") })
      .doOnNext(userDetailsChecker::check)
      .map { userDetails ->
        val authenticationToken = MultiTypeAuthenticationToken(
          userDetails,
          authentication.credentials,
          userDetails.authorities
        )
        authenticationToken.details = token.details
        return@map authenticationToken
      }
  }

  private fun supports(authentication: Authentication): Boolean {
    return MultiTypeAuthenticationToken::class.java.isAssignableFrom(authentication.javaClass)
  }
}