package com.example.quizzler_kotlin

class QuizBrain(qList : List<Question>) {
    var questionNumber : Int = 0
    var score : Int = 0
    val questionList = qList
    var currentQuestion : Question? = null

    fun stillHasQuestions() : Boolean{
        return questionNumber < questionList.size
    }

    fun nextQuestion() : String{
        currentQuestion = questionList[questionNumber]
        questionNumber += 1
        val qText : String = currentQuestion!!.text
        return "Q.$questionNumber: $qText (True/False): "
    }

    fun checkAnswer(userAnswer : String) : Boolean{
        val correctAnswer : String = currentQuestion?.answer ?: ""
        if(userAnswer.lowercase() == correctAnswer.lowercase()){
            score += 1
            return true
        } else{
            return false
        }
        println("Your current score is: $score/$questionNumber")
    }

}