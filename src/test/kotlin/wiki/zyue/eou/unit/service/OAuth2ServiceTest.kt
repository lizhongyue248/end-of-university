package wiki.zyue.eou.unit.service

import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import wiki.zyue.eou.model.entity.User
import wiki.zyue.eou.repository.UserRepository
import wiki.zyue.eou.service.OAuth2Service
import wiki.zyue.eou.service.OAuth2ServiceImpl

private const val EXIST_EMAIL = "raylene_braytona@personally.nzu"
private const val NOT_EXIST_EMAIL = "henri_marshpc8z@barriers.xl"

private val existUser = User().apply {
  avatar = "https://supersvqfj.nfl"
  name = "Alan"
  username = EXIST_EMAIL
  email = EXIST_EMAIL
  roles = listOf("ROLE_USER")
}

private val notExistUser = User().apply {
  avatar = "https://weirdpfa.gg"
  name = "Alice"
  username = NOT_EXIST_EMAIL
  email = NOT_EXIST_EMAIL
  roles = listOf("USER")
}


/**
 * 2022/1/18 00:37:42
 * @author echo
 */
internal class OAuth2ServiceTest {

  @Mock
  private lateinit var userRepository: UserRepository

  private val oAuth2Service: OAuth2Service

  init {
    MockitoAnnotations.openMocks(this)
    Mockito.`when`(userRepository.findFirstByEmail(EXIST_EMAIL))
      .thenReturn(Mono.just(existUser))
    Mockito.`when`(userRepository.findFirstByEmail(NOT_EXIST_EMAIL))
      .thenReturn(Mono.empty())
    Mockito.`when`(userRepository.save(notExistUser))
      .thenReturn(Mono.just(notExistUser))
    oAuth2Service = OAuth2ServiceImpl(userRepository)
  }

  @Test
  fun `Register When User Do Not Exist`() {
    StepVerifier.create(oAuth2Service.registerOrGet(existUser.name, existUser.email, existUser.avatar))
      .expectNextMatches {
        val newUser = it as User
        newUser.username == existUser.email
          && newUser.avatar == existUser.avatar
          && newUser.email == existUser.email
          && newUser.name == existUser.name
      }
      .verifyComplete()
    Mockito.verify(userRepository, Mockito.times(1)).findFirstByEmail(EXIST_EMAIL)
  }

  @Test
  fun `Register When User Exist`() {
    StepVerifier.create(oAuth2Service.registerOrGet(notExistUser.name, notExistUser.email, notExistUser.avatar))
      .expectNextMatches {
        val newUser = it as User
        newUser.username == notExistUser.email
          && newUser.avatar == notExistUser.avatar
          && newUser.email == notExistUser.email
          && newUser.name == notExistUser.name
      }
      .verifyComplete()
    Mockito.verify(userRepository, Mockito.times(1)).findFirstByEmail(NOT_EXIST_EMAIL)
  }
}