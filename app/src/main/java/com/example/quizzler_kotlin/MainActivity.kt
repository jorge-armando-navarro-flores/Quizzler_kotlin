package com.example.quizzler_kotlin

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import java.util.concurrent.ExecutionException

class MainActivity : AppCompatActivity() {
    lateinit var spinner: Spinner
    var categories: List<Category>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        categories = getCategoryData(null)
        var categoryId: String = "9"
        val categoryNames: MutableList<String> = mutableListOf()

        for( category in categories!!){
            categoryNames.add(category.name)
        }
        val context = this

        // list of spinner items

        var nickname: EditText = findViewById(R.id.nicknameEditText)
        spinner = findViewById(R.id.categorySpinner)
        // initialize an array adapter for spinner
        val adapter:ArrayAdapter<String> = object: ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            categoryNames
        ){
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view:TextView = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView
                // set item text bold and monospace font
                view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)

                // spinner item text color
                view.setTextColor(Color.parseColor("#338FF3"))

                // set selected item style
//                if (position == spinner.selectedItemPosition){
//                    view.background = ColorDrawable(Color.parseColor("#338FF3"))
//                }

                return view
            }
        }

        // finally, data bind spinner with adapter
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                Toast.makeText(applicationContext,"You selected " + adapter.getItem(position) + " " + categories!![position].id, Toast.LENGTH_SHORT).show()
                categoryId = categories!![position].id
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val startButton : Button = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val intent: Intent = Intent(this, QuizzlerActivity::class.java)
            intent.putExtra("nickname", nickname.text.toString())
            intent.putExtra("categoryId", categoryId)
            startActivity(intent)
        }

    }

    fun getCategoryData(view: View?): List<Category> {
        val questionCoroutine = QuestionCoroutine()
        var categoryData: JSONArray? = null
        try {
            var job = GlobalScope.launch{
                categoryData =  questionCoroutine.getData("https://opentdb.com/api_category.php", "trivia_categories")
            }
            runBlocking {
                job.join()

            }
            if (categoryData != null) {
                var categories: MutableList<Category> = mutableListOf<Category>()
                for (i in 0 until categoryData!!.length()) {
                    val category = categoryData!!.getJSONObject(i)
                    val categoryName = category.getString("name")
                    val categoryId = category.getString("id")
                    val newCategory = Category(categoryName, categoryId)
                    categories.add(newCategory)
//                    Log.d("QUESTION", questionText)

                }
//                Log.d("CREATION", questionData!!.getString(0))
                return categories
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return listOf()
    }


}