package an.example.randomchat.domain.user

import java.util.concurrent.atomic.AtomicLong

class User private constructor(
    val id: Long,
    val nickName: String
) {
    companion object {
        private val nextUserId = AtomicLong(1)

        fun create(nickName: String): User {
            val userId = nextUserId.getAndIncrement()
            return User(userId, nickName)
        }
    }
}