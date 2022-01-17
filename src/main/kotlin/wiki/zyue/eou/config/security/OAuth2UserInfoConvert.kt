package wiki.zyue.eou.config.security

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import wiki.zyue.eou.service.OAuth2Service

/**
 * 2022/1/16 23:57:28
 * @author echo
 */
interface OAuth2UserInfoConvert {

  fun convert(oAuth2User: OAuth2User): Mono<AuthenticationResponse>

}

@Component
class GithubOAuth2UserInfoConvert(
  private val oAuth2Service: OAuth2Service,
  private val authorizationToken: AuthorizationToken
) : OAuth2UserInfoConvert {

  private val registerId = "github"

  override fun convert(oAuth2User: OAuth2User): Mono<AuthenticationResponse> =
    jacksonMapperBuilder()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .build()
      .convertValue(oAuth2User.attributes, GithubUserInfo::class.java)
      .map { name, email, avatar -> oAuth2Service.registerOrGet("GITHUB_${name}_${(1000..9999).random()}", email, avatar) }
      .map { userDetails ->
        authorizationToken.buildAuthenticationResponse(
          OAuth2AuthenticationToken(oAuth2User, userDetails.authorities, registerId),
          mapOf("issue" to registerId)
        )
      }

}