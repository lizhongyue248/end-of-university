package wiki.zyue.eou.service

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import wiki.zyue.eou.repository.UserRepository

/**
 * 2022/1/1 23:22:49
 * @author echo
 */
interface AuthService : ReactiveUserDetailsService {

}

@Service
class AuthServiceImpl(
  private val userRepository: UserRepository
) : AuthService {
  override fun findByUsername(username: String): Mono<UserDetails> =
    userRepository.findFirstByUsername(username)
      .switchIfEmpty(Mono.error(UsernameNotFoundException("User not found.")))
      .cast(UserDetails::class.java)

}