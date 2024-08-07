package ohior.app.askbox.ui.screens.ask_home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
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
import ohior.app.askbox.service.ask_notification.NotificationWorker
import ohior.app.askbox.service.database.DatabaseManager
import ohior.app.askbox.service.database.PrefManager
import ohior.app.askbox.utils.RequestAction
import java.util.concurrent.TimeUnit

class AskHomeScreenLogic(context: Context) : ViewModel() {
    private val workManager = WorkManager.getInstance(context)
    val messages: StateFlow<List<ChatMessage>> = DatabaseManager.getAllmesageFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    var requestAction by mutableStateOf(RequestAction.None)
        private set
    var textfield by mutableStateOf("")
        private set

    var textResult by mutableStateOf(PrefManager.getData(PrefManager.BOT_RESULT_KEY, ""))
        private set

    fun onValueChange(value: String) {
        textfield = value
    }

    var showNotification by mutableStateOf(false)

    // load previous AI chat to history
    private val chatInstance by lazy { AskBotAI.initializeChat(
        DatabaseManager.getAllmesage()
            .map {
                content(it.messenger.name.lowercase()) { text(it.message) }
            }) }

    fun resetVariables() {
        requestAction = RequestAction.None
        textfield = ""
    }

    fun getTextResult() {
        requestAction = RequestAction.Loading
        viewModelScope.launch {
            try {
                if (textfield.isNotEmpty()) {
                    val response = AskBotAI.getQueryFromAI(textfield)
                    textResult = response.text ?: "Could not find a result"
                    requestAction = RequestAction.Success
                    PrefManager.saveData(PrefManager.BOT_RESULT_KEY, textResult)
                } else {
                    textResult = "Please enter a question"
                    requestAction = RequestAction.Error
                }
                textfield = ""
            } catch (e: Exception) {
                requestAction = RequestAction.Error
            }
        }
    }


    fun sendChatMessage() {
        viewModelScope.launch {
            requestAction = RequestAction.Loading
            try {
                val content = AskBotAI.sendMessage(textfield, chatInstance)
                textResult = content.text ?: ""
                DatabaseManager.insertManyMessage(
                    listOf(
                        ChatMessage(
                            chatId = System.nanoTime(),
                            message = textfield,
                            messenger = Messenger.USER
                        ),
                        ChatMessage(
                            chatId = System.nanoTime(),
                            message = textResult,
                            messenger = Messenger.MODEL
                        )
                    )
                )
                textfield = ""
                requestAction = RequestAction.Success
            } catch (e: UnknownException) {
                debugMessage(e.message)
                requestAction = RequestAction.Error
            }
        }
    }


    fun createPeriodicWorkRequest() {
        val request = PeriodicWorkRequestBuilder<NotificationWorker>(30L, TimeUnit.MINUTES)
            .setConstraints(Constraints(requiredNetworkType = androidx.work.NetworkType.CONNECTED))
            .build()
        workManager.enqueue(request)
    }
}