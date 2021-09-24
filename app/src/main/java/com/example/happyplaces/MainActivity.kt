package com.example.happyplaces

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.utils.SwipeToDeleteCallBack
import com.example.happyplaces.utils.SwipeToEditCallBack
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fa_action_button.setOnClickListener { view->

            val intent = Intent(this, AddHappyPlaces::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)

        }

        getHappyPlacesListFromDatabase()

    }

    private fun setUpHappyPlacesRecyclerView(happyPlaceList : ArrayList<HappyPlaceModel>){
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
        recyclerView.adapter = placesAdapter

        val swipeHandler= object : SwipeToEditCallBack(this){

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }

        }

        val editItemTouchHelper = ItemTouchHelper(swipeHandler)
        editItemTouchHelper.attachToRecyclerView(recyclerView)



        val deleteSwipeHandler= object : SwipeToDeleteCallBack(this){

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getHappyPlacesListFromDatabase()
            }

        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(recyclerView)
        //getHappyPlacesListFromDatabase()

    }


     fun getHappyPlacesListFromDatabase(){
        val dbHandler= DatabaseHandler(this)
        val getHappyPlaceList : ArrayList<HappyPlaceModel> = dbHandler.getHappyPlacesList()

        if (getHappyPlaceList.size >0){
            recyclerView.visibility = View.VISIBLE
            setUpHappyPlacesRecyclerView(getHappyPlaceList)

        }else{
            recyclerView.visibility = View.GONE
            Toast.makeText(this, "No Happy Places Present :(", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                getHappyPlacesListFromDatabase()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object{
        private val ADD_PLACE_ACTIVITY_REQUEST_CODE =1
    }
}