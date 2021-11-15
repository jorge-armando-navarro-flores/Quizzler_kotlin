package com.example.quizzler_kotlin

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class TextToSpeechImplementation(private val context: Context) : TextToSpeech.OnInitListener {
    private val textToSpeech: TextToSpeech
    private val bufferedMessages: ConcurrentLinkedQueue<String>
    private var isReady = false
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale("en", "US")
            synchronized(this) {
                isReady = true
                for (message in bufferedMessages) {
                    speak(message)
                }
                bufferedMessages.clear()
            }
        }
    }

    private fun speak(message: String) {
        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_STREAM] = "STREAM_NOTIFICATION"
        textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, params)
        textToSpeech.playSilence(500, TextToSpeech.QUEUE_ADD, params)
    }

    fun newMessage(message: String) {
        synchronized(this) {
            if (isReady) {
                speak(message)
            } else {
                bufferedMessages.add(message)
            }
        }
    }

    fun release() {
        synchronized(this) {
            textToSpeech.shutdown()
            isReady = false
        }
    }

    init {
        textToSpeech = TextToSpeech(context, this)
        bufferedMessages = ConcurrentLinkedQueue()
    }
}