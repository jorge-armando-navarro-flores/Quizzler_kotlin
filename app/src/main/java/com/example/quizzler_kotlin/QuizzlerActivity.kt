package com.example.quizzler_kotlin

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
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
    var student: Student? = null
    private var tts: TextToSpeechImplementation? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizzler)
        var bundle :Bundle ?=intent.extras
        student = Student()
        tts = TextToSpeechImplementation(this)
        var nick = bundle!!.getString("nickname")
        if (nick != null) {
            student!!.name = nick
        }
        var categoryId = bundle!!.getString("categoryId") // 1
        Toast.makeText(this, categoryId, Toast.LENGTH_SHORT).show()

        questionBank = categoryId?.let { getQuestionData(null, it) }
        quiz = QuizBrain(questionBank!!)
        scoreTextView = findViewById(R.id.scoreLabel)
        questionTextView = findViewById<TextView>(R.id.questionArea)
        getNextQuestion()
//        Log.d("CREATIOOOOOOOOOOOON", questionBank!![0].text)
        questionTextView = findViewById<View>(R.id.questionArea) as TextView


    }

    public override fun onDestroy() {
        super.onDestroy()
        tts!!.release()

    }



    fun getNextQuestion(){
        questionTextView?.setBackgroundColor(resources.getColor(R.color.white))
        scoreTextView?.setText("Score: ${quiz!!.score}")
        if(quiz?.stillHasQuestions() == true){
            val qText = quiz?.nextQuestion()
            questionTextView?.setText(qText)
        } else{
            questionTextView?.setText("You've reached the end of the quiz.")
            student?.score = quiz!!.score
            saveScore()
            openRanking()
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
            tts!!.newMessage("Well done")

            questionTextView?.text = "Right"
            questionTextView?.setBackgroundColor(resources.getColor(R.color.right))
        } else{
            tts!!.newMessage("Sorry, you are wrong")
            questionTextView?.text = "Wrong"
            questionTextView?.setBackgroundColor(resources.getColor(R.color.wrong))
        }



        Timer().schedule(2000) {
            runOnUiThread {
                getNextQuestion()

            }
        }

    }

    fun saveScore() {
//        val nombreDelContactoEditText = findViewById<EditText>(R.id.nombreDelContacto)
//        val nombreDelContacto = nombreDelContactoEditText.text.toString()
//
//        val telefonoDelContactoEditText = findViewById<EditText>(R.id.nombreDelContacto)
//        val telefonoDelContacto = telefonoDelContactoEditText.text.toString()

        val database = FirebaseDatabase.getInstance("https://quizzler-kotlin-default-rtdb.firebaseio.com/")
        val myRef = database.getReference("Students")

        myRef.child(student!!.name).setValue(student)

    }

    fun openRanking() {
        var intent: Intent = Intent(applicationContext, RankingActivity::class.java)
        startActivity(intent)

    }

    fun getQuestionData(view: View?, categoryId: String): List<Question> {
        val questionCoroutine = QuestionCoroutine()
        var questionData: JSONArray? = null
        try {
            var job = GlobalScope.launch{
                questionData =  questionCoroutine.getData("https://opentdb.com/api.php?amount=10&category=$categoryId&type=boolean", "results")
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