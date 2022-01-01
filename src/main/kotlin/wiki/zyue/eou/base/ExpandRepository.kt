package wiki.zyue.eou.base

import org.springframework.data.domain.Example
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.NoRepositoryBean
import reactor.core.publisher.Flux

/**
 * 2021/12/28 05:09:30
 * @author echo
 */
@NoRepositoryBean
interface ExpandRepository<E: BaseEntity<E>>: ReactiveMongoRepository<E, String> {

  /**
   * Find all by [example] and [pageable].
   */
  fun findExample(example: Example<E>, pageable: Pageable): Flux<E>

}