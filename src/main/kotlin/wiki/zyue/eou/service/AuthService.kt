package wiki.zyue.eou.service

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import wiki.zyue.eou.config.security.AuthenticationType
import wiki.zyue.eou.config.security.DEFAULT_ROLE
import wiki.zyue.eou.model.HttpException
import wiki.zyue.eou.model.dto.RegisterEntity
import wiki.zyue.eou.model.entity.User
import wiki.zyue.eou.repository.UserCoroutineRepository
import org.springframework.security.core.userdetails.User as DemoUser

/**
 * 2022/1/1 23:22:49
 * @author echo
 */
interface AuthService : ReactiveUserDetailsService {

  fun findByUsername(username: String, password: String): Mono<UserDetails>

  fun findByEmailOrPhone(
    type: AuthenticationType, authentication: String, code: String
  ): Mono<UserDetails>

  suspend fun register(entity: RegisterEntity)

}

@Service
class AuthServiceImpl(
  private val passwordEncoder: PasswordEncoder,
  private val userCoroutineRepository: UserCoroutineRepository
) : AuthService {
  override fun findByEmailOrPhone(
    type: AuthenticationType, authentication: String, code: String
  ): Mono<UserDetails> {
    if (type.isEmail()) {
      // TODO: Validate email code Match
    } else {
      // TODO: Validate phone code Match
    }
    return Mono.just(
      DemoUser.withDefaultPasswordEncoder().username("user").password("password").roles("USER")
        .build()
    )
  }

  override suspend fun register(entity: RegisterEntity) {
    val user = User().apply {
      name = entity.name
      password = passwordEncoder.encode(entity.password)
      roles = listOf(DEFAULT_ROLE)
    }
    if (entity.type.isPhone()) {
      user.phone = entity.authentication
      if (userCoroutineRepository.existsByPhone(user.phone))
        throw HttpException("User ${user.phone} is exists.")
    } else {
      user.email = entity.authentication
      if (userCoroutineRepository.existsByEmail(user.email))
        throw HttpException("User ${user.email} is exists.")
    }
    if (userCoroutineRepository.existsByName(entity.name))
      throw HttpException("User ${user.name} is exists.")
    userCoroutineRepository.save(user)
  }

  override fun findByUsername(username: String, password: String): Mono<UserDetails> {
    // TODO: Add user.
    val user =
      DemoUser.withDefaultPasswordEncoder().username("user").password("password").roles("USER")
        .build()
    if (passwordEncoder.matches(password, user.password)) {
      return Mono.just(user)
    }
    return Mono.error(UsernameNotFoundException("账号密码错误"))
  }

  @Deprecated(
    message = "Use Two Parameters Replace",
    replaceWith = ReplaceWith("this.findByUsername(user,password)")
  )
  override fun findByUsername(username: String?): Mono<UserDetails> =
    Mono.just(DemoUser.builder().build())


}