package wiki.zyue.eou.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import wiki.zyue.eou.base.BaseRepository
import wiki.zyue.eou.model.entity.User

/**
 * 2021/12/28 04:25:43
 * @author echo
 */
@Repository
interface UserRepository : BaseRepository<User> {

  fun findFirstByEmail(email: String): Mono<User>

  fun findFirstByPhone(phone: String): Mono<User>

  fun findFirstByUsername(username: String): Mono<User>

  fun findFirstByUsernameOrEmailOrPhone(username: String, email: String, phone: String): Mono<User>

}

@Repository
interface UserCoroutineRepository : CoroutineCrudRepository<User, String> {

  suspend fun existsByPhone(phone: String): Boolean

  suspend fun existsByEmail(email: String): Boolean

  suspend fun existsByUsername(name: String): Boolean

}
