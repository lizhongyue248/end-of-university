package wiki.zyue.eou.model

/**
 * 2022/1/5 00:30:15
 * @author echo
 */
class BadRequestException(msg: String): RuntimeException(msg)

class AuthenticationException(msg: String): RuntimeException(msg)