package wiki.zyue.eou.controller

import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import wiki.zyue.eou.component.EmailCodeSender
import wiki.zyue.eou.component.PhoneCodeSender
import wiki.zyue.eou.model.type.CodeType

/**
 * 2022/1/1 22:57:41
 * @author echo
 */
@RestController
@ResponseStatus(HttpStatus.NO_CONTENT)
class AuthController(
  private val emailCodeSender: EmailCodeSender,
  private val phoneCodeSender: PhoneCodeSender,
) {
  private val logger = LogFactory.getLog(this::class.java)

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

