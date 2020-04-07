package an.example.randomchat.domain.chat.room

import an.example.randomchat.domain.user.User
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class RandomChatRoomManager(
    private val randomChatRoomRepository: RandomChatRoomRepository
) {

    private val waitingUsers = ConcurrentLinkedQueue<User>()

    fun createRoomOrWaitAnotherUser(user: User): RandomChatRoom? {
        val waitingUser = getWaitingUser()

        if (waitingUser == null) {
            addWaitingUser(user)
            return null
        }

        return createRoom(user, waitingUser)
    }

    fun addWaitingUser(user: User) {
        waitingUsers.add(user)
    }

    fun getWaitingUser(): User? {
        return waitingUsers.poll()
    }

    fun findRoomByUser(user: User): RandomChatRoom? {
        return randomChatRoomRepository.findByUser(user)
    }

    private fun createRoom(user: User, waitingUser: User): RandomChatRoom {
        return randomChatRoomRepository.create(user, waitingUser)
    }

    @Synchronized
    fun removeUserFromRoom(user: User): RandomChatRoom? {
        return findRoomByUser(user)
            ?.also { room ->
                randomChatRoomRepository.removeUserFromRoom(user)

                if (room.users.size < 2) {
                    randomChatRoomRepository.remove(room)
                    room.users.firstOrNull()?.let {
                        waitingUsers.add(it)
                    }
                }
            }
    }

    fun removeWaitingUser(user: User) {
        waitingUsers.remove(user)
    }

}
