package com.example.happyplaces

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnMarkerDragListener {


    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var currentLocation: Location? = null
    var addressNew: Address?= null
    private var mGoogleMap: GoogleMap? = null
    private var markerOptions: MarkerOptions? = null
    private var marker: Marker? = null
    var locationSaved: String?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        supportActionBar!!.hide()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

       setUpMap()
/*
        geoLocate()

*/

        et_search_bar.setOnEditorActionListener{v,actionId,event ->
           /* if (actionId==EditorInfo.IME_ACTION_SEARCH  || actionId ==EditorInfo.IME_ACTION_DONE
                ||event.action == KeyEvent.ACTION_DOWN
                ||event.action == KeyEvent.KEYCODE_ENTER)*/

            if (actionId == EditorInfo.IME_ACTION_SEARCH){

                val searchString = et_search_bar.text
                if (searchString.isNullOrEmpty()){
                    Toast.makeText(applicationContext, "Please Enter Location To Search", Toast.LENGTH_SHORT).show()

                }else{
                    geoLocateForSearch(searchString.toString())
                }
            }
            false
        }

    }

    private fun setUpMap(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUES_CODE)
            return
        }
        Log.i("Inside Set Up Map", "Set Up Map Started")

        //mMap.isMyLocationEnabled = true




        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null){
                currentLocation = location
                val supportMapFragment: SupportMapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                supportMapFragment.getMapAsync(this)
                geoLocate()
                //Toast.makeText(this, "Latitude: ${currentLocation!!.latitude} & Longitude: ${currentLocation!!.longitude} ", Toast.LENGTH_SHORT).show()

                Log.i("Current Location Direct", currentLocation.toString())
                /*
                val currentLatLong = LatLng(location.latitude, location.longitude)
                placesMarker(currentLatLong)
                Log.i("Current Location", currentLatLong.toString())
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
*/
            }/* else if(location == null){
                Toast.makeText(this, "Location is Null", Toast.LENGTH_SHORT).show()
            }*/
        }
    }




    private fun geoLocateForSearch(searchString: String){

        val geocoder: Geocoder = Geocoder(this)
        var list: List<Address> = ArrayList()


        try{
            list = geocoder.getFromLocationName(searchString,1)
            addressNew = list[0]
            val address = list[0].getAddressLine(0)
            Toast.makeText(applicationContext, "Address: ${address.toString()}", Toast.LENGTH_SHORT).show()

            moveCamera(LatLng(addressNew!!.latitude, addressNew!!.longitude),12f,addressNew!!.getAddressLine(0))

            locationSaved = address.toString()
        }catch (e:Exception){
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            Log.i("GeoLocate For Search", e.localizedMessage.toString() + e.message.toString())
        }

    }

    private fun geoLocate(){
        val geocoder: Geocoder = Geocoder(this)


        var list: List<Address> = ArrayList()


        try{
            list = geocoder.getFromLocation(currentLocation!!.latitude, currentLocation!!.longitude,1)

            val address = list[0].getAddressLine(0)
            Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show()
            locationSaved = address.toString()
            moveCamera(LatLng(currentLocation!!.latitude, currentLocation!!.longitude),12f,"I am here!")
            Log.i("Current Location", address.toString())
        }catch (e: Exception){
            //Toast.makeText(applicationContext, "This is error ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }



    }

    private fun moveCamera(latLng: LatLng, zoom:Float, title: String){

        marker?.remove()

        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        if (!title.equals("I am here!") ){
             markerOptions = MarkerOptions().position(latLng).title(title)
            markerOptions!!.draggable(true)
            marker =mGoogleMap!!.addMarker(markerOptions)


        }

    }

    override fun onMapReady(mMap: GoogleMap) {


        mGoogleMap = mMap




            val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        markerOptions = MarkerOptions().position(latLng).title("I am here!")
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
        mGoogleMap?.addMarker(markerOptions)
            ?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        markerOptions!!.draggable(true)

        mGoogleMap!!.setOnInfoWindowClickListener(this)
        mGoogleMap!!.setOnMarkerDragListener(this)
        mGoogleMap!!.setOnInfoWindowLongClickListener {
            val intent = Intent(applicationContext, AddHappyPlaces::class.java)
            intent.putExtra("Location Details", locationSaved.toString())
            setResult(RESULT_OK,intent)
            /*startActivity(intent)*/
            Toast.makeText(applicationContext, "Location Saved : $locationSaved", Toast.LENGTH_SHORT).show()
            finish()
        }




        Log.i("Set Up Map", "Set Up Map Started")

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when(requestCode){
            LOCATION_REQUES_CODE ->if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setUpMap()
/*
                geoLocate()
*/
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }



 /*   private fun placesMarker(currentLatLng: LatLng){
        val markerOptions = MarkerOptions().position(currentLatLng)
        markerOptions.title("Your Current Location")
        mMap.addMarker(markerOptions)
    }*/

    companion object{
        private const val LOCATION_REQUES_CODE =1
    }

    override fun onInfoWindowClick(p0: Marker?) {

        Toast.makeText(applicationContext, p0?.title.toString(), Toast.LENGTH_SHORT).show()
    }




    override fun onMarkerDragStart(p0: Marker?) {

        var position : LatLng = p0?.position!!
        Log.i("onMarkerDragStart", "Latitide: ${position.latitude} & Longitude: ${position.longitude}")
    }

    override fun onMarkerDrag(p0: Marker?) {
        var position : LatLng = p0?.position!!
        Log.i("onMarkerDrag", "Latitide: ${position.latitude} & Longitude: ${position.longitude}")    }

    override fun onMarkerDragEnd(p0: Marker?) {
        p0?.remove()
        var position : LatLng = p0?.position!!
        val geocoderNew = Geocoder(applicationContext)

        var address = geocoderNew.getFromLocation(position.latitude, position.longitude,1)
        val addressLine = address.get(0).getAddressLine(0)
       // moveCamera(LatLng(position.longitude, position.latitude),12f,addressLine.toString())
        //Toast.makeText(applicationContext, "Pin Dropped at ${addressLine.toString()}", Toast.LENGTH_SHORT).show()
        Log.i("onMarkerDragEnd", "Latitide: ${position.latitude} & Longitude: ${position.longitude}")


        var list: List<Address> = ArrayList()


        try{
            list = geocoderNew.getFromLocationName(addressLine,1)
            addressNew = list[0]
            val address = list[0].getAddressLine(0)
            Toast.makeText(applicationContext, "Address: ${address.toString()}", Toast.LENGTH_SHORT).show()

            moveCamera(LatLng(addressNew!!.latitude, addressNew!!.longitude),12f,addressNew!!.getAddressLine(0))

            locationSaved = address.toString()
            p0?.remove()
        }catch (e:Exception){
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}
