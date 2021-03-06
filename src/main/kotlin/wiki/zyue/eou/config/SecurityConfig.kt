package wiki.zyue.eou.config

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import wiki.zyue.eou.config.security.*
import wiki.zyue.eou.service.AuthService
import java.security.interfaces.RSAPublicKey

/**
 * 2021/12/28 03:26:43
 * @author echo
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
  private val authService: AuthService,
  private val authorizationToken: AuthorizationToken
) {

  @Bean
  fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
    http {
      httpBasic { disable() }
      formLogin { disable() }
      csrf { disable() }
      logout { disable() }
      authorizeExchange {
        authorize(pathMatchers(POST, LOGIN_URL, REGISTER_URL), permitAll)
        authorize(pathMatchers(GET, CODE_URL, STATIC_RESOURCE), permitAll)
        authorize(anyExchange, authenticated)
      }
      addFilterAt(authenticationFilter(), SecurityWebFiltersOrder.FORM_LOGIN)
      oauth2ResourceServer { jwt { publicKey = authorizationToken.getRsaPublicKey() as RSAPublicKey } }
    }


  fun authenticationFilter(): AuthenticationWebFilter {
    val authenticationFilter = AuthenticationWebFilter(UserDetailsRepositoryReactiveAuthenticationManager(authService))
    authenticationFilter.setRequiresAuthenticationMatcher(pathMatchers(POST, LOGIN_URL))
    authenticationFilter.setAuthenticationFailureHandler(AuthenticationFailureHandler())
    authenticationFilter.setAuthenticationSuccessHandler(AuthenticationSuccessHandler(authorizationToken))
    authenticationFilter.setServerAuthenticationConverter(SecurityServerAuthenticationConverter(ServerCodecConfigurer.create()))
    return authenticationFilter
  }

}