package me.madhead.kafka.wikipedia.model

import java.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.madhead.kafka.wikipedia.model.serialization.EpochInstantSerializer

@Serializable
data class RecentChange(
    @SerialName("\$schema")
    val schema: String,
    val meta: Meta,
    val id: Int?,
    val type: String?,
    val title: String?,
    val namespace: Int?,
    val comment: String?,
    @SerialName("parsedcomment")
    val parsedComment: String?,
    @Serializable(with = EpochInstantSerializer::class)
    val timestamp: Instant,
    val user: String?,
    val bot: Boolean?,
    @SerialName("server_url")
    val serverUrl: String?,
    @SerialName("server_name")
    val serverName: String?,
    @SerialName("server_script_path")
    val serverScriptPath: String?,
    val wiki: String?,
    val minor: Boolean?,
    val patrolled: Boolean?,
    val length: Length?,
    val revision: Revision?,
)
