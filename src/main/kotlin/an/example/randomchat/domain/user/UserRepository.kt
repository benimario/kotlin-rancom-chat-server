package an.example.randomchat.domain.user

import an.example.randomchat.common.RandomChatException
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class UserRepository {

    private val users = mutableListOf<User>()

    private val indexById = ConcurrentHashMap<Long, User>()
    private val indexByNickName = ConcurrentHashMap<String, User>()

    fun create(nickName: String): User {
        validate(nickName)

        return createUser(nickName)
    }

    private fun validate(nickName: String) {
        findByNickName(nickName)?.let {
            throw RandomChatException("이미 사용 중인 닉네임입니다.")
        }
    }

    private fun createUser(nickName: String): User {
        val user = User.create(nickName)

        users.add(user)
        onCreateUser(user)
        return user
    }

    fun findByNickName(nickName: String): User? {
        return indexByNickName[nickName]
    }

    fun findById(id: Long): User? {
        return indexById[id]
    }

    fun deleteUser(user: User) {
        findById(user.id)?.let {
            users.remove(it)
            onDeleteUser(it)
        }
    }

    private fun onCreateUser(user: User) {
        indexById[user.id] = user
        indexByNickName[user.nickName] = user
    }

    private fun onDeleteUser(user: User) {
        indexById.remove(user.id)
        indexByNickName.remove(user.nickName)
    }

}