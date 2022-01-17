package wiki.zyue.eou.service

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import wiki.zyue.eou.component.CodeVerifier
import wiki.zyue.eou.config.security.AuthenticationType
import wiki.zyue.eou.config.security.DEFAULT_ROLE
import wiki.zyue.eou.model.AuthenticationException
import wiki.zyue.eou.model.BadRequestException
import wiki.zyue.eou.model.dto.RegisterEntity
import wiki.zyue.eou.model.entity.User
import wiki.zyue.eou.repository.UserCoroutineRepository
import wiki.zyue.eou.repository.UserRepository

/**
 * 2022/1/1 23:22:49
 * @author echo
 */
interface AuthService : ReactiveUserDetailsService {

  fun authorizationPassword(authorization: String, password: String): Mono<UserDetails>

  fun findByEmailOrPhone(
    type: AuthenticationType, authentication: String, code: String
  ): Mono<UserDetails>

  suspend fun register(entity: RegisterEntity)

}

@Service
class AuthServiceImpl(
  private val passwordEncoder: PasswordEncoder,
  private val userCoroutineRepository: UserCoroutineRepository,
  private val userRepository: UserRepository,
  private val codeVerifier: CodeVerifier
) : AuthService {

  override fun findByEmailOrPhone(
    type: AuthenticationType, authentication: String, code: String
  ): Mono<UserDetails> =
    codeVerifier.check(authentication, code, type)
      .filter { it }
      .switchIfEmpty(Mono.error(AuthenticationException("Code Error.")))
      .flatMap {
        if (type.isEmail()) userRepository.findFirstByEmail(authentication)
        else userRepository.findFirstByPhone(authentication)
      }
      .switchIfEmpty(Mono.error(UsernameNotFoundException("User not found.")))
      .cast(UserDetails::class.java)

  override suspend fun register(entity: RegisterEntity) {
    val user = User().apply {
      name = entity.name
      username = entity.authentication
      password = passwordEncoder.encode(entity.password)
      roles = listOf(DEFAULT_ROLE)
    }
    if (entity.type.isPhone()) {
      user.phone = entity.authentication
      if (userCoroutineRepository.existsByPhone(user.phone))
        throw BadRequestException("User ${user.phone} is already exists.")
    } else {
      user.email = entity.authentication
      if (userCoroutineRepository.existsByEmail(user.email))
        throw BadRequestException("User ${user.email} is already exists.")
    }
    if (userCoroutineRepository.existsByUsername(entity.name))
      throw BadRequestException("User ${user.name} is already exists.")
    userCoroutineRepository.save(user)
  }

  override fun authorizationPassword(authorization: String, password: String): Mono<UserDetails> =
    userRepository.findFirstByUsernameOrEmailOrPhone(authorization, authorization, authorization)
      .switchIfEmpty(Mono.error(UsernameNotFoundException("User not found.")))
      .filter { passwordEncoder.matches(password, it.password) }
      .switchIfEmpty(Mono.error(AuthenticationException("Password Error.")))
      .cast(UserDetails::class.java)

  @Deprecated(
    message = "Use Two Parameters Replace",
    replaceWith = ReplaceWith("this.authorizationPassword(authorization,password)")
  )
  override fun findByUsername(username: String): Mono<UserDetails> =
    Mono.just(User())


}