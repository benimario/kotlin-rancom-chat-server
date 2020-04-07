package an.example.randomchat.controller

import an.example.randomchat.common.ApiResponse
import an.example.randomchat.common.RandomChatException
import an.example.randomchat.controller.request.MessageRequest
import an.example.randomchat.domain.auth.UserContextHolder
import an.example.randomchat.domain.chat.RandomChatManager
import an.example.randomchat.domain.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/randomchat")
class MessageController(
    private val userRepository: UserRepository,
    private val userContextHolder: UserContextHolder,
    private val randomChatManager: RandomChatManager
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/message")
    fun sendMessage(@RequestBody request: MessageRequest): ApiResponse {
        return try {
            val userId = userContextHolder.id
            val user = userRepository.findById(userId)
                ?: throw RandomChatException("사용자 정보 없음.")

            randomChatManager.sendMessage(user, request.content)

            ApiResponse.ok()
        } catch (e: RuntimeException) {
            logger.error("메세지 전송 실패", e)
            ApiResponse.error("메세지 전송에 실패하였습니다.")
        }

    }

}