package me.madhead.kafka.kafka.wikipedia.source.connector

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.madhead.kafka.wikipedia.client.WikipediaClient
import me.madhead.kafka.wikipedia.model.RecentChange
import org.apache.kafka.common.config.ConfigException
import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.source.SourceTask

class WikipediaSourceTask : SourceTask() {
    private lateinit var topic: String
    private val scope = CoroutineScope(Dispatchers.IO)
    private val channel = Channel<RecentChange>(Channel.CONFLATED)
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    override fun version() = VERSION

    override fun start(props: MutableMap<String, String>) {
        topic = props[CONFIG_TOPIC] ?: throw ConfigException("Missing $CONFIG_TOPIC configuration parameter")
        scope.launch {
            WikipediaClient.recentChanges.collect {
                channel.send(it)
            }
        }
    }

    override fun stop() {
        scope.cancel()
    }

    override fun poll(): List<SourceRecord> {
        return channel.tryReceive().takeIf { it.isSuccess }?.getOrNull()?.let { event ->
            listOf(
                SourceRecord(
                    /* sourcePartition = */ mapOf("domain" to event.meta.domain),
                    /* sourceOffset = */ mapOf("dt" to event.meta.timestamp.toEpochMilli()),
                    /* topic = */ topic,
                    /* valueSchema = */ Schema.BYTES_SCHEMA,
                    /* value = */ json.encodeToString(event).encodeToByteArray(),
                )
            )
        } ?: emptyList()
    }
}
