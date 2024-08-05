package ohior.app.askbox.model

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient
import io.objectbox.converter.PropertyConverter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
enum class Messenger { USER, MODEL }


@Entity
data class ChatMessage(
    @Id var id: Long = 0,
    val chatId: Long,
    val message: String,
    @Convert(converter = MessengerConverter::class, dbType = String::class)
    val messenger: Messenger
)


class MessengerConverter : PropertyConverter<Messenger, String> {
    override fun convertToDatabaseValue(entityProperty: Messenger?): String {
        return entityProperty?.let { Json.encodeToString(Messenger.serializer(), it) } ?: ""
    }

    override fun convertToEntityProperty(databaseValue: String?): Messenger {
        return databaseValue?.let { Json.decodeFromString(Messenger.serializer(), it) }
            ?: Messenger.USER
    }
}