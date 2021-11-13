package com.example.quizzler_kotlin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import java.util.*
import java.util.concurrent.ExecutionException
import kotlin.concurrent.schedule

class QuizzlerActivity : AppCompatActivity() {
    var questionBank: List<Question>? = null
    var quiz: QuizBrain? = null
    var questionTextView: TextView? = null
    var scoreTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizzler)
        questionBank = getData(null)
        quiz = QuizBrain(questionBank!!)
        scoreTextView = findViewById(R.id.scoreLabel)
        questionTextView = findViewById<TextView>(R.id.questionArea)
        getNextQuestion()
//        Log.d("CREATIOOOOOOOOOOOON", questionBank!![0].text)
        questionTextView = findViewById<View>(R.id.questionArea) as TextView
    }

    fun getNextQuestion(){
        questionTextView?.setBackgroundColor(resources.getColor(R.color.white))
        scoreTextView?.setText("Score: ${quiz!!.score}")
        if(quiz?.stillHasQuestions() == true){
            val qText = quiz?.nextQuestion()
            questionTextView?.setText(qText)
        } else{
            questionTextView?.setText("You've reached the end of the quiz.")

        }

    }

    fun truePressed(view: View?){
        val isRight: Boolean = quiz?.checkAnswer("True") == true
        giveFeedback(isRight)
    }

    fun falsePressed(view: View?){
        val isRight: Boolean = quiz?.checkAnswer("False") == true
        giveFeedback(isRight)
    }

    fun giveFeedback(isRight: Boolean){
        if(isRight){
            questionTextView?.text = "Right"
            questionTextView?.setBackgroundColor(resources.getColor(R.color.right))
        } else{
            questionTextView?.text = "Wrong"
            questionTextView?.setBackgroundColor(resources.getColor(R.color.wrong))
        }



        Timer().schedule(2000) {
            runOnUiThread {
                getNextQuestion()
            }
        }

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