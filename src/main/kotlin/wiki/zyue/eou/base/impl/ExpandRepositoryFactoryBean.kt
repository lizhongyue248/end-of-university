package wiki.zyue.eou.base.impl

import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactoryBean
import org.springframework.data.repository.Repository
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import java.io.Serializable
import wiki.zyue.eou.base.BaseEntity

/**
 * This bean will be injected in [wiki.zyue.eou.config.MongodbConfig].
 * It will give a [wiki.zyue.eou.base.ExpandRepository] implement and
 * a [ExpandMongoRepositoryFactory] to create repository.
 *
 * 2021/12/28 05:13:25
 * @author echo
 */
class ExpandRepositoryFactoryBean<T : Repository<E, String>, E : BaseEntity<E>>(
  repositoryInterface: Class<out T>
) : ReactiveMongoRepositoryFactoryBean<T, E, String>(repositoryInterface) {

  /**
   * Get customize factory instance.
   */
  override fun getFactoryInstance(operations: ReactiveMongoOperations): RepositoryFactorySupport =
    ExpandMongoRepositoryFactory<E>(operations)

  @Suppress("UNCHECKED_CAST")
  private class ExpandMongoRepositoryFactory<E : BaseEntity<E>>(
    private val reactiveMongoTemplate: ReactiveMongoOperations
  ) : ReactiveMongoRepositoryFactory(reactiveMongoTemplate) {

    /**
     * Get our customize repository base class.
     */
    override fun getRepositoryBaseClass(metadata: RepositoryMetadata): Class<*> = ExpandRepositoryImpl::class.java

    /**
     * Get target repository.
     */
    override fun getTargetRepository(information: RepositoryInformation): Any {
      val entityInformation: MongoEntityInformation<*, Serializable> = getEntityInformation(information.domainType)
      return ExpandRepositoryImpl(entityInformation as MongoEntityInformation<E, String>, reactiveMongoTemplate)
    }

  }
}