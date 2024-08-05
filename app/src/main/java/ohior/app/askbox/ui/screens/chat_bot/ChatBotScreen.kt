package ohior.app.askbox.ui.screens.chat_bot

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mukesh.MarkDown
import dev.jeziellago.compose.markdowntext.MarkdownText
import ohior.app.askbox.model.Messenger
import ohior.app.askbox.utils.RequestAction


@Composable
fun ColumnScope.ChatBotScreen() {
    val chatBotViewModel = viewModel<ChatBotScreenLogic>()
    val keyboard = LocalSoftwareKeyboardController.current
    val messages by chatBotViewModel.messages.collectAsStateWithLifecycle()
    val context = LocalContext.current
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(8.dp),
//        reverseLayout = true
//    ) {
//        items(messages, key = { it.chatId }) { message ->
//            val startPad = if (message.messenger == Messenger.USER) 50.dp else 0.dp
//            val endPad = if (message.messenger == Messenger.AIBOT) 50.dp else 0.dp
//            Box(
//                modifier = Modifier.fillMaxWidth(),
//                contentAlignment = if (message.messenger == Messenger.USER) Alignment.CenterEnd
//                else Alignment.CenterStart
//            ) {
////                Text(
////                    modifier = Modifier
////                        .padding(start = startPad, end = endPad),
////                    text = message.message
////                )
//                MarkDown(
//                    modifier = Modifier
//                        .wrapContentSize()
//                        .padding(start = startPad, end = endPad),
//                    text = message.message
//                )
//            }
//        }
//        item {
//        }
//    }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            messages.forEach { message ->
                val startPad = if (message.messenger == Messenger.USER) 50.dp else 8.dp
                val endPad = if (message.messenger == Messenger.MODEL) 50.dp else 8.dp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentAlignment = if (message.messenger == Messenger.USER) Alignment.CenterEnd
                    else Alignment.CenterStart
                ) {
                    MarkdownText(
                        modifier = Modifier
                            .padding(start = startPad, end = endPad)
                            .background(
                                MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(10.dp)
                            ).padding(10.dp),
                        markdown = message.message
                    )
                }
            }
        }
        when (chatBotViewModel.isSendingMessage) {
            RequestAction.Loading -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            RequestAction.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(
                                topStartPercent = 20,
                                topEndPercent = 20
                            )
                        )
                ) {
                    Text(
                        text = "There was an error getting response",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.background,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    )
                }
            }

            else -> Unit
        }
//        TextField(
//            value = chatBotViewModel.sendQuery,
//            onValueChange = chatBotViewModel::onSendQueryChange,
//            modifier = Modifier.fillMaxWidth(),
//            trailingIcon = {
//                OutlinedButton(onClick = {
//                    if (chatBotViewModel.sendQuery.isNotBlank()) {
//                        keyboard?.hide()
//                        chatBotViewModel.sendMessage()
//                    } else {
//                        Toast.makeText(
//                            context,
//                            "Text input is empty",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }) {
//                    Icon(
//                        imageVector = Icons.Outlined.Send,
//                        contentDescription = "Send message"
//                    )
//                }
//            },
//        )
}