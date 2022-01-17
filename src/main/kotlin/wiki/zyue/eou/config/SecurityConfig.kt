package wiki.zyue.eou.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.ProxyProvider
import wiki.zyue.eou.config.security.*
import wiki.zyue.eou.service.AuthService
import java.security.interfaces.RSAPublicKey

/**
 * 2021/12/28 03:26:43
 * @author echo
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {

  @Autowired
  private lateinit var authService: AuthService

  @Autowired
  private lateinit var serverCodecConfigurer: ServerCodecConfigurer

  @Autowired
  private lateinit var authorizationToken: AuthorizationToken

  @Bean
  fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
    http {
      httpBasic { disable() }
      formLogin { disable() }
      csrf { disable() }
      logout { disable() }
      authorizeExchange {
        authorize(pathMatchers(POST, LOGIN_URL, REGISTER_URL), permitAll)
        authorize(pathMatchers(GET, CODE_URL, STATIC_RESOURCE, OAUTH_LOGIN, OAUTH_TOKEN), permitAll)
        authorize(anyExchange, authenticated)
      }
      addFilterAt(authenticationFilter(), SecurityWebFiltersOrder.FORM_LOGIN)
      oauth2ResourceServer { jwt { publicKey = authorizationToken.getRsaPublicKey() as RSAPublicKey } }
      oauth2Client { }
    }


  fun authenticationFilter(): AuthenticationWebFilter {
    val authenticationFilter = AuthenticationWebFilter(MultiTypeAuthenticationManager(authService))
    authenticationFilter.setRequiresAuthenticationMatcher(pathMatchers(POST, LOGIN_URL))
    authenticationFilter.setAuthenticationFailureHandler(AuthenticationFailureHandler())
    authenticationFilter.setAuthenticationSuccessHandler(AuthenticationSuccessHandler(authorizationToken))
    authenticationFilter.setServerAuthenticationConverter(
      MultiTypeAuthenticationConverter(serverCodecConfigurer)
    )
    return authenticationFilter
  }

  @Bean
  fun serverOAuth2AuthorizationRequestResolver(clientRegistrationRepository: ReactiveClientRegistrationRepository): ServerOAuth2AuthorizationRequestResolver =
    DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository)

  @Bean
  fun oauth2UserService(): ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> =
    DefaultReactiveOAuth2UserService()

  @Bean
  fun reactiveOAuth2AccessTokenResponseClient(): ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    val client = WebClientReactiveAuthorizationCodeTokenResponseClient()
    val builder = WebClient.builder()
    val httpClient = HttpClient.create()
      .proxy { proxy ->
        proxy.type(ProxyProvider.Proxy.HTTP).host("127.0.0.1").port(7890)
        proxy.type(ProxyProvider.Proxy.SOCKS5).host("127.0.0.1").port(7890)
      }
    builder.clientConnector(ReactorClientHttpConnector(httpClient))
    client.setWebClient(builder.build())
    return client
  }


}