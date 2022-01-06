package wiki.zyue.eou.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.http.CacheControl
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import wiki.zyue.eou.config.security.STATIC_RESOURCE
import java.util.concurrent.TimeUnit


/**
 * 2022/1/3 23:17:45
 * @author echo
 */
@Configuration
class ApplicationConfig: WebFluxConfigurer {

  @Bean
  fun passwordEncoder(): PasswordEncoder =
    PasswordEncoderFactories.createDelegatingPasswordEncoder()

  @Bean
  fun reactiveStringRedisTemplate(factory: ReactiveRedisConnectionFactory) =
    ReactiveStringRedisTemplate(factory)

  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
    registry.addResourceHandler(STATIC_RESOURCE)
      .addResourceLocations("classpath:/static/")
      .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
  }
}