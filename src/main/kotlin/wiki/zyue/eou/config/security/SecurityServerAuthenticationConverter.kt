package wiki.zyue.eou.config.security

import org.springframework.core.ResolvableType
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import wiki.zyue.eou.model.request.LoginRequest

/**
 * The class will [convert] request to out custom data class from Json String.
 *
 * 2022/6/12 20:14:09
 * @author echo
 */
class SecurityServerAuthenticationConverter(
  private val serverCodecConfigurer: ServerCodecConfigurer
) : ServerAuthenticationConverter {

  private val loginParam = ResolvableType.forClass(LoginRequest::class.java)


  override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
    val request = exchange.request
    return serverCodecConfigurer.readers.stream()
      .filter { reader -> reader.canRead(loginParam, MediaType.APPLICATION_JSON) }
      .findFirst()
      .orElseThrow { IllegalStateException("No JSON reader for UsernamePasswordContent") }
      .readMono(loginParam, request, emptyMap())
      .cast(LoginRequest::class.java)
      .doOnError { throw IllegalStateException("Login Param error ${it.message}.") }
      .map {
        UsernamePasswordAuthenticationToken(
          it.username,
          it.password
        )
      }
  }
}