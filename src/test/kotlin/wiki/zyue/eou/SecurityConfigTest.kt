package wiki.zyue.eou

import com.nimbusds.jose.shaded.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import wiki.zyue.eou.config.SecurityConfig
import wiki.zyue.eou.config.security.AuthenticationType
import wiki.zyue.eou.service.AuthService
import wiki.zyue.eou.service.AuthServiceImpl


/**
 * 2022/1/3 23:18:42
 * @author echo
 */
@WebFluxTest
@ContextConfiguration(classes = [EouApplication::class])
@Import(SecurityConfig::class, SecurityBeanMocker::class)
class SecurityConfigTest {

  @Autowired
  lateinit var context: ApplicationContext

  lateinit var rest: WebTestClient

  @BeforeEach
  internal fun setUp() {
    rest = WebTestClient.bindToApplicationContext(context)
      .configureClient()
      .build()
  }

  @Test
  internal fun `Login Success Test`() {
    val params = JSONObject()
    params["username"] = "test@123.com"
    params["password"] = "password"
    params["type"] = "EMAIL"
    rest.post()
      .uri("/login")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(params))
      .exchange()
      .expectBody()
      .jsonPath("token").exists()
      .jsonPath("username").isEqualTo("test")
      .jsonPath("authorities").isArray
      .jsonPath("authorities[0]").isEqualTo("ROLE_USER")
  }

  @Test
  internal fun `Login Fail Test When User Can Not Found`() {
    val params = JSONObject()
    params["username"] = "test"
    params["password"] = "123456"
    params["type"] = "PASSWORD"
    rest.post()
      .uri("/login")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(params))
      .exchange()
      .expectStatus()
      .is4xxClientError
  }
}

@Configuration
class SecurityBeanMocker {

  @Bean
  fun passwordEncoder(): PasswordEncoder =
    PasswordEncoderFactories.createDelegatingPasswordEncoder()

  @Bean
  fun authService(): AuthService {
    val mock = Mockito.mock(AuthServiceImpl::class.java)
    Mockito.`when`(mock.findByEmailOrPhone(AuthenticationType.EMAIL, "test@123.com", "password"))
      .thenReturn(
        Mono.just(
          User.withUsername("test")
            .password("123456")
            .roles("USER")
            .build()
        )
      )
    Mockito.`when`(mock.findByUsername("test", "123456"))
      .thenThrow(UsernameNotFoundException("用户不存在"))
    return mock
  }

}