package me.madhead.kafka.wikipedia.model

import java.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.madhead.kafka.wikipedia.model.serialization.ISOInstantSerializer

@Serializable
data class Meta(
    val uri: String?,
    @SerialName("request_id")
    val requestId: String?,
    val id: String,
    @SerialName("dt")
    @Serializable(with = ISOInstantSerializer::class)
    val timestamp: Instant,
    val domain: String?,
    val stream: String,
)
