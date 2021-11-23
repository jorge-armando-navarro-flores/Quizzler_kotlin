package com.example.quizzler_kotlin

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizzler_kotlin.models.Student
import com.google.firebase.database.*


class RankingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)



        val database = FirebaseDatabase.getInstance("https://quizzler-kotlin-default-rtdb.firebaseio.com/")
        val myRef = database.getReference("Students")

        val context = this
        val studentListView = findViewById<ListView>(R.id.studentListView)
        var valores = ArrayList<String>();
        val adapter:ArrayAdapter<String> = object: ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            valores
        ){
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
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
        studentListView.adapter = adapter


        myRef.orderByChild("score").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val nodes = dataSnapshot.children.toList()
                nodes.asReversed().forEach {
                    var student: Student? = it.getValue(Student::class.java)
//                    Toast.makeText(applicationContext, student?.name , Toast.LENGTH_LONG).show()
//                    valores.add(contacto?.nombre + " : " + contacto?.telefono)
                    adapter.add(student?.name + " : " + student?.score)
                }
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        )


    }

    override fun onBackPressed() {
        Log.d("CDA", "onBackPressed Called")
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}