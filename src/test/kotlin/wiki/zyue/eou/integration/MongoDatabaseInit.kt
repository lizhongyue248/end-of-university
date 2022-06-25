package wiki.zyue.eou.integration

/**
 * 2022/6/23 23:30:35
 * @author echo
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MongoDatabaseInit(
  val value: String = ""
)
