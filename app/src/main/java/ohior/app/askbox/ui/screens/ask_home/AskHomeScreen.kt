package ohior.app.askbox.ui.screens.ask_home

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import ohior.app.askbox.service.PermissionManager
import ohior.app.askbox.utils.AppConstants
import ohior.app.askbox.utils.RequestAction


@Composable
private fun EffectHandler(askHomeScreen: AskHomeScreenLogic, context: Context) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        Log.e("DEBUG ", it.toString())
    }
    LaunchedEffect(key1 = AppConstants.isChatModeEnabled) {
        askHomeScreen.resetVariables()
    }
    LaunchedEffect(key1 = null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionManager.checkPermissionGranted(context) { granted, permits ->
                if (!granted) {
                    launcher.launch(permits.toTypedArray())
                } else {
                    askHomeScreen.showNotification = true
                }
            }
        } else {
            askHomeScreen.showNotification = true
        }
    }
    DisposableEffect(key1 = null) {
        onDispose {
            if (askHomeScreen.showNotification) {
                askHomeScreen.createPeriodicWorkRequest()
            }
        }
    }
}

@Composable
private fun ColumnScope.QueryAIBot(askHomeScreen: AskHomeScreenLogic) {
    when (askHomeScreen.requestAction) {
        RequestAction.Loading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        RequestAction.Error -> {
            DisplayErrorMessage()
        }

        else -> {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 10.dp), contentAlignment = Alignment.BottomCenter
            ) {
                if (askHomeScreen.textResult.isNotEmpty()) {
                    MarkDown(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState()),
                        text = askHomeScreen.textResult,
                        shouldOpenUrlInBrowser = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.ChatAIBot(askHomeScreen: AskHomeScreenLogic) {
    val messages by askHomeScreen.messages.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .padding(bottom = 8.dp),
        state = rememberLazyListState(messages.size),
        verticalArrangement = Arrangement.Bottom
    ) {
        items(messages, key = { it.chatId }) { message ->
            val startPad = if (message.messenger == Messenger.USER) 50.dp else 8.dp
            val endPad = if (message.messenger == Messenger.MODEL) 50.dp else 8.dp
            Box(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                contentAlignment = if (message.messenger == Messenger.USER) Alignment.CenterEnd
                else Alignment.CenterStart
            ) {
                MarkdownText(
                    modifier = Modifier
                        .padding(start = startPad, end = endPad)
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(8.dp),
                    markdown = message.message
                )
            }
        }
    }
    when (askHomeScreen.requestAction) {
        RequestAction.Loading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        RequestAction.Error -> {
            DisplayErrorMessage()
        }

        else -> Unit
    }
}

@Composable
private fun DisplayErrorMessage() {
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
                .padding(8.dp)
        )
    }
}

@Composable
fun AskHomeScreen(modifier: Modifier) {
    val context = LocalContext.current
    val askHomeScreen = viewModel { AskHomeScreenLogic(context) }
    val focusManager = LocalFocusManager.current

    EffectHandler(askHomeScreen, context)
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (!AppConstants.isChatModeEnabled) {
            QueryAIBot(askHomeScreen = askHomeScreen)
        } else {
            ChatAIBot(askHomeScreen)
        }
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = askHomeScreen.textfield,
            onValueChange = { askHomeScreen.onValueChange(it) },
            trailingIcon = {
                IconButton(onClick = {
                    if (askHomeScreen.textfield.isNotBlank()) {
                        focusManager.clearFocus()
                        if (AppConstants.isChatModeEnabled) {
                            askHomeScreen.sendChatMessage()
                        } else {
                            askHomeScreen.getTextResult()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Text input is empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Icon(
                        imageVector = if (AppConstants.isChatModeEnabled) Icons.AutoMirrored.Outlined.Send
                        else Icons.Default.Search,
                        contentDescription = "ask AI bot"
                    )
                }
            }
        )
    }
}
