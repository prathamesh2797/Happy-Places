package com.example.happyplaces

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_add_happy_places.view.*
import kotlinx.android.synthetic.main.item_happy_places.view.*

class HappyPlacesAdapter(private val context: Context, private val list: ArrayList<HappyPlaceModel>):
    RecyclerView.Adapter<HappyPlacesAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_happy_places, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = list[position]

        if (holder is ViewHolder){
            holder.placeImage.setImageURI(Uri.parse(model.image))
            holder.title.text = model.title
            holder.description.text = model.description
        }

        holder.itemView.setOnClickListener {
            //var model: HappyPlaceModel
            val intent = Intent(context, HappyPlaceDetails::class.java)
            intent.putExtra(EXTRA_PLACES_DETAILS, model)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {

        return list.size
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(context, AddHappyPlaces::class.java)
        intent.putExtra(EXTRA_PLACES_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    fun removeAt(position: Int){
        val dbHandler= DatabaseHandler(context)
        val isDelete = dbHandler.deleteHappyPlaces(list[position])

        if (isDelete>0){
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val placeImage = itemView.profile_image
        val title = itemView.tv_title
        val description = itemView.tv_description
    }

    companion object{

        var EXTRA_PLACES_DETAILS ="extra_place_details"
    }
}