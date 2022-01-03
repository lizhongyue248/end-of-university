package wiki.zyue.eou.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
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

  @Bean
  fun springSecurityFilterChain(
    http: ServerHttpSecurity
  ): SecurityWebFilterChain = http
    .httpBasic().disable()
    .formLogin().disable()
    .csrf().disable()
    .logout().disable()
    .authorizeExchange { exchanges ->
      exchanges
        .pathMatchers(HttpMethod.POST, LOGIN_URL).permitAll()
        .anyExchange().authenticated()
    }
    .addFilterAt(authenticationFilter(), SecurityWebFiltersOrder.FORM_LOGIN)
    .oauth2ResourceServer { resourceServer ->
      resourceServer.jwt { jwt ->
        jwt.publicKey(JwtUtil.getRsaPublicKey() as RSAPublicKey)
      }
    }
    .build()

  fun authenticationFilter(): AuthenticationWebFilter {
    val authenticationFilter = AuthenticationWebFilter(MultiTypeAuthenticationManager(authService))
    authenticationFilter.setRequiresAuthenticationMatcher(
      ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, LOGIN_URL)
    )
    authenticationFilter.setAuthenticationFailureHandler(AuthenticationFailureHandler())
    authenticationFilter.setServerAuthenticationConverter(
      MultiTypeAuthenticationConverter(serverCodecConfigurer)
    )
    authenticationFilter.setAuthenticationSuccessHandler(AuthenticationSuccessHandler())
    return authenticationFilter
  }

}