package com.example.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private var recyclerView :RecyclerView?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectView()
        prepareRecycler()
    }

    private fun connectView(){
        recyclerView = findViewById(R.id.recycler)

    }
    private fun  prepareRecycler(){

        val array:ArrayList<Person> = ArrayList()
        array.add(Person("khaled",R.drawable.profile))
        array.add(Person("Ahmad",R.drawable.profile))
        array.add(Person("Mohammed",R.drawable.profile))
        array.add(Person("sarah",R.drawable.profile))
        array.add(Person("faisal",R.drawable.profile))
        array.add(Person("Ali",R.drawable.profile))
        array.add(Person("Ayman",R.drawable.profile))

        val customAdapter:CustomAdapter = CustomAdapter(array)

        recyclerView?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerView?.adapter = customAdapter

        /*GridLayOut كود العرض الشبكي

         recyclerView?.layoutManager = GridLayoutManager( context:this,spanCount:3)


         */
    }
}