package an.example.randomchat.controller

import an.example.randomchat.common.ApiResponse
import an.example.randomchat.common.RandomChatException
import an.example.randomchat.domain.auth.JWTUtil
import an.example.randomchat.domain.auth.SigninResponse
import an.example.randomchat.domain.auth.UserContextHolder
import an.example.randomchat.domain.user.UserRepository
import an.example.randomchat.interceptor.TokenValidationInterceptor
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/randomchat")
class AuthController(
    private val userRepository: UserRepository,
    private val userContextHolder: UserContextHolder
) {

    private val notAllowedNickNames = listOf("운영자", "알림")

    @PostMapping("/signin")
    fun signin(@RequestParam nickName: String): ApiResponse {
        validateNickName(nickName)

        val user = userRepository.create(nickName)

        return try {
            val signinResponse = SigninResponse(
                JWTUtil.createToken(nickName, user.id),
                JWTUtil.createRefreshToken(nickName, user.id),
                nickName
            )

            ApiResponse.ok(signinResponse)
        } catch (e: RuntimeException) {
            userRepository.deleteUser(user)

            ApiResponse.error(e.message)
        }
    }

    @PostMapping("/refresh_token")
    fun refreshToken(
        @RequestParam("grant_type") grantType: String
    ): ApiResponse {
        if (grantType != TokenValidationInterceptor.GRANT_TYPE_REFRESH) {
            throw IllegalArgumentException("grant_type 없음")
        }

        val nickName = userContextHolder.nickName
        val userId = userContextHolder.id

        return ApiResponse.ok(JWTUtil.createToken(nickName, userId))
    }

    private fun validateNickName(nickName: String) {
        if (nickName.isEmpty() ||
            nickName.isBlank() ||
            nickName.length > 10 ||
            notAllowedNickNames.contains(nickName)
        ) {
            throw RandomChatException(
                "닉네임 형식이 올바르지 않거나 허용되지 않은 닉네임입니다."
            )
        }
    }

}