package com.movies.syncflix.backend.server.routing

import android.content.Context
import androidx.core.net.toUri
import com.movies.syncflix.data.common.RuntimeServerRepository
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.util.cio.toByteReadChannel
import io.ktor.utils.io.consumeEachBufferRange
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers

internal fun Routing.videoRouting(
    context: Context,
    runtimeServerRepository: RuntimeServerRepository
) {
    get("/video") {
        val videoUri = runtimeServerRepository.currentVideo?.toUri() ?: return@get
        val inputStream = context.contentResolver.openInputStream(videoUri)
        if (inputStream == null) {
            call.respond("Error open input stream")
            return@get
        }
        val fileDescriptor = context.contentResolver.openAssetFileDescriptor(videoUri, "r")
        if (fileDescriptor == null) {
            call.respond("Error create fileDescriptor")
            return@get
        }
        val fileSize = fileDescriptor.length.also { fileDescriptor.close() }
        call.respondBytesWriter(contentType = ContentType.parse("video/mp4"), contentLength = fileSize) {

            inputStream.toByteReadChannel(
                context = Dispatchers.IO
            ).consumeEachBufferRange { buffer, last ->
                this.writeFully(buffer)
                !last
            }.also { inputStream.close() }
        }
    }

    get("/audio") {
        val videoUri = runtimeServerRepository.currentAudio?.toUri() ?: return@get
        val inputStream = context.contentResolver.openInputStream(videoUri)
        if (inputStream == null) {
            call.respond("Error open input stream")
            return@get
        }
        val fileDescriptor = context.contentResolver.openAssetFileDescriptor(videoUri, "r")
        if (fileDescriptor == null) {
            call.respond("Error create fileDescriptor")
            return@get
        }
        val fileSize = fileDescriptor.length.also { fileDescriptor.close() }
        call.respondBytesWriter(contentType = ContentType.parse("audio/mpeg"), contentLength = fileSize) {
            val output = this
            inputStream.buffered().use {
                it.copyTo(output)
            }.also {
                inputStream.close()
            }
        }
    }
}