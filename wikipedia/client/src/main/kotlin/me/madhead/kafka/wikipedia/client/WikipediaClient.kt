package me.madhead.kafka.wikipedia.client

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.madhead.kafka.wikipedia.model.RecentChange
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

object WikipediaClient {
    val recentChanges: Flow<RecentChange>
        get() = callbackFlow {
            val client = OkHttpClient.Builder().build()
            val request = Request.Builder()
                .url("https://stream.wikimedia.org/v2/stream/recentchange")
                .header("Accept", "text/event-stream")
                .build()
            val json = Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }
            val listener = object : EventSourceListener() {
                override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                    if (type == "message") {
                        this@callbackFlow.trySendBlocking(json.decodeFromString(data))
                    }
                }

                override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                    this@callbackFlow.close(t)
                }

                override fun onClosed(eventSource: EventSource) {
                    this@callbackFlow.close()
                }
            }
            val events = EventSources.createFactory(client).newEventSource(request, listener)

            awaitClose { events.cancel() }
        }
}
