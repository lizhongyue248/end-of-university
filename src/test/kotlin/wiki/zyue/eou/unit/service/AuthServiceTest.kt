package wiki.zyue.eou.unit.service

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import reactor.core.publisher.Mono
import wiki.zyue.eou.component.CodeVerifier
import wiki.zyue.eou.config.security.AuthenticationType
import wiki.zyue.eou.model.AuthenticationException
import wiki.zyue.eou.model.BadRequestException
import wiki.zyue.eou.model.dto.RegisterEntity
import wiki.zyue.eou.model.entity.User
import wiki.zyue.eou.repository.UserCoroutineRepository
import wiki.zyue.eou.repository.UserRepository
import wiki.zyue.eou.service.AuthService
import wiki.zyue.eou.service.AuthServiceImpl
import wiki.zyue.eou.unit.monoExpectException
import wiki.zyue.eou.unit.monoNotNull

/**
 * 2022/1/5 16:09:21
 * @author echo
 */
private const val EXIST_EMAIL = "123@123.com"
private const val EXIST_PHONE = "13712341234"
private const val EXIST_NAME = "Alan"
private const val EXIST_SUCCESS_NAME = "Alan success"
private const val NOT_EXIST_EMAIL = "321@123.com"
private const val NOT_EXIST_PHONE = "13743214321"
private const val NOT_EXIST_NAME = "Bob"
private const val SUCCESS_PASSWORD = "Wiley St 4755, Gaines, Suriname, 060173"
private const val FAIL_PASSWORD = "Mountains Road 3253, Nunez, Gabon, 499719"
private const val CHECK_CODE_SUCCESS = "123456"
private const val CHECK_CODE_FAIL = "654321"

internal class AuthServiceTest {

  private val passwordEncoder = BCryptPasswordEncoder()

  @Mock
  private lateinit var userCoroutineRepository: UserCoroutineRepository

  @Mock
  private lateinit var userRepository: UserRepository

  @Mock
  private lateinit var codeVerifier: CodeVerifier

  private val authService: AuthService

  init {
    MockitoAnnotations.openMocks(this)

    runBlocking {
      // Set exist data.
      `when`(userCoroutineRepository.existsByEmail(EXIST_EMAIL)).thenReturn(true)
      `when`(userCoroutineRepository.existsByPhone(EXIST_PHONE)).thenReturn(true)
      `when`(userCoroutineRepository.existsByUsername(EXIST_NAME)).thenReturn(true)

      // Set not exist data.
      `when`(userCoroutineRepository.existsByEmail(NOT_EXIST_EMAIL)).thenReturn(false)
      `when`(userCoroutineRepository.existsByPhone(NOT_EXIST_PHONE)).thenReturn(false)
      `when`(userCoroutineRepository.existsByUsername(NOT_EXIST_NAME)).thenReturn(false)
    }

    // Find a user.
    `when`(userRepository.findFirstByEmail(EXIST_EMAIL)).thenReturn(Mono.just(User()))
    `when`(userRepository.findFirstByPhone(EXIST_PHONE)).thenReturn(Mono.just(User()))
    `when`(userRepository.findFirstByUsername(EXIST_NAME)).thenReturn(Mono.just(User()))
    `when`(userRepository.findFirstByUsername(EXIST_SUCCESS_NAME)).thenReturn(
      Mono.just(User().setPassword(passwordEncoder.encode(SUCCESS_PASSWORD)))
    )

    // Not find a user.
    `when`(userRepository.findFirstByEmail(NOT_EXIST_EMAIL)).thenReturn(Mono.empty())
    `when`(userRepository.findFirstByPhone(NOT_EXIST_PHONE)).thenReturn(Mono.empty())
    `when`(userRepository.findFirstByUsername(NOT_EXIST_NAME)).thenReturn(Mono.empty())

    `when`(userRepository.findFirstByUsernameOrEmailOrPhone(NOT_EXIST_NAME, NOT_EXIST_NAME, NOT_EXIST_NAME)).thenReturn(Mono.empty())
    `when`(userRepository.findFirstByUsernameOrEmailOrPhone(EXIST_SUCCESS_NAME, EXIST_SUCCESS_NAME, EXIST_SUCCESS_NAME)).thenReturn(
      Mono.just(User().setPassword(passwordEncoder.encode(SUCCESS_PASSWORD)))
    )
    `when`(userRepository.findFirstByUsernameOrEmailOrPhone(EXIST_NAME, EXIST_NAME ,EXIST_NAME)).thenReturn(
      Mono.just(User().setPassword(passwordEncoder.encode(SUCCESS_PASSWORD)))
    )

    `when`(codeVerifier.check(EXIST_EMAIL, CHECK_CODE_SUCCESS, AuthenticationType.EMAIL))
      .thenReturn(Mono.just(true))
    `when`(codeVerifier.check(EXIST_PHONE, CHECK_CODE_SUCCESS, AuthenticationType.PHONE))
      .thenReturn(Mono.just(true))
    `when`(codeVerifier.check(EXIST_EMAIL, CHECK_CODE_FAIL, AuthenticationType.EMAIL))
      .thenReturn(Mono.just(false))
    `when`(codeVerifier.check(EXIST_PHONE, CHECK_CODE_FAIL, AuthenticationType.PHONE))
      .thenReturn(Mono.just(false))
    `when`(codeVerifier.check(NOT_EXIST_EMAIL, CHECK_CODE_SUCCESS, AuthenticationType.EMAIL))
      .thenReturn(Mono.just(true))
    `when`(codeVerifier.check(NOT_EXIST_PHONE, CHECK_CODE_SUCCESS, AuthenticationType.PHONE))
      .thenReturn(Mono.just(true))
    `when`(codeVerifier.check(NOT_EXIST_EMAIL, CHECK_CODE_FAIL, AuthenticationType.EMAIL))
      .thenReturn(Mono.just(false))
    `when`(codeVerifier.check(NOT_EXIST_PHONE, CHECK_CODE_FAIL, AuthenticationType.PHONE))
      .thenReturn(Mono.just(false))

    authService =
      AuthServiceImpl(passwordEncoder, userCoroutineRepository, userRepository, codeVerifier)
  }

