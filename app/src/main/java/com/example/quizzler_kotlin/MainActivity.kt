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

class MainActivity : AppCompatActivity() {
    lateinit var spinner: Spinner
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startButton : Button = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val intent: Intent = Intent(this, QuizzlerActivity::class.java)
            startActivity(intent)
        }
        val context = this

        // list of spinner items
        val list = mutableListOf(
            "Python",
            "Java",
            "C++"
        )
        spinner = findViewById(R.id.categorySpinner)
        // initialize an array adapter for spinner
        val adapter:ArrayAdapter<String> = object: ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            list
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
                view.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)

                // spinner item text color
                view.setTextColor(Color.parseColor("#0018A8"))

                // set selected item style
                if (position == spinner.selectedItemPosition){
                    view.background = ColorDrawable(Color.parseColor("#F0F8FF"))
                }

                return view
            }
        }

        // finally, data bind spinner with adapter
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext,"You selected " + adapter.getItem(position), Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }


}