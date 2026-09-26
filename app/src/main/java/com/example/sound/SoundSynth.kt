package com.example.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundSynth {
  private val scope = CoroutineScope(Dispatchers.Default)
  var isSoundEnabled: Boolean = true

  fun playSlimeSquish() {
    if (!isSoundEnabled) return
    scope.launch {
      playToneSweep(startFreq = 300f, endFreq = 750f, durationMs = 90, shape = "sine")
    }
  }

  fun playCoinSound() {
    if (!isSoundEnabled) return
    scope.launch {
      playTone(987.77f, 60) // B5
      playTone(1318.51f, 120) // E6
    }
  }

  fun playUpgradeSound() {
    if (!isSoundEnabled) return
    scope.launch {
      playTone(523.25f, 50) // C5
      playTone(659.25f, 50) // E5
      playTone(783.99f, 50) // G5
      playTone(1046.50f, 100) // C6
    }
  }

  fun playMutationSound() {
    if (!isSoundEnabled) return
    scope.launch {
      playToneSweep(400f, 1200f, 180, shape = "sine")
      playTone(1396.91f, 150)
      playTone(1760.00f, 200)
    }
  }

  fun playRebirthSound() {
    if (!isSoundEnabled) return
    scope.launch {
      playToneSweep(200f, 1600f, 350, shape = "sine")
      playTone(1567.98f, 150)
      playTone(2093.00f, 300)
    }
  }

  private fun playTone(freq: Float, durationMs: Int) {
    try {
      val sampleRate = 44100
      val numSamples = (durationMs * sampleRate) / 1000
      val buffer = ShortArray(numSamples)
      val twoPiF = 2.0 * Math.PI * freq / sampleRate

      for (i in 0 until numSamples) {
        val envelope = (1.0 - (i.toDouble() / numSamples)) // Decay envelope
        val sample = sin(twoPiF * i) * envelope * 0.4
        buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
      }

      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(buffer.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      track.write(buffer, 0, buffer.size)
      track.play()
      Thread.sleep((durationMs + 20).toLong())
      track.release()
    } catch (_: Exception) {}
  }

  private fun playToneSweep(startFreq: Float, endFreq: Float, durationMs: Int, shape: String = "sine") {
    try {
      val sampleRate = 44100
      val numSamples = (durationMs * sampleRate) / 1000
      val buffer = ShortArray(numSamples)

      var phase = 0.0
      for (i in 0 until numSamples) {
        val t = i.toDouble() / numSamples
        val currentFreq = startFreq + (endFreq - startFreq) * t
        val phaseInc = 2.0 * Math.PI * currentFreq / sampleRate
        phase += phaseInc

        val envelope = when {
          t < 0.1 -> t / 0.1
          else -> (1.0 - t)
        }
        val sample = sin(phase) * envelope * 0.4
        buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
      }

      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(buffer.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      track.write(buffer, 0, buffer.size)
      track.play()
      Thread.sleep((durationMs + 20).toLong())
      track.release()
    } catch (_: Exception) {}
  }
}
