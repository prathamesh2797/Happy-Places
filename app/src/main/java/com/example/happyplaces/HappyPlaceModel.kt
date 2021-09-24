package com.example.happyplaces

import java.io.Serializable

data class HappyPlaceModel (

    val id: Int,
    val title: String,
    val image: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: String,
    val longitude: String


): Serializable