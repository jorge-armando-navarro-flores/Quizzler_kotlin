package com.example.quizzler_kotlin

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
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
    var trueButton: ImageButton? = null
    var falseButton: ImageButton? = null
    var counter: Int = 10
    var timer: CountDownTimer? =null
    var counterTextView: TextView? = null
    var nickTextView: TextView? = null
    private var tts: TextToSpeechImplementation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizzler)

        var bundle :Bundle ?=intent.extras
        student = Student()
        tts = TextToSpeechImplementation(this)
        trueButton = findViewById(R.id.trueButton)
        falseButton = findViewById(R.id.falseButton)
        counterTextView = findViewById(R.id.counterTextView)
        nickTextView = findViewById(R.id.nickTextView)
        var nick = bundle!!.getString("nickname")
        if (nick != null) {
            student!!.name = nick
            nickTextView!!.text = nick
        }
        var categoryId = bundle!!.getString("categoryId") // 1
//        Toast.makeText(this, categoryId, Toast.LENGTH_SHORT).show()
        questionBank = categoryId?.let { getQuestionData(null, it) }
        quiz = QuizBrain(questionBank!!)
        scoreTextView = findViewById(R.id.scoreLabel)
        questionTextView = findViewById(R.id.questionArea)
        getNextQuestion()
        questionTextView = findViewById<View>(R.id.questionArea) as TextView
    }

    public override fun onDestroy() {
        super.onDestroy()
        tts!!.release()
    }

    fun startCounter(){
        if(timer != null){
            timer?.cancel()
        }

        counter = 9
        timer = object: CountDownTimer(11000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                counterTextView?.text = counter.toString()
                Log.d("COUNTERRRRRRR", "$counter")
                counter -= 1
            }
            override fun onFinish() {
                giveFeedback(false, true)
            }
        }
        (timer as CountDownTimer).start()
    }

    fun getNextQuestion(){
        trueButton?.setEnabled(true)
        falseButton?.setEnabled(true)
        questionTextView?.setBackgroundColor(resources.getColor(R.color.white))
        scoreTextView?.setText("Score: ${quiz!!.score}")

        if(quiz?.stillHasQuestions() == true){
            val qText = quiz?.nextQuestion()
            questionTextView?.setText(qText)
            startCounter()
        } else{
            questionTextView?.setText("You've reached the end of the quiz.")
            student?.score = quiz!!.score
            saveScore()
        }
    }

    fun truePressed(view: View?){
        val isRight: Boolean = quiz?.checkAnswer("True") == true
        giveFeedback(isRight, false)
    }

    fun falsePressed(view: View?){
        val isRight: Boolean = quiz?.checkAnswer("False") == true
        giveFeedback(isRight, false)
    }

    fun giveFeedback(isRight: Boolean, timeUp: Boolean){
        if(timer != null){
            timer?.cancel()
        }
        trueButton?.setEnabled(false)
        falseButton?.setEnabled(false)
        if(timeUp){
            tts!!.newMessage("Sorry, Time up")
            questionTextView?.text = "Time up"
            questionTextView?.setBackgroundColor(resources.getColor(R.color.wrong))
        }else{
            if(isRight){
                tts!!.newMessage("Well done")

                questionTextView?.text = "Right"
                questionTextView?.setBackgroundColor(resources.getColor(R.color.right))
            } else{
                tts!!.newMessage("Sorry, you are wrong")
                questionTextView?.text = "Wrong"
                questionTextView?.setBackgroundColor(resources.getColor(R.color.wrong))
            }
        }

        Timer().schedule(2000) {
            runOnUiThread {
                getNextQuestion()
            }
        }
    }

    fun saveScore() {
//
        val database = FirebaseDatabase.getInstance("https://quizzler-kotlin-default-rtdb.firebaseio.com/")
        val myRef = database.getReference("Students")

        myRef.child(student!!.name).child("score").addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue<Int>()
//                Toast.makeText(applicationContext, value.toString(), Toast.LENGTH_SHORT).show()
//                Log.d("SCOREEEEEEEEE", "Value is: " + value)
                if(student!!.score > value!!){
                    myRef.child(student!!.name).setValue(student)
                }
                openRanking()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
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