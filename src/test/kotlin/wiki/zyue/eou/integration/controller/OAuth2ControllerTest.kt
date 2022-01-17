package wiki.zyue.eou.integration.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import wiki.zyue.eou.config.security.AuthenticationResponse
import wiki.zyue.eou.config.security.DEFAULT_ROLE
import wiki.zyue.eou.integration.OAUTH_LOGIN_URL
import wiki.zyue.eou.repository.UserRepository

private const val TEST_USER = "Alan"
private const val TEST_EMAIL = "charlisa_chavise0@earn.dc"
private const val TEST_AVATAR = "https://chinaoho9lodvda.uqj"

/**
 * 2022/1/17 16:11:54
 * @author echo
 */
@SpringBootTest
@ExtendWith(RestDocumentationExtension::class, MockitoExtension::class)
@DisplayName("Github OAuth2 Mock Test")
internal class OAuth2ControllerTest : AbstractControllerTest() {

  @MockBean
  private lateinit var reactiveOAuth2AccessTokenResponseClient: ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>

  @MockBean
  private lateinit var oauth2UserService: ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User>

  @Autowired
  private lateinit var userRepository: UserRepository

  @BeforeEach
  internal fun mockBean() {
    Mockito.`when`(reactiveOAuth2AccessTokenResponseClient.getTokenResponse(Mockito.any()))
      .thenReturn(
        Mono.just(
          OAuth2AccessTokenResponse
            .withToken("access_token")
            .tokenType(OAuth2AccessToken.TokenType.BEARER).build()
        )
      )
    Mockito.`when`(oauth2UserService.loadUser(Mockito.any()))
      .thenReturn(
        Mono.just(
          DefaultOAuth2User(
            listOf(SimpleGrantedAuthority(DEFAULT_ROLE)),
            mapOf("name" to TEST_USER, "email" to TEST_EMAIL, "avatar_url" to TEST_AVATAR),
            "name"
          )
        )
      )
  }

  @Test
  internal fun `Oauth Authorization Login Url`() {
    rest.get()
      .uri("/oauth/authorization/{registrationId}", "github")
      .exchange()
      .expectStatus().isNoContent
      .expectHeader().value(HttpHeaders.LOCATION) { location ->
        val locationUrl = UriComponentsBuilder.fromHttpUrl(location).build()
        val queryParams = locationUrl.queryParams
        assertAll("Location Url Verification",
          { assertEquals("github.com", locationUrl.host) },
          { assertEquals("/login/oauth/authorize", locationUrl.path) },
          { assertEquals("client-id", queryParams.getFirst(CLIENT_ID)) },
          { assertEquals("http://localhost:8080/oauth", queryParams.getFirst(REDIRECT_URI)) },
          { assertEquals("user:email", queryParams.getFirst(SCOPE)) },
          { assertEquals("code", queryParams.getFirst(RESPONSE_TYPE)) },
          { assertNotNull(queryParams.getFirst(STATE)) }
        )
      }
      .expectBody().consumeWith(
        document(
          OAUTH_LOGIN_URL,
          pathParameters(parameterWithName("registrationId").description("OAuth Provide")),
          responseHeaders(headerWithName(HttpHeaders.LOCATION).description("Oauth Authorization Login Url"))
        )
      )
  }

  @Test
  internal fun `OAuth Authorization Token By Code`() {
    rest.get()
      .uri { uriBuilder ->
        uriBuilder.path("/oauth/token/github")
          .queryParam(CODE, "code")
          .queryParam(STATE, "state")
          .build()
      }
      .exchange()
      .expectStatus().isOk
      .expectBody(AuthenticationResponse::class.java)
      .value { response ->
        assertAll("Token Exist",
          { assertNotNull(response.token) },
          { assertNotNull(response.username) },
          { assertNotNull(response.expire) },
          { assertTrue(response.authorities.isNotEmpty()) }
        )
      }
    StepVerifier.create(userRepository.findFirstByEmail(TEST_EMAIL))
      .expectNextMatches { user ->
        user.email == TEST_EMAIL
          && user.avatar == TEST_AVATAR
          && user.name.startsWith("GITHUB_${TEST_USER}_")
          && user.username == TEST_EMAIL
      }
      .verifyComplete()
  }
}