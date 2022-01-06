package wiki.zyue.eou.unit.config

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import reactor.test.StepVerifier
import wiki.zyue.eou.config.MongoConfig
import wiki.zyue.eou.model.entity.User
import wiki.zyue.eou.repository.UserRepository

/**
 * 2022/1/6 12:32:10
 * @author echo
 */
@DataMongoTest
@Import(MongoConfig::class)
internal class MongoConfigTest {

  @Autowired
  private lateinit var userRepository: UserRepository

  @Test
  @WithAnonymousUser
  internal fun `Auditing Test When Anonymous`() {
    StepVerifier.create(userRepository.save(User()))
      .expectNextMatches { it != null && it.createUser == "anonymous" && it.modifyUser == "anonymous" }
      .verifyComplete()
  }

  @Test
  @WithMockUser("Alan")
  internal fun `Auditing Test When User`() {
    StepVerifier.create(userRepository.save(User()))
      .expectNextMatches { it != null && it.createUser == "Alan" && it.modifyUser == "Alan" }
      .verifyComplete()
  }
}