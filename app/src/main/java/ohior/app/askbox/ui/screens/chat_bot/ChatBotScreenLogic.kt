package ohior.app.askbox.ui.screens.chat_bot

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.UnknownException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ohior.app.askbox.debugMessage
import ohior.app.askbox.model.ChatMessage
import ohior.app.askbox.model.Messenger
import ohior.app.askbox.service.AskBotAI
import ohior.app.askbox.service.database.DatabaseManager
import ohior.app.askbox.utils.RequestAction

class ChatBotScreenLogic : ViewModel() {
    val messages: StateFlow<List<ChatMessage>> = DatabaseManager.getAllmesageFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    var sendQuery by mutableStateOf("")
        private set

    var isSendingMessage by mutableStateOf(RequestAction.None)
        private set

    fun onSendQueryChange(value: String) {
        sendQuery = value
    }

    private var chatResult by mutableStateOf("")

    private val chatInstance by lazy { AskBotAI.initializeChat(
        DatabaseManager.getAllmesage()
            .map {
                content(it.messenger.name.lowercase()) { text(it.message) }
            }) }

    fun sendMessage() {
        viewModelScope.launch {
            isSendingMessage = RequestAction.Loading
            try {
                val content = AskBotAI.sendMessage(sendQuery, chatInstance)
                chatResult = content.text ?: ""
                DatabaseManager.insertManyMessage(
                    listOf(
                        ChatMessage(
                            chatId = System.nanoTime(),
                            message = sendQuery,
                            messenger = Messenger.USER
                        ),
                        ChatMessage(
                            chatId = System.nanoTime(),
                            message = chatResult,
                            messenger = Messenger.MODEL
                        )
                    )
                )
                sendQuery = ""
                isSendingMessage = RequestAction.Success
            } catch (e: UnknownException) {
                debugMessage(e.message)
                isSendingMessage = RequestAction.Error
            }
        }
    }
}