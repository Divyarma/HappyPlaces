 package com.learning.happyplace.happyplace.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learning.happyplace.R
import com.learning.happyplace.happyplace.adapters.rv_adapt
import com.learning.happyplace.happyplace.databases.DatabaseHandler
import com.learning.happyplace.happyplace.models.Happyplacemodel
import com.learning.happyplace.happyplace.utils.SwipeToDeleteCallback
import com.learning.happyplace.happyplace.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_main.*

 class MainActivity : AppCompatActivity() {

     companion object{
         val ADD_PLACE_CODE=1
     }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fabHappyPlace.setOnClickListener {
            val i=Intent(this, AddHappyPlace::class.java)
            startActivityForResult(i,ADD_PLACE_CODE)        }
        getData()
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if (requestCode== ADD_PLACE_CODE){
             if(resultCode==Activity.RESULT_OK){
                 getData()
             }
         }
     }

     private fun getData(){
         val db=DatabaseHandler(this)
         val places=db.getdata()
         if(places.size>0){
             rv.visibility=View.VISIBLE
             tex.visibility=View.GONE
             setuprecycler(places)
         }else{
             rv.visibility=View.GONE
             tex.visibility=View.VISIBLE
         }

     }

     private fun setuprecycler(hpl:ArrayList<Happyplacemodel>){
         rv.layoutManager=LinearLayoutManager(this)
         rv.setHasFixedSize(true)
         val adapter = rv_adapt(this,hpl)
         rv.adapter=adapter
         adapter.setOnClickListener(object : rv_adapt.OnclickListener{
             override fun onClick(position: Int, model: Happyplacemodel) {
                 val intent=Intent(this@MainActivity,HappyPlaceDetail::class.java)
                 intent.putExtra("model to display",model)
                 startActivity(intent)
             }
         } )

         val editSwipeHandler=object :SwipeToEditCallback(this){
             override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                 val adapt=rv.adapter as rv_adapt
                 adapt.notifyEditItem(this@MainActivity,viewHolder.adapterPosition, ADD_PLACE_CODE)
             }
         }

         val DeleteSwipeHandler=object : SwipeToDeleteCallback(this){
             override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                 val adapt=rv.adapter as rv_adapt
                 adapt.deleteitem(viewHolder.adapterPosition)
                 getData()
             }
         }

         val deleteItemTouchHelper=ItemTouchHelper(DeleteSwipeHandler)
         deleteItemTouchHelper.attachToRecyclerView(rv)

         val editItemTouchHelper=ItemTouchHelper(editSwipeHandler)
         editItemTouchHelper.attachToRecyclerView(rv)
     }
}