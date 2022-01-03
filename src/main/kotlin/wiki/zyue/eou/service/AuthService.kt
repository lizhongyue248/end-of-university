package wiki.zyue.eou.service

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import wiki.zyue.eou.config.security.AuthenticationType

/**
 * 2022/1/1 23:22:49
 * @author echo
 */
interface AuthService {

  fun findByUsername(username: String, password: String): Mono<UserDetails>

  fun findByEmailOrPhone(
    type: AuthenticationType,
    authentication: String,
    code: String
  ): Mono<UserDetails>

}

@Service
class AuthServiceImpl(
  private val passwordEncoder: PasswordEncoder
) : AuthService {

  override fun findByEmailOrPhone(
    type: AuthenticationType,
    authentication: String,
    code: String
  ): Mono<UserDetails> {
    if (type.isEmail()) {
      // TODO: Validate email code Match
    } else {
      // TODO: Validate phone code Match
    }
    return Mono.just(
      User.withDefaultPasswordEncoder()
        .username("user")
        .password("password")
        .roles("USER")
        .build()
    )
  }

  override fun findByUsername(username: String, password: String): Mono<UserDetails> {
    // TODO: Add user.
    val user = User.withDefaultPasswordEncoder()
      .username("user")
      .password("password")
      .roles("USER")
      .build()
    if (passwordEncoder.matches(password, user.password)) {
      return Mono.just(user)
    }
    return Mono.error(UsernameNotFoundException("账号密码错误"))
  }


}