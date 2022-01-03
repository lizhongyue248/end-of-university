package wiki.zyue.eou

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import reactor.test.StepVerifier
import wiki.zyue.eou.base.impl.ExpandRepositoryFactoryBean
import wiki.zyue.eou.repository.UserRepository

@DataMongoTest
@EnableReactiveMongoRepositories(repositoryFactoryBeanClass = ExpandRepositoryFactoryBean::class)
class EouApplicationTests {

  @Autowired
  private lateinit var userRepository: UserRepository

  @Test
  fun contextLoads() {
    val count = userRepository.findAll()
    StepVerifier.create(count)
      .expectNextCount(0)
      .verifyComplete()
  }

  @Test
  fun password() {
    println(BCryptPasswordEncoder().encode("123456"))
  }

}