  @Test
  internal fun `Find By Username Success Test`() {
    monoNotNull(authService.authorizationPassword(EXIST_SUCCESS_NAME, SUCCESS_PASSWORD))
  }

  @Test
  internal fun `Find By Username Fail Test When Exception`() {
    monoExpectException(
      authService.authorizationPassword(NOT_EXIST_NAME, SUCCESS_PASSWORD),
      UsernameNotFoundException::class.java
    )
    monoExpectException(
      authService.authorizationPassword(EXIST_NAME, FAIL_PASSWORD),
      AuthenticationException::class.java
    )
  }


  @Test
  internal fun `Find By Email Or Phone Success Test`() {
    monoNotNull(
      authService.findByEmailOrPhone(AuthenticationType.EMAIL, EXIST_EMAIL, CHECK_CODE_SUCCESS)
    )
    monoNotNull(
      authService.findByEmailOrPhone(AuthenticationType.PHONE, EXIST_PHONE, CHECK_CODE_SUCCESS)
    )
  }

  @Test
  internal fun `Find By Email Or Phone Fail Test When Exception`() {
    monoExpectException(
      authService.findByEmailOrPhone(AuthenticationType.EMAIL, EXIST_EMAIL, CHECK_CODE_FAIL),
      AuthenticationException::class.java
    )
    monoExpectException(
      authService.findByEmailOrPhone(AuthenticationType.PHONE, EXIST_PHONE, CHECK_CODE_FAIL),
      AuthenticationException::class.java
    )
    monoExpectException(
      authService.findByEmailOrPhone(AuthenticationType.EMAIL, NOT_EXIST_EMAIL, CHECK_CODE_SUCCESS),
      UsernameNotFoundException::class.java
    )
    monoExpectException(
      authService.findByEmailOrPhone(AuthenticationType.PHONE, NOT_EXIST_PHONE, CHECK_CODE_SUCCESS),
      UsernameNotFoundException::class.java
    )
  }

  @Test
  internal fun `Register Success Test`() {
    runBlocking {
      authService.register(
        RegisterEntity(
          NOT_EXIST_NAME,
          SUCCESS_PASSWORD,
          SUCCESS_PASSWORD,
          NOT_EXIST_EMAIL,
          CHECK_CODE_SUCCESS,
          AuthenticationType.EMAIL
        )
      )
    }
  }

  @Test
  internal fun `Register Fail Test`() {
    runBlocking {
      // Email exist.
      assertThrows<BadRequestException> {
        authService.register(
          RegisterEntity(
            NOT_EXIST_NAME,
            SUCCESS_PASSWORD,
            SUCCESS_PASSWORD,
            EXIST_EMAIL,
            CHECK_CODE_SUCCESS,
            AuthenticationType.EMAIL
          )
        )
      }

      // Phone Exist.
      assertThrows<BadRequestException> {
        authService.register(
          RegisterEntity(
            NOT_EXIST_NAME,
            SUCCESS_PASSWORD,
            SUCCESS_PASSWORD,
            EXIST_PHONE,
            CHECK_CODE_SUCCESS,
            AuthenticationType.PHONE
          )
        )
      }

      // Name Exists..
      assertThrows<BadRequestException> {
        authService.register(
          RegisterEntity(
            EXIST_NAME,
            SUCCESS_PASSWORD,
            SUCCESS_PASSWORD,
            NOT_EXIST_PHONE,
            CHECK_CODE_SUCCESS,
            AuthenticationType.PHONE
          )
        )
      }
    }
  }
}
