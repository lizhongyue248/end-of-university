package wiki.zyue.eou.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import wiki.zyue.eou.base.BaseRepository
import wiki.zyue.eou.model.entity.User

/**
 * 2021/12/28 04:25:43
 * @author echo
 */
@Repository
interface UserRepository : BaseRepository<User> {

}

@Repository
interface UserCoroutineRepository : CoroutineCrudRepository<User, String> {

  suspend fun existsByPhone(phone: String): Boolean

  suspend fun existsByEmail(email: String): Boolean

  suspend fun existsByName(name: String): Boolean

}
