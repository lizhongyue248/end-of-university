package wiki.zyue.eou.model.type

/**
 * 2022/1/4 23:01:47
 * @author echo
 */
enum class CodeType {
  PHONE,
  EMAIL;

  fun isPhone(): Boolean = this == PHONE

  fun isEmail(): Boolean = this == EMAIL
}