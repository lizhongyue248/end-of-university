package wiki.zyue.eou.model.dto

import wiki.zyue.eou.config.security.AuthenticationType
import javax.validation.constraints.NotNull

data class RegisterEntity(
  @NotNull
  val name: String,
  @NotNull
  val password: String,
  @NotNull
  val rePassword: String,
  @NotNull
  val authentication: String,
  @NotNull
  val code: String,
  val type: AuthenticationType = AuthenticationType.EMAIL
)