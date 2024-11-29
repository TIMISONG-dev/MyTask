package timisongdev.mytasks

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

class MyBaseData {

    @Entity(tableName = "messages")
    data class MessageEntity(
        @PrimaryKey val id: String,
        val text: String,
        val senderId: String,
        val timestamp: Long
    )

    @Dao
    interface ChatDao {

        @Insert
        suspend fun insertMessage(message: MessageEntity)

        @Query("SELECT * FROM messages ORDER BY timestamp ASC")
        fun getAllMessages(): Flow<List<MessageEntity>>
    }

    @Database(entities = [MessageEntity::class], version = 1)
    abstract class ChatDatabase : RoomDatabase() {
        abstract fun chatDao(): ChatDao
    }

    object DatabaseProvider {
        fun provideChatDatabase(context: Context): ChatDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ChatDatabase::class.java,
                "chat_database"
            ).build()
        }
    }
}