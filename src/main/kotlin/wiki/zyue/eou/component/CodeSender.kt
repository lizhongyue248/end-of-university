package wiki.zyue.eou.component

import org.apache.commons.logging.LogFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

/**
 * 2022/1/4 23:04:29
 * @author echo
 */
abstract class CodeSender(
  private val reactiveStringRedisTemplate: ReactiveStringRedisTemplate
) {
  companion object {
    private const val DEFAULT_TTL = 30L
  }

  private val logger = LogFactory.getLog(this::class.java)

  /**
   * Real send action To [target].
   */
  protected abstract fun sendAction(target: String): Mono<Void>

  /**
   * Send Type.
   */
  abstract fun getType(): String

  /**
   * How to generate code.
   */
  protected open fun generateCode(): String = "${(100000..999999).random()}"

  /**
   * Duration timeout.
   */
  protected open fun timeout(): Duration = Duration.ofMinutes(DEFAULT_TTL)

  /**
   * It will save in redis and call [sendAction].
   */
  fun send(target: String): Mono<Void> {
    val code = generateCode()
    logger.debug("Send $code to $target")
    return reactiveStringRedisTemplate.opsForValue()
      .set("${getType()}-$target", code, timeout())
      .flatMap { sendAction(target) }
  }

}

@Component
class PhoneCodeSender(
  reactiveStringRedisTemplate: ReactiveStringRedisTemplate
) : CodeSender(reactiveStringRedisTemplate) {

  override fun getType() = "PHONE"

  override fun sendAction(target: String): Mono<Void> {
    // TODO: Really Send
    return Mono.empty()
  }

}

@Component
class EmailCodeSender(
  reactiveStringRedisTemplate: ReactiveStringRedisTemplate
) : CodeSender(reactiveStringRedisTemplate) {

  override fun getType() = "EMAIL"

  override fun generateCode(): String =
    "${('A'..'Z').random()}${('A'..'Z').random()}${super.generateCode()}"

  override fun sendAction(target: String): Mono<Void> {
    // TODO: Really Send
    return Mono.empty()
  }

}