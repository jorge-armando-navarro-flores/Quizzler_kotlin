package com.example.quizzler_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import java.util.concurrent.ExecutionException

class QuizzlerActivity : AppCompatActivity() {

    var minTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizzler)
        minTextView = findViewById<View>(R.id.questionArea) as TextView
        getData(null)


    }

    fun getData(view: View?) {
        val questionCoroutine = QuestionCoroutine()
        var questionData: JSONArray? = null
        try {
            var job = GlobalScope.launch{
                questionData =  questionCoroutine.getQuestionsData()
            }
            runBlocking {
                job.join()
            }
            if (questionData != null) {
                Log.d("CREATION", questionData!!.getString(0))

            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

}