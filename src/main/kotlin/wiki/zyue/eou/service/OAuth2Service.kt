package wiki.zyue.eou.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import wiki.zyue.eou.config.security.DEFAULT_ROLE
import wiki.zyue.eou.model.entity.User
import wiki.zyue.eou.repository.UserRepository

/**
 * 2022/1/17 00:52:46
 * @author echo
 */
interface OAuth2Service {

  fun registerOrGet(name: String, email: String, avatar: String): Mono<UserDetails>

}

@Service
class OAuth2ServiceImpl(
  private val userRepository: UserRepository
) : OAuth2Service {
  override fun registerOrGet(name: String, email: String, avatar: String): Mono<UserDetails> =
    userRepository.findFirstByEmail(email)
      .switchIfEmpty {
        userRepository.save(User().also {
          it.name = name
          it.username = email
          it.email = email
          it.avatar = avatar
          it.roles = listOf(DEFAULT_ROLE)
        })
      }
      .cast(UserDetails::class.java)
}