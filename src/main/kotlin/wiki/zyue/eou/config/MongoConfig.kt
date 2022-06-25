package wiki.zyue.eou.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeConvertCallback
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import reactor.core.publisher.Mono
import wiki.zyue.eou.base.BaseEntity
import java.time.LocalDateTime

/**
 * 2021/12/28 05:15:34
 * @author echo
 */
@Configuration
@EnableMongoAuditing
class MongoConfig {

  @Bean
  @Order(99)
  @Suppress("UsePropertyAccessSyntax", "USELESS_CAST")
  fun userAuditingHandler() =
    ReactiveBeforeConvertCallback { entity: Any, _: String ->
      ReactiveSecurityContextHolder
        .getContext()
        .map(SecurityContext::getAuthentication)
        .switchIfEmpty(Mono.just(UsernamePasswordAuthenticationToken("anonymous", "")))
        .map { authentication ->
          val id = (entity as BaseEntity<*>).getId()
          if (id == null) {
            entity.setCreateUser(authentication.name)
            entity.setCreateTime(LocalDateTime.now())
          }
          entity.setModifyUser(authentication.name)
          entity.setModifyTime(LocalDateTime.now())
          entity
        }
    }
}