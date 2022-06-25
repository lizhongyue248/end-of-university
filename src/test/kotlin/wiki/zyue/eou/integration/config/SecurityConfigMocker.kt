package wiki.zyue.eou.integration.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * 2022/6/25 15:26:26
 * @author echo
 */
@TestConfiguration
class SecurityConfigMocker {

  @Bean
  @Suppress("DEPRECATION")
  fun passwordEncoder(): PasswordEncoder {
    val encodingId = "noop"
    val encoders = mapOf<String, PasswordEncoder>(encodingId to NoOpPasswordEncoder.getInstance())
    return DelegatingPasswordEncoder(encodingId, encoders)
  }

}