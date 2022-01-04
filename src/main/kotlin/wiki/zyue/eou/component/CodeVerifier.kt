package wiki.zyue.eou.component

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.getAndAwait
import org.springframework.stereotype.Component
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
    val result = reactiveStringRedisTemplate.opsForValue().getAndAwait(key) == code
    if (result) reactiveStringRedisTemplate.delete(key).awaitSingle()
    return result
  }

}