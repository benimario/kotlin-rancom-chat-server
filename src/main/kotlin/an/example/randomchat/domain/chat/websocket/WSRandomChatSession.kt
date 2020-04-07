package an.example.randomchat.domain.chat.websocket

import an.example.randomchat.domain.chat.session.ChatMessage
import an.example.randomchat.domain.chat.session.RandomChatSession
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

class WSRandomChatSession(
    private val webSocketSession: WebSocketSession
) : RandomChatSession {

    override fun isOpen(): Boolean {
        return webSocketSession.isOpen
    }

    override fun close() {
        webSocketSession.close(CloseStatus.NORMAL)
    }

    override fun sendMessage(message: ChatMessage) {
        val jsonMessage = ObjectMapper()
            .writeValueAsString(message)
        val webSocketTextMessage = TextMessage(jsonMessage)

        webSocketSession.sendMessage(webSocketTextMessage)
    }

}