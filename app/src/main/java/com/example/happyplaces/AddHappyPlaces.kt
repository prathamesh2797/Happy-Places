package com.example.happyplaces

import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happy_places.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddHappyPlaces : AppCompatActivity(), View.OnClickListener {
    @RequiresApi(Build.VERSION_CODES.N)


    var calendar = Calendar.getInstance()
    private var saveImageToGallery: Uri? = null
    private var mLongitude : Double? = 0.0
    private var mLatitude : Double? = 0.0

    private var mHappyPlaceDetails: HappyPlaceModel? = null
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var currentLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_places)

        setSupportActionBar(tool_bar_add_place)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "ADD HAPPY PLACE"



       /* if (!Places.isInitialized()){
            Places.initialize(applicationContext, resources.getString(R.string.google_maps_api_key),Locale.US)
            Log.i("Places Initialized", " Initilize")
        }*/

        //var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)




        if (intent.hasExtra(HappyPlacesAdapter.EXTRA_PLACES_DETAILS)){
            mHappyPlaceDetails = intent.getSerializableExtra(HappyPlacesAdapter.EXTRA_PLACES_DETAILS) as HappyPlaceModel
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            getWindow().setStatusBarColor(getResources().getColor(R.color.indigo_dye))
        }

        tv_add_image.setOnClickListener(this)

        updateDateInView()

        if (mHappyPlaceDetails != null){
            supportActionBar?.title = "Edit Happy Place Details"
            et_title.setText(mHappyPlaceDetails!!.title)
            et_description.setText(mHappyPlaceDetails!!.description)
            et_date.setText(mHappyPlaceDetails!!.date)
            et_location.setText(mHappyPlaceDetails!!.location)
            mLatitude = mHappyPlaceDetails!!.latitude.toDouble()
            mLongitude = mHappyPlaceDetails!!.longitude.toDouble()

            saveImageToGallery = Uri.parse(Uri.parse(mHappyPlaceDetails!!.image).toString())

            iv_add_places.setImageURI(saveImageToGallery)
            btn_save.text ="UPDATE"
        }


        et_date.setOnClickListener{
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                     var dateString = "$dayOfMonth-${month+1}-$year"

                et_date.setText(dateString)

            },
                year,
                month,
                day


            )

            datePicker.show()
        }

        tv_add_image.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        et_location.setOnClickListener(this)
        tv_search_on_map.setOnClickListener(this)
        btn_get_current_location.setOnClickListener(this)


    }

    private fun isLocationEnabled(): Boolean{
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }





    private fun updateDateInView(){
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        et_date.setText(sdf.format(calendar.time).toString())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_add_image ->{
                //Toast.makeText(this, "add image clicked", Toast.LENGTH_SHORT).show()
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select Photo From Gallery", "Capture Photo From Camera")
                pictureDialog.setItems(pictureDialogItems){
                    dialog, which ->
                    when(which){
                        0->{
                            //Toast.makeText(this, "Opening Gallery", Toast.LENGTH_SHORT).show()
                            choosePhotoFromGallery()
                        }
                        1->{
                            takePhotoFromCamera()
                        }
                    }
                }
                pictureDialog.show()

            }

            R.id.btn_save ->{
                when{
                    et_title.text.isNullOrEmpty() ->{
                        Toast.makeText(this, "Please Enter Title", Toast.LENGTH_SHORT).show()
                    }
                    et_description.text.isNullOrEmpty() ->{
                        Toast.makeText(this, "Please Enter Description", Toast.LENGTH_SHORT).show()
                    }

                    et_location.text.isNullOrEmpty() ->{
                        Toast.makeText(this, "Please Enter Location", Toast.LENGTH_SHORT).show()
                    }

                    saveImageToGallery ==null -> {
                        Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show()
                    }

                    else ->{
                        val happyPlaceModel = HappyPlaceModel(
                            if (mHappyPlaceDetails ==null){
                                                          0
                                                          }else{
                                                               mHappyPlaceDetails!!.id
                                                               },
                            et_title.text.toString(),
                            saveImageToGallery.toString(),
                            et_description.text.toString(),
                            et_date.text.toString(),
                            et_location.text.toString(),
                            mLatitude.toString(),
                            mLongitude.toString()
                        )

                        val dbHandler = DatabaseHandler(this)

                        if (mHappyPlaceDetails == null){
                            val addHappyPlace = dbHandler.addPlaces(happyPlaceModel)

                            if (addHappyPlace >0){
                                Log.i("Test"," Test")
                                Toast.makeText(this, "Happy Place Details Saved Successfully", Toast.LENGTH_SHORT).show()

                                setResult(Activity.RESULT_OK)
                                finish()

                             /*   val mainActivity = MainActivity(this)
                                mainActivity.getHappyPlacesListFromDatabase()*/
                               /* val intent = Intent(this, AddHappyPlaces::class.java)
                                startActivity(intent)*/

                                //finish()

                            }
                        }  else{
                            val updateHappyPlace = dbHandler.updatePlaces(happyPlaceModel)
                            if (updateHappyPlace >0){
                                Toast.makeText(this, "Happy Place Details Updated Successfully", Toast.LENGTH_SHORT).show()
                                /*val mainActivity = MainActivity()
                                mainActivity.getHappyPlacesListFromDatabase()
                                setResult(Activity.RESULT_OK)

                                val intent = Intent(this, AddHappyPlaces::class.java)
                                startActivity(intent)
*/

                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }


                    }
                }



            }

            R.id.tv_search_on_map ->{

                locationAccess()
            }

            R.id.btn_get_current_location ->{
                if (!isLocationEnabled()){
                    Toast.makeText(applicationContext, "Please Enable Location Services For Using This Feature", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)

                }else{

                    getCurrentLocationAccess()
                    //locationAccess()
                }
            }

           /* R.id.et_location ->{
                //List Of Fields which needs to be passed
                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG
                    , Place.Field.ADDRESS)

                Log.i("Fields", fields.toString())

                //Start Auto complete intent

                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields)
                    .build(this)

                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            }*/
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY){
                if (data!= null){
                    val contentUri = data.data

                    try {
                        val selectedImage = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                        iv_add_places.setImageBitmap(selectedImage)
                        saveImageToGallery = saveImageToInternalStorage(selectedImage)
                        Log.i("Saved Image:", saveImageToGallery.toString())

                    }catch (e: Exception){
                        e.printStackTrace()
                        Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }else if (requestCode == CAMERA){
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                iv_add_places.setImageBitmap(thumbnail)
                saveImageToGallery = saveImageToInternalStorage(thumbnail)
                Log.i("Saved Image:", saveImageToGallery.toString())

            } else if (requestCode == LOCATION_ACCESS){
                if (resultCode == RESULT_OK){
                    val result = data?.getStringExtra("Location Details")
                    et_location.setText(result.toString())
                }
            }


        /*else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
                val place: Place =Autocomplete.getPlaceFromIntent(data!!)

                val status: Status = Autocomplete.getStatusFromIntent(data)
                Log.i("StatusOfApp", status.toString())
                et_location.setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
            }*/
        }


    /*else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            if (requestCode== PLACE_AUTOCOMPLETE_REQUEST_CODE){
                val status: Status = Autocomplete.getStatusFromIntent(data!!)
                Log.i("RESULT ERROR", status.toString())
            }

        }*/

    }


    private fun getCurrentLocationAccess(){
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
            .withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                    if (p0!!.areAllPermissionsGranted()){
                        getCurrentLocation()

                    }else if (p0.isAnyPermissionPermanentlyDenied){
                        showRationalDialogForLocationPermission()
                    }else{
                        Toast.makeText(
                            applicationContext,
                            "Location Permission Is Required For Using This Feature",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {

                    p1!!.continuePermissionRequest()
                }

            }).onSameThread().check()

        if (!isLocationEnabled()) {
            isLocationEnabledHandler()
        }
    }


    private fun locationAccess() {

            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(locationPermission: MultiplePermissionsReport?) {
                        if (locationPermission!!.areAllPermissionsGranted()) {
                            val intent = Intent(applicationContext, MapsActivity::class.java)
                            startActivityForResult(intent, LOCATION_ACCESS)
                            Toast.makeText(
                                applicationContext,
                                "Location Permission Granted",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else if (locationPermission.isAnyPermissionPermanentlyDenied) {
                            showRationalDialogForLocationPermission()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Location Permission Is Required For Using This Feature",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {

                        p1?.continuePermissionRequest()
                    }


                }).onSameThread().check()
        if (!isLocationEnabled()) {
           isLocationEnabledHandler()
        }
    }


    private fun isLocationEnabledHandler(){
        Toast.makeText(
            applicationContext,
            "Please enable your device location to use this feature",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun takePhotoFromCamera(){
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()){

                        val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(galleryIntent, CAMERA)

                        Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                    else if (p0.isAnyPermissionPermanentlyDenied){
                        showRationalDialogForPermission()
                    }else{
                        Toast.makeText(applicationContext, "Storage Permission Is Required To Access Gallery", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()


                }


            }).onSameThread().check()
    }

    private fun choosePhotoFromGallery(){
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()){

                            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            startActivityForResult(galleryIntent, GALLERY)

                        Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                    else if (p0.isAnyPermissionPermanentlyDenied){
                        showRationalDialogForPermission()
                    }else{
                        Toast.makeText(applicationContext, "Storage Permission Is Required To Access Gallery", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()


                }


            }).onSameThread().check()
    }

    internal fun showRationalDialogForLocationPermission(){
        AlertDialog.Builder(this)
            .setMessage("Location Permission Is Required For Accessing Location! You can enable it from App Settings")
            .setPositiveButton("Settings"){
                    _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e:Exception){
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel"){
                    dialog, which ->
                dialog.cancel()
                dialog.dismiss()
            }
            .show()
    }

    internal fun showRationalDialogForPermission(){
        AlertDialog.Builder(this)
            .setMessage("Storage Permission Is Required For Accessing Photo From Gallery! You can enable it from App Settings")
            .setPositiveButton("Settings"){
                _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e:Exception){
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel"){
                dialog, which ->
                dialog.cancel()
                dialog.dismiss()
            }
            .show()


    }


    private fun saveImageToInternalStorage(bitmap: Bitmap) : Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("HappyPlacesImages", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {

            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,stream)
            stream.flush()
            stream.close()

        }catch (e: IOException){
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }



    private fun getCurrentLocation(){
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocation.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null){
                currentLocation = location

                Toast.makeText(applicationContext, "Longitude: ${currentLocation!!.longitude} & Latitude: ${currentLocation!!.latitude}", Toast.LENGTH_SHORT).show()

                Log.i("Latitude", "${currentLocation!!.latitude.toString() }" )

                Log.i("Longitude", "${currentLocation!!.longitude.toString() }" )

                val geocoder: Geocoder = Geocoder(this)

                var list: List<Address> = ArrayList()
                try{
                    list = geocoder.getFromLocation(currentLocation!!.latitude, currentLocation!!.longitude,1)
                    val address = list[0].getAddressLine(0)
                    et_location.setText(address.toString())
                }catch (e: Exception){
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }


            }


        }
    }

    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE= 3
        private const val LOCATION_ACCESS=4

    }
}