package wiki.zyue.eou.integration

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.snippet.Attributes

/**
 * 2022/1/6 13:07:20
 * @author echo
 */
internal const val AUTH_CODE = "auth_code"
internal const val AUTH_REGISTER = "auth_register"
internal const val AUTH_REGISTER_ERROR = "auth_register_error"
internal const val CONSTRAINTS = "constraints"
internal const val REQUIRE = "require"

internal fun FieldDescriptor.require(): FieldDescriptor =
  this.attributes(Attributes.key(REQUIRE).value("true"))

internal fun FieldDescriptor.string(): FieldDescriptor =
  this.type(JsonFieldType.STRING)

internal fun FieldDescriptor.array(): FieldDescriptor =
  this.type(JsonFieldType.ARRAY)

internal fun FieldDescriptor.constraints(des: String): FieldDescriptor =
  this.attributes(Attributes.key(CONSTRAINTS).value(des))
