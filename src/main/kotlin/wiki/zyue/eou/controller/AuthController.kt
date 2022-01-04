package wiki.zyue.eou.controller

import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import wiki.zyue.eou.component.CodeVerifier
import wiki.zyue.eou.component.EmailCodeSender
import wiki.zyue.eou.component.PhoneCodeSender
import wiki.zyue.eou.model.HttpException
import wiki.zyue.eou.model.dto.RegisterEntity
import wiki.zyue.eou.model.type.CodeType
import wiki.zyue.eou.service.AuthService
import javax.validation.Valid

/**
 * 2022/1/1 22:57:41
 * @author echo
 */
@RestController
class AuthController(
  private val authService: AuthService,
  private val emailCodeSender: EmailCodeSender,
  private val phoneCodeSender: PhoneCodeSender,
  private val codeVerifier: CodeVerifier
) {
  private val logger = LogFactory.getLog(this::class.java)

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  suspend fun register(@Valid @RequestBody entity: RegisterEntity) {
    val type = if (entity.type.isEmail()) CodeType.EMAIL
    else CodeType.PHONE
    val result = codeVerifier.check(entity.authentication, entity.code, type)
    if (!result) throw HttpException("Code ${entity.code} Error.")
    authService.register(entity)
  }

  @GetMapping("/code/{action}/{authentication}/{type}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
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

