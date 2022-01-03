package wiki.zyue.eou.config.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import reactor.core.publisher.Mono

/**
 * 2022/1/3 00:59:35
 * @author echo
 */
class AuthenticationFailureHandler: ServerAuthenticationFailureHandler {
  override fun onAuthenticationFailure(
    webFilterExchange: WebFilterExchange,
    exception: AuthenticationException
  ): Mono<Void> {
    val exchange = webFilterExchange.exchange
    if (exception is UsernameNotFoundException) {
      exchange.response.statusCode = HttpStatus.BAD_REQUEST
    }
    return Mono.empty()
  }
}