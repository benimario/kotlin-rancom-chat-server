package an.example.randomchat.configuration

import an.example.randomchat.domain.chat.websocket.WSRandomChatHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.support.DefaultHandshakeHandler

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val wsRandomChatHandler: WSRandomChatHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(
        registry: WebSocketHandlerRegistry
    ) {
        registry
            .addHandler(wsRandomChatHandler, "/ws/randomchat")
            .setAllowedOrigins("*")
            .setHandshakeHandler(handshakeHandler())
    }

    private fun handshakeHandler(): DefaultHandshakeHandler {
        return DefaultHandshakeHandler().apply {
            setSupportedProtocols("access_token")
        }
    }

}