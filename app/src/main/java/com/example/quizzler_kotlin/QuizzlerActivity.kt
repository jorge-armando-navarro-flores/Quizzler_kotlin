package com.example.quizzler_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import java.util.concurrent.ExecutionException

class QuizzlerActivity : AppCompatActivity() {
    var questionBank: List<Question>? = null
    var quiBrain: QuizBrain? = null
    var questionTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizzler)
        questionBank = getData(null)
        quiBrain = QuizBrain(questionBank!!)
        questionTextView = findViewById<TextView>(R.id.questionArea)
        getNextQuestion(null)
//        Log.d("CREATIOOOOOOOOOOOON", questionBank!![0].text)
        questionTextView = findViewById<View>(R.id.questionArea) as TextView
    }

    fun getNextQuestion(view: View?){
        val qText = quiBrain?.nextQuestion()
        questionTextView?.setText(qText)
    }

    fun getData(view: View?): List<Question> {
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
                var questionBank: MutableList<Question> = mutableListOf<Question>()
                for (i in 0 until questionData!!.length()) {
                    val question = questionData!!.getJSONObject(i)
                    val questionText = question.getString("question")
                    val questionAnswer = question.getString("correct_answer")
                    val newQuestion = Question(questionText, questionAnswer)
                    questionBank.add(newQuestion)
//                    Log.d("QUESTION", questionText)
                }
//                Log.d("CREATION", questionData!!.getString(0))
                return questionBank
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return listOf()
    }


}