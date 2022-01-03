package wiki.zyue.eou.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 2022/1/1 22:57:41
 * @author echo
 */
@RestController
class AuthController {

  @GetMapping("/test")
  fun test() = ResponseEntity.ok("test")

}