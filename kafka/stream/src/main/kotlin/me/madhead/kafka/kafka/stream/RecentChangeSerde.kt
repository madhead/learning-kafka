package me.madhead.kafka.kafka.stream

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.madhead.kafka.wikipedia.model.RecentChange
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

object RecentChangeSerde : Serde<RecentChange> {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    override fun serializer() = Serializer<RecentChange> { _, data ->
        json.encodeToString(data).encodeToByteArray()
    }

    override fun deserializer() = Deserializer<RecentChange> { _, data ->
        json.decodeFromString(data.decodeToString())
    }
}
