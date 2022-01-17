package wiki.zyue.eou.repository.cache

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import wiki.zyue.eou.config.security.AuthenticationResponse

/**
 * 2022/1/17 21:22:39
 * @author echo
 */
@Repository
interface AuthenticationResponseRepository : CrudRepository<AuthenticationResponse, String>