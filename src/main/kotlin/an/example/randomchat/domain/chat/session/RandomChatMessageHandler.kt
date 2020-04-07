package an.example.randomchat.domain.chat.session

import an.example.randomchat.domain.chat.room.RandomChatRoomManager
import an.example.randomchat.domain.user.User
import org.springframework.stereotype.Component

@Component
class RandomChatMessageHandler(
    private val randomChatRoomManager: RandomChatRoomManager,
    private val randomChatSessionManager: RandomChatSessionManager
) {

    fun onMessage(sender: User, message: String) {
        val room = randomChatRoomManager.findRoomByUser(sender)

        room?.let {
            val chatMessage = ChatMessage(sender.nickName, message)

            room.users
                .filter { user ->
                    user.id != sender.id
                }
                .forEach { user ->
                    val session = randomChatSessionManager.getSession(user)
                    session?.sendMessage(chatMessage)
                }
        }
    }

}