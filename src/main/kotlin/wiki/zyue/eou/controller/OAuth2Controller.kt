package wiki.zyue.eou.controller

import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import wiki.zyue.eou.config.security.AuthenticationResponse
import wiki.zyue.eou.config.security.OAuth2UserInfoConvert
import wiki.zyue.eou.model.BadRequestException

/**
 * 2022/1/15 23:36:42
 * @author echo
 */
@RestController
@RequestMapping("/oauth/")
class OAuth2Controller(
  private val clientRegistrationRepository: ReactiveClientRegistrationRepository,
  private val serverOAuth2AuthorizationRequestResolver: ServerOAuth2AuthorizationRequestResolver,
  private val reactiveOAuth2AccessTokenResponseClient: ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>,
  private val oauth2UserService: ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User>,
  private val applicationContext: ApplicationContext
) {

  @GetMapping("/authorization/{registrationId}")
  fun login(
    exchange: ServerWebExchange,
    @PathVariable registrationId: String
  ): Mono<ResponseEntity<String>> =
    serverOAuth2AuthorizationRequestResolver.resolve(exchange, registrationId)
      .switchIfEmpty(Mono.error(BadRequestException("Can not find $registrationId registration.")))
      .map {
        ResponseEntity.noContent().header(HttpHeaders.LOCATION, it.authorizationRequestUri).build()
      }

  @GetMapping("/token/{registrationId}")
  fun token(exchange: ServerWebExchange, @PathVariable registrationId: String): Mono<ResponseEntity<AuthenticationResponse>> =
    Mono.zip(
      clientRegistrationRepository.findByRegistrationId(registrationId),
      serverOAuth2AuthorizationRequestResolver.resolve(exchange, registrationId)
    )
      .switchIfEmpty(Mono.error(BadRequestException("Can not find $registrationId registration.")))
      .flatMap { (clientRegistration: ClientRegistration, authorizationRequest: OAuth2AuthorizationRequest) ->
        OAuth2AuthorizationCodeGrantRequest(
          clientRegistration,
          OAuth2AuthorizationExchange(
            authorizationRequest, convertResponse(clientRegistration, exchange)
          )
        ).let { request ->
          reactiveOAuth2AccessTokenResponseClient.getTokenResponse(request)
            .map { OAuth2UserRequest(clientRegistration, it.accessToken) }
        }
      }
      .flatMap { oauth2UserService.loadUser(it) }
      .flatMap { oAuth2User ->
        applicationContext
          .getBean<OAuth2UserInfoConvert>("${registrationId}OAuth2UserInfoConvert")
          .convert(oAuth2User)
      }.map { ResponseEntity.ok(it) }

  /**
   * ServerWebExchange convert to OAuth2AuthorizationResponse
   * @see org.springframework.security.oauth2.client.web.server.OAuth2AuthorizationResponseUtils.convert
   */
  private fun convertResponse(clientRegistration: ClientRegistration, exchange: ServerWebExchange): OAuth2AuthorizationResponse {
    val request = exchange.request.queryParams
    val redirectUri =
      UriComponentsBuilder.fromHttpUrl(clientRegistration.providerDetails.authorizationUri).build()
        .toUriString()

    val code = request.getFirst(OAuth2ParameterNames.CODE)
    val state = request.getFirst(OAuth2ParameterNames.STATE)
    if (code.isNullOrBlank()) {
      throw BadRequestException("Code must not empty.")
    }
    return OAuth2AuthorizationResponse.success(code).redirectUri(redirectUri).state(state).build()
    // Get Error Message, maybe do not need to receive.
    // It should be handled on Vue App.
    //    val errorCode: String? = request.getFirst(OAuth2ParameterNames.ERROR)
    //    val errorDescription: String? = request.getFirst(OAuth2ParameterNames.ERROR_DESCRIPTION)
    //    val errorUri: String? = request.getFirst(OAuth2ParameterNames.ERROR_URI)
    //    return OAuth2AuthorizationResponse.error(errorCode)
    //      .redirectUri(redirectUri)
    //      .errorDescription(errorDescription)
    //      .errorUri(errorUri)
    //      .state(state)
    //      .build()
  }

}