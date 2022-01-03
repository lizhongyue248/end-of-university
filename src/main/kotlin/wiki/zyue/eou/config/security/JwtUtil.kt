package wiki.zyue.eou.config.security

import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import java.security.PublicKey
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*


/**
 * 2022/1/3 20:37:45
 * @author echo
 */
class JwtUtil {

  companion object {
    private val rsaJWK = RSAKeyGenerator(2048).keyID("eou").generate()
    private val signer = RSASSASigner(rsaJWK)

    fun encode(
      authentication: Authentication,
      claims: Map<String, Any>,
      days: Long = 7
    ): Pair<String, LocalDateTime> {
      val now = LocalDateTime.now()
      val expirationTime = now.plusDays(days)
      val jwtClaims = JWTClaimsSet.Builder()
        .issuer("eou")
        .subject(authentication.name)
//        .audience(Arrays.asList("https://app-one.com", "https://app-two.com"))
        .expirationTime(Timestamp.valueOf(expirationTime))
        .notBeforeTime(Timestamp.valueOf(now))
        .issueTime(Timestamp.valueOf(now))
        .jwtID(UUID.randomUUID().toString())
        .claim("authorities", authentication.authorities.map(GrantedAuthority::getAuthority))
        .let {
          claims.forEach { (key, value) -> it.claim(key, value) }
          it
        }.build()
      val signedJWT =
        SignedJWT(JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.keyID).build(), jwtClaims)
      signedJWT.sign(signer)
      return Pair(signedJWT.serialize(), expirationTime)
    }

    fun getRsaPublicKey(): PublicKey = rsaJWK.toPublicKey()

  }


}
