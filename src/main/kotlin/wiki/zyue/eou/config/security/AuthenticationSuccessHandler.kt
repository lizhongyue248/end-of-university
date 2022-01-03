package wiki.zyue.eou.config.security

import org.apache.commons.logging.LogFactory
import org.springframework.core.ResolvableType
import org.springframework.core.codec.Hints
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 2022/1/3 00:59:35
 * @author echo
 */
class AuthenticationSuccessHandler : ServerAuthenticationSuccessHandler {
  private val logger = LogFactory.getLog(this::class.java)

  override fun onAuthenticationSuccess(
    webFilterExchange: WebFilterExchange,
    authentication: Authentication
  ): Mono<Void> {
    val (token, expirationTime) = JwtUtil.encode(authentication, mapOf("a" to "b"))
    val expiration = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(expirationTime)
    logger.info("${authentication.name} 登录成功, 有效期至 $expiration")
    val result = AuthenticationResponse(
      authentication.name,
      token,
      toTimestamp(expirationTime),
      authoritiesToList(authentication.authorities)
    )
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

  private fun toTimestamp(expirationTime: LocalDateTime) =
    expirationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

  private fun authoritiesToList(authorities: MutableCollection<out GrantedAuthority>) =
    authorities.map(GrantedAuthority::getAuthority)
}