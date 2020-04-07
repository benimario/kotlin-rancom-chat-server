package an.example.randomchat.domain.chat.websocket

import an.example.randomchat.domain.auth.JWTUtil
import an.example.randomchat.domain.chat.RandomChatManager
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class WSRandomChatHandler(
    private val randomChatManager: RandomChatManager
) : TextWebSocketHandler() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun afterConnectionEstablished(session: WebSocketSession) {
        runCatching {
            val token = session.authorizationToken
                ?: throw RuntimeException("인증 토큰 없음")

            val decodedJWT = JWTUtil.verify(token)
            val userId = JWTUtil.extractId(decodedJWT)
            val randomChatSession = WSRandomChatSession(session)

            randomChatManager.start(userId, randomChatSession)
        }.onFailure { e ->
            logger.error("소켓 연결 후처리 오류 발생.", e)
            session.close(CloseStatus.BAD_DATA)
        }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus
    ) {
        logger.info("websocket session closed.")
        runCatching {
            val token = session.authorizationToken
                ?: throw RuntimeException("인증 토큰 없음")

            val decodedJWT = JWTUtil.verify(token)
            val userId = JWTUtil.extractId(decodedJWT)

            logger.info("closing user session. userId = $userId")
            randomChatManager.closeSession(userId)
        }.onFailure { e ->
            logger.error("소켓 접속 해제 후처리 오류 발생.", e)
        }
    }

    val WebSocketSession.authorizationToken
        get(): String? {
            return handshakeHeaders["sec-websocket-protocol"]
                ?.lastOrNull()
                ?.replace("access_token ", "")
                ?.trim()
        }

}
