package timisongdev.mytasks

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.lifecycle.viewModelScope
import com.google.accompanist.insets.imePadding
import kotlinx.coroutines.launch

class Chat {
    data class ChatMessage(
        val id: String,
        val text: String,
        val senderId: String,
        val timestamp: Long
    )

    companion object {

        private fun formatTimestamp(timestamp: Long): String {
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            return formatter.format(Date(timestamp))
        }

        @Composable
        fun ChatBlank(messages: List<ChatMessage>, onMessage: (String) -> Unit) {

            Column {
                LazyColumn (
                    Modifier
                        .fillMaxSize()
                        .weight(1F)
                        .padding(8.dp)
                ){
                    items(messages) { message ->
                        val time = formatTimestamp(message.timestamp)
                        Column (
                            Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                        ) {
                            Text(
                                message.senderId,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                message.text,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row (
                                Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Absolute.Right
                            ){
                                Text(
                                    time,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(Modifier.padding(8.dp))
                    }
                }

                val support = remember { mutableStateOf("") }

                Row (
                    Modifier
                        .fillMaxWidth()
                        .imePadding()
                ){
                    TextField(
                        value = support.value,
                        onValueChange = {
                                newText -> support.value = newText
                        },
                        Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxWidth(),
                        label = {
                            Text(
                                "Type your message for support"
                            )
                        },
                        trailingIcon = {
                            if (support.value.isNotEmpty()) {
                                Icon(
                                    Icons.Outlined.Send,
                                    "",
                                    Modifier
                                        .padding(16.dp)
                                        .clickable {
                                            onMessage(support.value)
                                            support.value = ""
                                        }
                                )
                            }
                        },
                    )
                }
            }
        }
    }

    class ChatViewModel(context: Context) : ViewModel() {
        private val chatDatabase = MyBaseData.DatabaseProvider.provideChatDatabase(context)
        private val _messages = mutableStateListOf<ChatMessage>()
        val messages: List<ChatMessage> get() = _messages

        init {
            loadMessagesFromDatabase()
        }

        private fun loadMessagesFromDatabase() {
            viewModelScope.launch {
                chatDatabase.chatDao().getAllMessages().collect { messageEntities ->
                    _messages.clear() // Clear the existing messages
                    val chatMessages = messageEntities.map { entity ->
                        ChatMessage(
                            id = entity.id,
                            text = entity.text,
                            senderId = entity.senderId,
                            timestamp = entity.timestamp
                        )
                    }
                    _messages.addAll(chatMessages)
                }
            }
        }

        fun sendMessage(text: String) {
            val newMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = text,
                senderId = "user_id", // Replace with actual user ID if needed
                timestamp = System.currentTimeMillis()
            )
            _messages.add(newMessage)

            // Save the message to the database
            viewModelScope.launch {
                chatDatabase.chatDao().insertMessage(
                    MyBaseData.MessageEntity(
                        id = newMessage.id,
                        text = newMessage.text,
                        senderId = newMessage.senderId,
                        timestamp = newMessage.timestamp
                    )
                )
            }
        }
    }
}