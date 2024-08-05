package ohior.app.askbox.service.database

import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.flow
import io.objectbox.query.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import ohior.app.askbox.model.ChatMessage

object DatabaseManager {
    private val messageBox = ObjectBox.store.boxFor(ChatMessage::class)

    // Extension function to convert Query to Flow
    private fun <T> Query<T>.asFlow(): Flow<List<T>> = callbackFlow {
        val subscription = this@asFlow.subscribe().observer { data ->
            trySend(data).isSuccess
        }
        awaitClose { subscription.cancel() }
    }

    fun insertMessage(chatMessage: ChatMessage) {
        messageBox.put(chatMessage)
    }

    fun insertManyMessage(chatMessages: List<ChatMessage>) {
        messageBox.put(chatMessages)
    }

    fun getAllmesage(): List<ChatMessage> = messageBox.all

    fun getAllmesageFlow(): Flow<List<ChatMessage>> = messageBox.query().build().asFlow()

    fun deleteMessage(chatMessage: ChatMessage): Boolean = messageBox.remove(chatMessage)

    fun deleteAllMessage(): Unit = messageBox.removeAll()
}