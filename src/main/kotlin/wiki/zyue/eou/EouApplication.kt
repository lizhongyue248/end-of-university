package wiki.zyue.eou

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EouApplication

fun main(args: Array<String>) {
  runApplication<EouApplication>(*args)
}
