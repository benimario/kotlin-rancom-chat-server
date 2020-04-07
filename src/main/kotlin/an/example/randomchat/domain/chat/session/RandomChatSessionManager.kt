package an.example.randomchat.domain.chat.session

import an.example.randomchat.domain.user.User
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class RandomChatSessionManager {

    private val sessions =
        ConcurrentHashMap<User, RandomChatSession>()

    fun addSession(user: User, session: RandomChatSession) {
        sessions[user] = session
    }

    fun removeSession(user: User) {
        sessions[user]?.close()
        sessions.remove(user)
    }

    fun getSession(user: User): RandomChatSession? {
        return sessions[user]
    }

}
