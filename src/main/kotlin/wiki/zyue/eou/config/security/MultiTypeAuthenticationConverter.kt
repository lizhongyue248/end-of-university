package wiki.zyue.eou.config.security

import org.springframework.core.ResolvableType
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * 2022/1/3 01:12:51
 * @author echo
 */
class MultiTypeAuthenticationConverter(
  private val serverCodecConfigurer: ServerCodecConfigurer
): ServerAuthenticationConverter {

  private val loginParam = ResolvableType.forClass(LoginParam::class.java)

  override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
    val request = exchange.request
    return serverCodecConfigurer.readers.stream()
      .filter { reader -> reader.canRead(loginParam, MediaType.APPLICATION_JSON) }
      .findFirst()
      .orElseThrow { IllegalStateException("No JSON reader for UsernamePasswordContent") }
      .readMono(loginParam, request, emptyMap())
      .cast(LoginParam::class.java)
      .map { param ->
        MultiTypeAuthenticationToken(
          param.username,
          param.password
        ).setType(param.type)
      }
  }

}