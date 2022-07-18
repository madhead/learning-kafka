package me.madhead.kafka.kafka.stream

import java.util.Properties
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import me.madhead.kafka.wikipedia.model.RecentChange
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Grouped
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.kstream.Suppressed
import org.apache.kafka.streams.kstream.TimeWindows
import org.apache.kafka.streams.state.WindowStore
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.message.SimpleMessage
import org.intellij.lang.annotations.Language

fun main(args: Array<String>) {
    val bootStrapServers = args[0]
    val sourceTopic = args[1]
    val sinkTopic = args[2]

    val logger = LogManager.getLogger("me.madhead.kafka.kafka.Stream")

    val streamsBuilder = StreamsBuilder()

    streamsBuilder.stream(
        sourceTopic,
        Consumed
            .`as`<Void, RecentChange>("wikipedia_recent_changes_stream")
            .withValueSerde(RecentChangeSerde)
            .withTimestampExtractor(RecentChangeTimestampExtractor)
            .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST),
    )
        .filter(
            { _, value -> value.bot == false && value.wiki != null },
            Named.`as`("filter_updates_from_bots_and_unknown_wikis"),
        )
        .selectKey(
            { _, value -> value.wiki!! },
            Named.`as`("key_by_wiki"),
        )
        .groupByKey(
            Grouped
                .`as`<String?, RecentChange?>("group_by_wiki")
                .withKeySerde(Serdes.String())
                .withValueSerde(RecentChangeSerde)
        )
        .windowedBy(
            TimeWindows
                .ofSizeAndGrace(1.minutes.toJavaDuration(), 15.seconds.toJavaDuration())
                .advanceBy(5.seconds.toJavaDuration()),
        )
        .count(
            Materialized
                .`as`<String?, Long?, WindowStore<Bytes, ByteArray>?>("mediawiki.recentchange.by_wiki")
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.Long())
                .withCachingDisabled()
        )
        .suppress(
            Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()),
        )
        .toStream(
            Named.`as`("ktable_to_kstream")
        )
        .map(
            { key, value -> KeyValue(key.key(), constructPayloadWithSchema(key.key(), value, key.window().end())) },
            Named.`as`("prepare_payload_with_schema")
        )
        .peek(
            { key, value -> logger.debug { SimpleMessage("$key: $value") } },
            Named.`as`("debug_the_stream"),
        )
        .to(
            sinkTopic,
            Produced
                .`as`<String?, String?>("wikipedia_recent_changes_stream_rate_per_minute_by_wiki")
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.String())
        )

    val topology = streamsBuilder.build()

    logger.info(topology.describe())

    val kafkaStreams = KafkaStreams(topology, streamsProperties(bootStrapServers))

    kafkaStreams.start()

    Runtime.getRuntime().addShutdownHook(Thread { kafkaStreams.close() })
}

private fun streamsProperties(bootStrapServers: String): Properties = mapOf(
    StreamsConfig.APPLICATION_ID_CONFIG to "stream",
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to bootStrapServers,
    StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.Void().javaClass.name,
    StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.Void().javaClass.name,
).toProperties()

@Language("JSON")
private fun constructPayloadWithSchema(wiki: String, rate: Long, time: Long) = """
{
    "schema": {
        "type": "struct",
        "name": "wikipedia_recent_changes_rate_by_wiki",
        "fields": [
            {
                "field": "wiki",
                "type": "string",
                "optional": false
                
            },
            {
                "field": "rate",
                "type": "int64",
                "optional": false
            },
            {
                "field": "time",
                "type": "int64",
                "optional": false
            }
        ]
    },
    "payload": {
        "wiki": "$wiki",
        "rate": $rate,
        "time": $time
    }
}
""".trimIndent()
