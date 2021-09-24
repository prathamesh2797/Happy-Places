package com.example.happyplaces

import android.content.Intent
import android.location.LocationListener
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_add_happy_places.*
import kotlinx.android.synthetic.main.activity_happy_place_details.*

class HappyPlaceDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_details)

        setSupportActionBar(tool_bar_add_place_details)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "HAPPY PLACE DETAILS"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            getWindow().setStatusBarColor(getResources().getColor(R.color.indigo_dye))
        }


        tool_bar_add_place_details.setOnClickListener {
            onBackPressed()
        }

        var happyPlaceModel: HappyPlaceModel? = null

        if (intent.hasExtra(HappyPlacesAdapter.EXTRA_PLACES_DETAILS)){

            happyPlaceModel = intent.getSerializableExtra(HappyPlacesAdapter.EXTRA_PLACES_DETAILS) as HappyPlaceModel

        }

        if (happyPlaceModel != null){

            supportActionBar!!.title = happyPlaceModel!!.title

            iv_imageView.setImageURI(Uri.parse(happyPlaceModel.image))
            tv_description.text = happyPlaceModel.description
            tv_location.text = happyPlaceModel.location
        }

        btn_view_on_map.setOnClickListener {
            val intent = Intent(this, ViewOnMapActivity::class.java)
            intent.putExtra(SAVED_LOCATION, tv_location.text)
            startActivity(intent)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object{
        var SAVED_LOCATION ="Saved Location"
    }
}