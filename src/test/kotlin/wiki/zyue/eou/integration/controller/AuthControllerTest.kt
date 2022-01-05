package wiki.zyue.eou.integration.controller

import com.nimbusds.jose.shaded.json.JSONObject
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.test.StepVerifier

/**
 * 2022/1/5 11:37:22
 * @author echo
 */
@SpringBootTest
internal class AuthControllerTest {

  @Autowired
  lateinit var context: ApplicationContext

  @Autowired
  lateinit var reactiveStringRedisTemplate: ReactiveStringRedisTemplate

  lateinit var rest: WebTestClient

  @BeforeEach
  internal fun setUp() {
    rest = WebTestClient.bindToApplicationContext(context)
      .configureClient()
      .build()
  }

  private fun code(pathVariable: String, key: String) {
    rest.get()
      .uri("/code${pathVariable}")
      .exchange()
      .expectStatus()
      .isNoContent
    val cache = reactiveStringRedisTemplate.opsForValue().get(key)
    StepVerifier.create(cache)
      .assertNext { it != null && it.isNotEmpty() }
      .verifyComplete()
  }

  @Test
  fun `Get Code With Email To Register`() {
    code("/register/123@123.com/EMAIL", "EMAIL-123@123.com")
  }

  @Test
  fun `Get Code With Phone To Register`() {
    code("/register/13415151515/PHONE", "PHONE-13415151515")
  }

  @Test
  fun `Register Success Test`() {
    runBlocking {
      val params = JSONObject()
      params["name"] = "Amh"
      params["authentication"] = "ambert_shopemhz@announces.ges"
      params["password"] = "123456"
      params["rePassword"] = "123456"
      params["type"] = "EMAIL"
      params["code"] = "123456"
      reactiveStringRedisTemplate.opsForValue()
        .set("EMAIL-ambert_shopemhz@announces.ges", "123456")
        .awaitSingle()
      rest.post()
        .uri("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(params))
        .exchange()
        .expectStatus()
        .isNoContent
    }
  }

  @Test
  fun `Register Fail Test When Code Error`() {
    runBlocking {
      val params = JSONObject()
      params["name"] = "Amh"
      params["authentication"] = "ambert_shopemhz@announces.ges"
      params["password"] = "123456"
      params["rePassword"] = "123456"
      params["type"] = "EMAIL"
      params["code"] = "1111"
      rest.post()
        .uri("/register")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(params))
        .exchange()
        .expectStatus()
        .isBadRequest
        .expectBody()
        .jsonPath("message").isNotEmpty
        .jsonPath("stack").isArray
    }
  }

}
