package wiki.zyue.eou.unit

import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.lang.Exception

/**
 * 2022/1/5 17:41:52
 * @author echo
 */
fun <T> monoNotNull(mono: Mono<T>) =
  StepVerifier.create(mono)
    .expectNextMatches { it != null }
    .verifyComplete()

fun <T> monoExpectException(mono: Mono<T>, exception: Class<out Exception>) =
  StepVerifier.create(mono)
    .expectError(exception)
    .verify()