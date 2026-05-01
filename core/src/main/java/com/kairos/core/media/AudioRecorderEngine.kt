package com.kairos.core.media

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRecorderEngine @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var recorder: MediaRecorder? = null

    /** Start recording into [outputFile]. File must already exist (or be creatable). */
    fun start(outputFile: File) {
        stop() // ensure previous session cleaned up
        val rec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        try {
            rec.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioChannels(1)
                setAudioEncodingBitRate(96_000)
                setAudioSamplingRate(44_100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            recorder = rec
        } catch (e: Exception) {
            try { rec.release() } catch (_: Exception) {}
            throw e
        }
    }

    /**
     * Stop the active recording. Returns elapsed milliseconds.
     * Caller should track start time and subtract if needed.
     */
    fun stop() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (_: Exception) {
            // MediaRecorder.stop() throws if called before any data was recorded
        } finally {
            recorder = null
        }
    }

    fun cancel(outputFile: File) {
        stop()
        outputFile.delete()
    }

    val isRecording: Boolean get() = recorder != null
}
