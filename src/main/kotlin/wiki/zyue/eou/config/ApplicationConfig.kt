package wiki.zyue.eou.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * 2022/1/3 23:17:45
 * @author echo
 */
@Configuration
class ApplicationConfig {

  @Bean
  fun passwordEncoder(): PasswordEncoder =
    PasswordEncoderFactories.createDelegatingPasswordEncoder()

  @Bean
  fun reactiveStringRedisTemplate(factory: ReactiveRedisConnectionFactory) =
    ReactiveStringRedisTemplate(factory)

}