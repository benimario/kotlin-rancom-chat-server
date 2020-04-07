package an.example.randomchat.domain.chat.session

interface RandomChatSession {

    fun isOpen(): Boolean

    fun close()

    fun sendMessage(message: ChatMessage)

}