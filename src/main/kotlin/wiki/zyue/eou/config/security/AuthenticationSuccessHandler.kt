package wiki.zyue.eou.config.security

import org.apache.commons.logging.LogFactory
import org.springframework.core.ResolvableType
import org.springframework.core.codec.Hints
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import reactor.core.publisher.Mono
import java.text.SimpleDateFormat
import java.util.*

/**
 * 2022/1/3 00:59:35
 * @author echo
 */
class AuthenticationSuccessHandler(
  private val authorizationToken: AuthorizationToken
) : ServerAuthenticationSuccessHandler {
  private val logger = LogFactory.getLog(this::class.java)

  override fun onAuthenticationSuccess(webFilterExchange: WebFilterExchange, authentication: Authentication): Mono<Void> {
    val result = authorizationToken.buildAuthenticationResponse(authentication)
    val expiration = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(result.expire))
    logger.info("${authentication.name} 登录成功, 有效期至 $expiration")
    val exchange = webFilterExchange.exchange
    exchange.response.headers.contentType = MediaType.APPLICATION_JSON
    return exchange.response.writeWith(
      Jackson2JsonEncoder().encode(
        Mono.just(result),
        exchange.response.bufferFactory(),
        ResolvableType.forInstance(result),
        MediaType.APPLICATION_JSON,
        Hints.from(Hints.LOG_PREFIX_HINT, exchange.logPrefix)
      )
    )
  }

}