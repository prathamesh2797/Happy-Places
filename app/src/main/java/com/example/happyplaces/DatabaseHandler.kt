package com.example.happyplaces

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, "HappyPlacesDatabase",null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        val HAPPY_PLACES_TABLE = ("CREATE TABLE HappyPlacesTable(_id INTEGER PRIMARY KEY, title TEXT," +
                "image TEXT, description TEXT, date TEXT, location TEXT, latitude TEXT, longitude TEXT)")

        db!!.execSQL(HAPPY_PLACES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db!!.execSQL("DROP TABLE IF EXISTS HappyPlacesTable")
        onCreate(db)
    }


    fun addPlaces(happyPlaces : HappyPlaceModel):Long{

        val db =this.writableDatabase
        val contentValues= ContentValues()

        contentValues.put("title", happyPlaces.title)
        contentValues.put("image", happyPlaces.image)
        contentValues.put("description", happyPlaces.description)
        contentValues.put("date", happyPlaces.date)
        contentValues.put("location", happyPlaces.location)
        contentValues.put("latitude", happyPlaces.latitude)
        contentValues.put("longitude", happyPlaces.longitude)

        val success = db.insert("HappyPlacesTable", null, contentValues)
        db.close()

        return success

    }

    fun updatePlaces(happyPlaces: HappyPlaceModel) : Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put("title", happyPlaces.title)
        contentValues.put("image", happyPlaces.image)
        contentValues.put("description", happyPlaces.description)
        contentValues.put("date", happyPlaces.date)
        contentValues.put("location", happyPlaces.location)
        contentValues.put("latitude", happyPlaces.latitude)
        contentValues.put("longitude", happyPlaces.longitude)

        val success = db.update("HappyPlacesTable",contentValues,"_id = ${happyPlaces.id}",null)

        db.close()

        return success
    }

    fun deleteHappyPlaces(happyPlaces: HappyPlaceModel): Int{

        val db = this.writableDatabase
        val contentValues =ContentValues()
        contentValues.put("_id", happyPlaces.id)

        val success =db.delete("HappyPlacesTable","_id = ${happyPlaces.id}", null)
        db.close()
        return success
    }

    fun getHappyPlacesList(): ArrayList<HappyPlaceModel>{

        val happyPlaceList :ArrayList<HappyPlaceModel> = ArrayList<HappyPlaceModel>()
        val selectQuery = "SELECT * FROM HappyPlacesTable"
        val db = this.readableDatabase

        var cursor: Cursor? = null

        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLException){
            db.execSQL(selectQuery)
            return ArrayList()
        }


        var id: Int
        var title: String
        var image: String
        var description: String
        var date: String
        var location: String
        var latitude: String
        var longitude: String

        if (cursor.moveToFirst()){
            do {
                id =cursor.getInt(cursor.getColumnIndex("_id"))
                title = cursor.getString(cursor.getColumnIndex("title"))
                image = cursor.getString(cursor.getColumnIndex("image"))
                description = cursor.getString(cursor.getColumnIndex("description"))
                date = cursor.getString(cursor.getColumnIndex("date"))
                location = cursor.getString(cursor.getColumnIndex("location"))
                latitude = cursor.getString(cursor.getColumnIndex("latitude"))
                longitude = cursor.getString(cursor.getColumnIndex("longitude"))

                val place = HappyPlaceModel(id,title,image,description,date,location,latitude,longitude)

                happyPlaceList.add(place)


            }while (cursor.moveToNext())
        }
       // cursor.close()
        return happyPlaceList
    }
}