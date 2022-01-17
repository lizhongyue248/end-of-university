package wiki.zyue.eou.handler

import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.core.OAuth2AuthorizationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import wiki.zyue.eou.model.BadRequestException

/**
 * 2022/1/5 18:15:24
 * @author echo
 */
@ControllerAdvice
class ExceptionHandler {

  @ExceptionHandler(BadRequestException::class, OAuth2AuthorizationException::class)
  fun badRequestException(exception: BadRequestException): ResponseEntity<ExceptionMessage> =
    ResponseEntity.badRequest().body(ExceptionMessage(
      exception.localizedMessage,
      exception.stackTrace.map { it.toString() }
    ))

  data class ExceptionMessage(
    val message: String,
    val stack: List<String>
  )

}