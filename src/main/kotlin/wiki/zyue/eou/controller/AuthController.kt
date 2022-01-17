package wiki.zyue.eou.controller

import kotlinx.coroutines.reactor.awaitSingle
import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import wiki.zyue.eou.component.CodeVerifier
import wiki.zyue.eou.component.EmailCodeSender
import wiki.zyue.eou.component.PhoneCodeSender
import wiki.zyue.eou.model.BadRequestException
import wiki.zyue.eou.model.dto.RegisterEntity
import wiki.zyue.eou.model.type.CodeType
import wiki.zyue.eou.service.AuthService
import javax.validation.Valid

/**
 * 2022/1/1 22:57:41
 * @author echo
 */
@RestController
@ResponseStatus(HttpStatus.NO_CONTENT)
class AuthController(
  private val authService: AuthService,
  private val emailCodeSender: EmailCodeSender,
  private val phoneCodeSender: PhoneCodeSender,
  private val codeVerifier: CodeVerifier
) {
  private val logger = LogFactory.getLog(this::class.java)

  @PostMapping("/register")
  suspend fun register(@Valid @RequestBody entity: RegisterEntity) {
    val result = codeVerifier.check(entity.authentication, entity.code, entity.type).awaitSingle()
    if (!result) throw BadRequestException("Code ${entity.code} Error.")
    if (entity.password != entity.rePassword) throw BadRequestException("Two password do not same.")
    authService.register(entity)
  }

  @GetMapping("/code/{action}/{authentication}/{type}")
  fun code(
    @PathVariable type: CodeType,
    @PathVariable authentication: String,
    @PathVariable action: String
  ): Mono<Void> {
    logger.debug("$authentication Get $action $type Code")
    val result = if (type.isEmail()) {
      emailCodeSender.send(authentication)
    } else {
      phoneCodeSender.send(authentication)
    }
    return result
  }

}

