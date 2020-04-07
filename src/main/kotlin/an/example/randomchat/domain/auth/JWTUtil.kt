package an.example.randomchat.domain.auth
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

object JWTUtil {

    private const val ISSUER = "RandomChat"
    private const val SUBJECT = "Auth"
    private const val EXPIRE_TIME = 60L * 60 * 2 * 1000
    private const val REFRESH_EXPIRE_TIME = 60L * 60 * 24 * 30 * 1000

    private val secret = "your-secret"
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    private val refreshSecret = "your-refresh-secret"
    private val refreshAlgorithm: Algorithm = Algorithm.HMAC256(refreshSecret)

    fun createToken(nickName: String, userId: Long) = JWT.create()
        .withIssuer(ISSUER)
        .withSubject(SUBJECT)
        .withIssuedAt(Date())
        .withExpiresAt(Date(Date().time + EXPIRE_TIME))
        .withClaim(JWTClaims.NICK_NAME, nickName)
        .withClaim(JWTClaims.USER_ID, userId)
        .sign(algorithm)

    fun createRefreshToken(nickName: String, userId: Long) = JWT.create()
        .withIssuer(ISSUER)
        .withSubject(SUBJECT)
        .withIssuedAt(Date())
        .withExpiresAt(Date(Date().time + REFRESH_EXPIRE_TIME))
        .withClaim(JWTClaims.NICK_NAME, nickName)
        .withClaim(JWTClaims.USER_ID, userId)
        .sign(refreshAlgorithm)

    fun verify(token: String): DecodedJWT =
        JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build()
            .verify(token)

    fun verifyRefresh(token: String): DecodedJWT =
        JWT.require(refreshAlgorithm)
            .withIssuer(ISSUER)
            .build()
            .verify(token)

    fun extractNickName(jwt: DecodedJWT): String =
        jwt.getClaim(JWTClaims.NICK_NAME).asString()

    fun extractId(jwt: DecodedJWT): Long =
        jwt.getClaim(JWTClaims.USER_ID).asLong()

    object JWTClaims {
        const val NICK_NAME = "nickName"
        const val USER_ID = "userId"
    }

}
