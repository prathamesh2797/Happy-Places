package com.example.happyplaces

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ViewOnMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var savedLocation:String?= null
    private var address: Address? = null
    private var mGoogleMap: GoogleMap? = null
    private var markerOptions: MarkerOptions?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_on_map)

        supportActionBar!!.hide()

        val supportMapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.view_on_map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        savedLocation =intent.getStringExtra(HappyPlaceDetails.SAVED_LOCATION)
        Toast.makeText(applicationContext, savedLocation.toString(), Toast.LENGTH_SHORT).show()
        geoLocateForSearch(savedLocation.toString())
    }

    override fun onMapReady(p0: GoogleMap?) {

        mGoogleMap = p0

        var latLng = LatLng(address!!.latitude, address!!.longitude)

        Log.i("Longitude", address!!.longitude.toString())
        Log.i("Latitude", address!!.latitude.toString())



        markerOptions = MarkerOptions().position(latLng).title(address!!.getAddressLine(0))
        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12f))
        mGoogleMap!!.addMarker(markerOptions)
       // geoLocateForSearch(savedLocation.toString())


    }

    private fun geoLocateForSearch(searchString:String){
        val geocode: Geocoder = Geocoder(this)
        var list: List<Address> = ArrayList()


        try{
            list = geocode.getFromLocationName(savedLocation.toString(),1)
            address = list[0]
           val addressLine = list[0].getAddressLine(0)

      /*      var latLng = LatLng(address!!.longitude, address!!.latitude)
            markerOptions = MarkerOptions().position(latLng).title(address!!.getAddressLine(0))
            mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12f))
            mGoogleMap!!.addMarker(markerOptions)*/

        }catch (e:Exception){
            Toast.makeText(applicationContext, "Exception: ${e.message.toString()}", Toast.LENGTH_SHORT).show()
        }
    }
}