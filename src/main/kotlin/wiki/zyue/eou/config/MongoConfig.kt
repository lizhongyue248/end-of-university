package wiki.zyue.eou.config

import org.springframework.context.annotation.Configuration

/**
 * 2021/12/28 05:15:34
 * @author echo
 */
@Configuration
//@EnableMongoAuditing
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