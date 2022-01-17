package wiki.zyue.eou.config.security

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

/**
 * 2022/1/2 22:56:58
 * @author echo
 */
enum class AuthenticationType {
  PHONE,
  EMAIL,
  PASSWORD;

  fun isEmail(): Boolean = this == EMAIL

  fun isPhone(): Boolean = this == PHONE

  fun isPassword(): Boolean = this == PASSWORD

}

data class LoginParam(
  var username: String,
  var password: String,
  var type: AuthenticationType
)

@RedisHash("token", timeToLive = 604800)
data class AuthenticationResponse(
  @Id
  var username: String,
  var token: String,
  var expire: Long,
  val authorities: List<String>
)

//{
//  "login": "lizhongyue248",
//  "id": 29678316,
//  "node_id": "MDQ6VXNlcjI5Njc4MzE2",
//  "avatar_url": "https://avatars.githubusercontent.com/u/29678316?v=4",
//  "gravatar_id": "",
//  "url": "https://api.github.com/users/lizhongyue248",
//  "html_url": "https://github.com/lizhongyue248",
//  "followers_url": "https://api.github.com/users/lizhongyue248/followers",
//  "following_url": "https://api.github.com/users/lizhongyue248/following{/other_user}",
//  "gists_url": "https://api.github.com/users/lizhongyue248/gists{/gist_id}",
//  "starred_url": "https://api.github.com/users/lizhongyue248/starred{/owner}{/repo}",
//  "subscriptions_url": "https://api.github.com/users/lizhongyue248/subscriptions",
//  "organizations_url": "https://api.github.com/users/lizhongyue248/orgs",
//  "repos_url": "https://api.github.com/users/lizhongyue248/repos",
//  "events_url": "https://api.github.com/users/lizhongyue248/events{/privacy}",
//  "received_events_url": "https://api.github.com/users/lizhongyue248/received_events",
//  "type": "User",
//  "site_admin": false,
//  "name": "阿月很乖",
//  "company": "@Programming-With-Love",
//  "blog": "https://echocow.cn",
//  "location": "GuiYang, China",
//  "email": "lizhongyue248@163.com",
//  "hireable": null,
//  "bio": "今生不悔，来生不为",
//  "twitter_username": "zhongyue248",
//  "public_repos": 21,
//  "public_gists": 1,
//  "followers": 39,
//  "following": 24,
//  "created_at": "2017-06-24T15:48:33Z",
//  "updated_at": "2021-11-23T11:50:29Z"
//}
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GithubUserInfo(
  val name: String,
  val email: String,
  @JsonProperty("avatar_url")
  val avatar: String
) {
  fun map(param: (String, String, String) -> Mono<UserDetails>) = param(name, email, avatar)
}

const val LOGIN_URL = "/login"
const val REGISTER_URL = "/register"
const val CODE_URL = "/code/**"
const val STATIC_RESOURCE = "/docs/**"
const val OAUTH_TOKEN = "/oauth/token/{registrationId}"
const val OAUTH_LOGIN = "/oauth/authorization/{registrationId}"

const val DEFAULT_ROLE = "USER"
