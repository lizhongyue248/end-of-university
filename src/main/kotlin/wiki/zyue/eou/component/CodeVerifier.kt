package wiki.zyue.eou.component

import kotlinx.coroutines.reactor.mono
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.getAndAwait
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import wiki.zyue.eou.config.security.AuthenticationType
import wiki.zyue.eou.model.type.CodeType

/**
 * 2022/1/5 00:11:46
 * @author echo
 */
@Component
class CodeVerifier(
  private val reactiveStringRedisTemplate: ReactiveStringRedisTemplate
) {

  suspend fun check(target: String, code: String, type: CodeType): Boolean {
    val key = "${type}-$target"
    //    if (result) reactiveStringRedisTemplate.delete(key).awaitSingle()
    return reactiveStringRedisTemplate.opsForValue().getAndAwait(key) == code
  }

  fun check(target: String, code: String, authenticationType: AuthenticationType): Mono<Boolean> =
    mono {
      val codeType = if (authenticationType.isEmail()) CodeType.EMAIL
      else CodeType.PHONE
      check(target, code, codeType)
    }

}