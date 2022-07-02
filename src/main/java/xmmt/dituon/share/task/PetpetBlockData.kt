package xmmt.dituon.share.task

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import xmmt.dituon.share.Type

@Serializable
data class PetpetBlockDTO constructor(
    val type: Type,
    val initTask: DrawTaskDTO,
    val frameTasks: List<FrameBlockDTO>,
)

@Serializable
data class FrameBlockDTO constructor(
    val tasks: List<DrawTaskDTO>
)

enum class DrawTaskType {
    Empty,
    DRAW_BACKGROUND,
    CONTEXT_MODIFY,
    DRAW_IMAGE,
    DRAW_TEXT
}

@Serializable(with = DrawTaskDTOSerializer::class)
abstract class DrawTaskDTO {
    abstract val type: DrawTaskType
}

@Serializable
data class EmptyTaskDTO(override val type: DrawTaskType) : DrawTaskDTO()

@Serializable
data class DrawBackgroundTaskDTO constructor(
    override val type: DrawTaskType,
    val transparent: Boolean
) : DrawTaskDTO()

@Serializable
data class ContextModifyTaskDTO constructor(
    override val type: DrawTaskType,
    val antialias: Boolean? = null,
    val colorRgb: Triple<Int, Int, Int>? = null,
    val colorHex: String? = null,
    val fontName: String? = null,
) : DrawTaskDTO()

@Serializable
data class DrawImageTaskDTO constructor(
    override val type: DrawTaskType,
    val imagePath: String? = null,
    val imageModify: ImageModifyDTO? = null,
) : DrawTaskDTO()

@Serializable
data class ImageModifyDTO constructor(
    val angle: Float? = null,
    val round: Boolean? = null,
    var vertexPosList: List<Pair<Int, Int>>? = null
)

object DrawTaskDTOSerializer : JsonContentPolymorphicSerializer<DrawTaskDTO>(DrawTaskDTO::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out DrawTaskDTO> {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
        return when {
            type.equals(DrawTaskType.DRAW_BACKGROUND.name) -> DrawBackgroundTaskDTO.serializer()
            type.equals(DrawTaskType.CONTEXT_MODIFY.name) -> ContextModifyTaskDTO.serializer()
            type.equals(DrawTaskType.DRAW_IMAGE.name) -> DrawImageTaskDTO.serializer()
            else -> EmptyTaskDTO.serializer()
        }
    }
}



/*object DrawTaskDTOSerializer : JsonContentPolymorphicSerializer<IDrawTaskDTO>(IDrawTaskDTO::class) {
    override fun selectDeserializer(element: JsonElement) = {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
        when {
                .equals(DrawTaskType.DrawBackground.name) -> DrawBackgroundTaskDTO.serializer()
            else -> EmptyTaskDTO.serializer()
        }
    }
}*/

fun main() {
    val data = FrameBlockDTO(
        listOf(
            DrawBackgroundTaskDTO(DrawTaskType.DRAW_BACKGROUND, true)
        )
    )
    val string = Json.encodeToString(data)
    println(string)
    val data2 : FrameBlockDTO = Json.decodeFromString(string)
    println(data2)
}