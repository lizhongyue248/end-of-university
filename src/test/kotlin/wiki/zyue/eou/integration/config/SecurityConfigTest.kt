package wiki.zyue.eou.integration.config

import com.nimbusds.jose.shaded.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import wiki.zyue.eou.config.security.AuthenticationResponse
import wiki.zyue.eou.config.security.AuthenticationType
import wiki.zyue.eou.integration.controller.AbstractControllerTest
import wiki.zyue.eou.repository.cache.AuthenticationResponseRepository
import wiki.zyue.eou.service.AuthService
import java.util.*


/**
 * Security
 *
 * 2022/1/3 23:18:42
 * @author echo
 */
@SpringBootTest
@Import(SecurityControllerMocker::class)
@ExtendWith(RestDocumentationExtension::class)
class SecurityConfigTest : AbstractControllerTest() {

  @MockBean
  private lateinit var authService: AuthService

  @MockBean
  private lateinit var authenticationResponseRepository: AuthenticationResponseRepository

  @BeforeEach
  internal fun mockBean() {
    Mockito.`when`(authService.findByEmailOrPhone(AuthenticationType.EMAIL, "test@123.com", "password"))
      .thenReturn(
        Mono.just(
          User.withUsername("test")
            .password("123456")
            .roles("USER")
            .build()
        )
      )
    Mockito.`when`(authService.authorizationPassword("test", "123456"))
      .thenThrow(UsernameNotFoundException("用户不存在"))
    Mockito.`when`(authenticationResponseRepository.findById(Mockito.any()))
      .thenReturn(Optional.empty())
    Mockito.`when`(authenticationResponseRepository.save(Mockito.any()))
      .thenReturn(AuthenticationResponse("test", "token", 123, listOf("ROLE_USER")))
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
      .jsonPath("expire").isNumber
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

  @Test
  internal fun `No Authorization Test`() {
    rest.get()
      .uri("/test")
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  @WithMockUser
  internal fun `User Authorization Test`() {
    rest.get()
      .uri("/test")
      .exchange()
      .expectStatus()
      .isOk
      .expectBody(String::class.java)
      .isEqualTo("test")
  }
}

@RestController
private class SecurityControllerMocker {

  @GetMapping("/test")
  fun test(): Mono<String> = Mono.just("test")

}