package wiki.zyue.eou.base.impl

import org.springframework.data.domain.Example
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository
import wiki.zyue.eou.base.BaseEntity
import wiki.zyue.eou.base.ExpandRepository

/**
 * The base repository implement.
 *
 * 2021/12/28 05:11:28
 * @author echo
 */
class ExpandRepositoryImpl<E : BaseEntity<E>>(
  private val entityInformation: MongoEntityInformation<E, String>,
  private val reactiveMongoTemplate: ReactiveMongoOperations
) : SimpleReactiveMongoRepository<E, String>(entityInformation, reactiveMongoTemplate),
  ExpandRepository<E> {

  override fun findExample(example: Example<E>, pageable: Pageable) =
    reactiveMongoTemplate.find(
      Query(Criteria().alike(example))
        .collation(entityInformation.collation)
        .with(pageable), example.probeType, entityInformation.collectionName
    )

}