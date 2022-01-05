package wiki.zyue.eou.base

import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.data.repository.NoRepositoryBean
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * 2021/12/28 05:10:32
 * @author echo
 */
@NoRepositoryBean
interface BaseRepository<E : BaseEntity<E>> : ExpandRepository<E>,
  ReactiveQuerydslPredicateExecutor<E> {

  fun findByIdAndIsEnableIsTrue(id: String): Mono<E>

  fun findAllByIdContaining(ids: List<String>): Flux<E>

  fun findFirstByName(name: String): Mono<E>

}