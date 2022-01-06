package wiki.zyue.eou.integration.controller

import com.nimbusds.jose.shaded.json.JSONObject
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.test.StepVerifier
import wiki.zyue.eou.config.security.AuthenticationType
import wiki.zyue.eou.integration.*

/**
 * 2022/1/5 11:37:22
 * @author echo
 */
@SpringBootTest
@ExtendWith(RestDocumentationExtension::class)
internal class AuthControllerTest {

  @Autowired
  lateinit var reactiveStringRedisTemplate: ReactiveStringRedisTemplate

  lateinit var rest: WebTestClient

  @BeforeEach
  internal fun setUp(
    context: ApplicationContext, restDocumentation: RestDocumentationContextProvider
  ) {
    rest = WebTestClient.bindToApplicationContext(context).configureClient()
      .baseUrl("https://eou.zyue.wiki").filter(
        documentationConfiguration(restDocumentation)
          .operationPreprocessors()
          .withRequestDefaults(prettyPrint())
          .withResponseDefaults(prettyPrint())
      ).build()
  }

  private fun code(action: String, authentication: String, type: AuthenticationType, key: String) {
    rest.get().uri("/code/{action}/{authentication}/{type}", action, authentication, type)
      .exchange().expectStatus().isNoContent.expectBody().consumeWith(
        document(
          AUTH_CODE + "_" + type, pathParameters(
            parameterWithName("action").description("Get code to action"),
            parameterWithName("authentication").description("Email or phone decide to type"),
            parameterWithName("type").description("EMAIL or PHONE")
          )
        )
      )
    val cache = reactiveStringRedisTemplate.opsForValue().get(key)
    StepVerifier.create(cache).assertNext { it != null && it.isNotEmpty() }.verifyComplete()
  }

  @Test
  fun `Get Code With Email To Register`() {
    code("register1", "123@123.com", AuthenticationType.EMAIL, "EMAIL-123@123.com")
  }

  @Test
  fun `Get Code With Phone To Register`() {
    code("register2", "13415151515", AuthenticationType.PHONE, "PHONE-13415151515")
  }

  private val registerRequestField = requestFields(
    fieldWithPath("name").type(JsonFieldType.STRING).require().description("User name"),
    fieldWithPath("authentication").string().require()
      .description("Phone or email, decide to type field"),
    fieldWithPath("password").string().require().description("User password."),
    fieldWithPath("rePassword").string().require().constraints("Same as password")
      .description("User password again."),
    fieldWithPath("type").string().require().constraints("One of mobile `PHONE` and `EMAIL`")
      .description("Register method."),
    fieldWithPath("code").string().require().description("Validate code.")
  )

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
      reactiveStringRedisTemplate.opsForValue().set("EMAIL-ambert_shopemhz@announces.ges", "123456")
        .awaitSingle()
      rest.post().uri("/register").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(params)).exchange().expectStatus().isNoContent.expectBody()
        .consumeWith(
          document(
            AUTH_REGISTER, registerRequestField
          )
        )
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
      rest.post().uri("/register").contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(params)).exchange().expectStatus().isBadRequest.expectBody()
        .jsonPath("message").isNotEmpty.jsonPath("stack").isArray.consumeWith(
          document(
            AUTH_REGISTER_ERROR, registerRequestField, responseFields(
              fieldWithPath("message").string().description("Error Message."),
              fieldWithPath("stack").array().description("Stack info.")
            )
          )
        )
    }
  }

}
