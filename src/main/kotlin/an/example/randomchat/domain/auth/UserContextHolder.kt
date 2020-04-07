package an.example.randomchat.domain.auth

import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
class UserContextHolder {

    private val userHolder = ThreadLocal
        .withInitial {
            UserHolder()
        }

    val nickName: String get() = userHolder.get().nickName
    val id: Long get() = userHolder.get().id

    fun set(nickName: String, id: Long) =
        this.userHolder.get().also {
            it.id = id
            it.nickName = nickName
        }.run(userHolder::set)

    fun clear() {
        userHolder.remove()
    }

    class UserHolder {
        var id by Delegates.notNull<Long>()
        lateinit var nickName: String
    }

}