package wiki.zyue.eou.config

import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeConvertCallback
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import wiki.zyue.eou.base.BaseEntity
import wiki.zyue.eou.base.impl.ExpandRepositoryFactoryBean
import wiki.zyue.eou.base.impl.ExpandRepositoryImpl
import java.time.LocalDateTime

/**
 * 2021/12/28 05:15:34
 * @author echo
 */
@Configuration
//@EnableMongoAuditing
@EnableReactiveMongoRepositories(repositoryFactoryBeanClass = ExpandRepositoryFactoryBean::class)
class MongodbConfig {

//  @Bean
//  @Order(99)
//  @Suppress("UsePropertyAccessSyntax", "USELESS_CAST")
//  fun userAuditingHandler() =
//    ReactiveBeforeConvertCallback { entity: Any, _: String ->
//      JwtInfo.userId().map {
//        val id = (entity as BaseEntity<*>).getId()
//        if (id == null) {
//          entity.setCreateUser(it)
//          entity.setCreateTime(LocalDateTime.now())
//          entity.setModifyTime(LocalDateTime.now())
//        } else entity.setModifyUser(it)
//        if (entity.getOrder() == null) entity.setOrder(1)
//        entity as Any
//      }.defaultIfEmpty(entity)
//    }
}