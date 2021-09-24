package com.example.happyplaces.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.R

abstract class SwipeToEditCallBack(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {


    private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_edit_24)
    private val intrisicWidth = editIcon!!.intrinsicWidth
    private val intrinsicHeight = editIcon!!.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#24AE05")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }




    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCancelled =dX ==0f && !isCurrentlyActive

        if (isCancelled){

            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(),itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            return
        }

        //Draw the green edit background
         background.color = backgroundColor
        background.setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
        background.draw(c)

        //calculate position of delete icon
        val editIconTop = itemView.top + (itemHeight - intrinsicHeight)/2
        val editIconMargin = (itemHeight - intrinsicHeight)
        val editIconLeft = itemView.left + editIconMargin - intrisicWidth
        val editIconRight = itemView.left + editIconMargin
        val editIconBottom = editIconTop + intrinsicHeight


        // Draw the Edit Icon
        editIcon!!.setBounds(editIconLeft,editIconTop,editIconRight,editIconBottom)
        editIcon!!.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)




    }

  /*  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }*/

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float){

        c?.drawRect(left,top,right,bottom,clearPaint )
    }
}