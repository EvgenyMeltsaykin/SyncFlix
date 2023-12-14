package com.movies.syncflix.common.ffmpeg

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFprobeKit
import com.arthenica.ffmpegkit.SessionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class VideoFormatter(
    private val context: Context
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val state: MutableStateFlow<FormattingState> = MutableStateFlow(FormattingState.Init)

    sealed class FormattingState {
        data object Init : FormattingState()
        data object Start : FormattingState()
        data class End(val videoUri: String, val audioUri: String?) : FormattingState()
        data class Progress(val progress: Float) : FormattingState()
    }

    fun observerState(): Flow<FormattingState> {
        return state.asStateFlow()
    }

    suspend fun convertToMp4(fileUri: String) {
        state.emit(FormattingState.Init)
        val uri = Uri.parse(fileUri)
        val mime = MimeTypeMap.getSingleton()
        val type = mime.getExtensionFromMimeType(context.contentResolver.getType(uri))?.lowercase()

        if (type == "mp4") {
            state.emit(FormattingState.End(fileUri, null))
            return
        }
        state.emit(FormattingState.Start)
        val fileName = uri.getName(context)
        val inputAudioPath = FFmpegKitConfig.getSafParameterForRead(context, uri)
        val outputFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "SyncFlix").also { it.mkdir() }
        val outputAudioFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "SyncFlix").also { it.mkdir() }
        val outputPathVideo = outputFolder.absolutePath + "/${fileName}.mp4"
        val outputPathAudio = outputAudioFolder.absolutePath + "/${fileName}_audio.mp3"
        val duration = FFprobeKit.getMediaInformation(FFmpegKitConfig.getSafParameterForRead(context, uri)).mediaInformation.duration.toFloat()
        val inputVideoPath = FFmpegKitConfig.getSafParameterForRead(context, uri)

        FFmpegKit.executeAsync(
            /* command = */ "-i $inputAudioPath -preset ultrafast -b:a 192K -vn $outputPathAudio -i $inputVideoPath -preset ultrafast -c copy -movflags +faststart $outputPathVideo",
            /* completeCallback = */ { session ->
                when (session.state) {
                    SessionState.CREATED -> Unit
                    SessionState.RUNNING -> Unit
                    SessionState.FAILED -> Unit
                    SessionState.COMPLETED -> {
                        coroutineScope.launch {
                            state.emit(
                                FormattingState.End(
                                    videoUri = Uri.fromFile(File(outputPathVideo)).toString(),
                                    audioUri = Uri.fromFile(File(outputPathAudio)).toString()
                                )
                            )
                        }
                    }
                    null -> Unit
                }
            },
            /* logCallback = */ { log ->
                println(log.message)
            },
            /* statisticsCallback = */ { statistics ->
                val progress: Float = (statistics.time.toFloat() / 1000) / duration
                coroutineScope.launch {
                    state.emit(FormattingState.Progress(progress))
                }
            }
        )
    }
}

private fun Uri.getName(context: Context): String {
    val returnCursor = context.contentResolver.query(this, null, null, null, null) ?: return ""
    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val fileName = returnCursor.getString(nameIndex)
    returnCursor.close()
    return fileName.trim().replace(" ", "").substringBeforeLast(".")
}