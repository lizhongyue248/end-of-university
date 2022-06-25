package wiki.zyue.eou.integration.config

import com.nimbusds.jose.shaded.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestExecutionListeners
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import wiki.zyue.eou.integration.DataSourceTestExecutionListener
import wiki.zyue.eou.integration.MongoDatabaseInit
import wiki.zyue.eou.integration.controller.AbstractControllerTest


/**
 * Security
 *
 * 2022/1/3 23:18:42
 * @author echo
 */
@Import(SecurityControllerMocker::class, SecurityConfigMocker::class)
@TestExecutionListeners(
  listeners = [DataSourceTestExecutionListener::class],
  mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension::class)
@MongoDatabaseInit("integration/config/")
class SecurityConfigTest : AbstractControllerTest() {

  @Test
  internal fun `Login Success Test`() {
    val params = JSONObject()
    params["username"] = "201607090217"
    params["password"] = "123456"
    rest.post()
      .uri("/login")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(params))
      .exchange()
      .expectStatus()
      .is2xxSuccessful
      .expectBody()
      .jsonPath("token").exists()
      .jsonPath("username").isEqualTo("201607090217")
      .jsonPath("roles").isArray
      .jsonPath("roles[0]").isEqualTo("ROLE_STUDENT")
      .jsonPath("expire").isNumber
  }

  @Test
  internal fun `Login Fail Test When User Can Not Found`() {
    val params = JSONObject()
    params["username"] = "201607092117"
    params["password"] = "123456"
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