package com.movies.syncflix.backend.routing

import android.content.Context
import androidx.core.net.toUri
import com.movies.syncflix.data.DataRepository
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.util.cio.toByteReadChannel
import io.ktor.utils.io.consumeEachBufferRange

fun Routing.videoRouting(
    context: Context,
    dataRepository: DataRepository
) {
    get("/video") {
        //val fileUri = this.call.parameters["fileUri"] ?: error("Empty url parameter")
        val inputStream = context.contentResolver.openInputStream(dataRepository.currentVideo.toUri()) ?: error("Error open input stream")
        val fileDescriptor = context.contentResolver.openAssetFileDescriptor(dataRepository.currentVideo.toUri(), "r") ?: error("Error create fileDescriptor")
        val fileSize = fileDescriptor.length.also { fileDescriptor.close() }

        call.respondBytesWriter(contentType = ContentType("video", "mp4"), contentLength = fileSize) {
            inputStream.toByteReadChannel().consumeEachBufferRange { buffer, last ->
                this.writeFully(buffer)
                !last
            }.also { inputStream.close() }
        }
    }
}