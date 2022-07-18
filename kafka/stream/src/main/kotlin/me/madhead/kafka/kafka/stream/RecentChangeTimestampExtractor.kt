package me.madhead.kafka.kafka.stream

import me.madhead.kafka.wikipedia.model.RecentChange
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.processor.TimestampExtractor

object RecentChangeTimestampExtractor : TimestampExtractor {
    override fun extract(record: ConsumerRecord<Any, Any>?, partitionTime: Long) =
        (record?.value() as? RecentChange)?.timestamp?.toEpochMilli() ?: -1
}
