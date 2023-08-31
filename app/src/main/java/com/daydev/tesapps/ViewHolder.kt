package com.daydev.tesapps

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textTitle: TextView = itemView.findViewById(R.id.title)
    var imageView: ImageView
    var cardViewContent: CardView = itemView.findViewById(R.id.cardViewContent)
    init {
        imageView = itemView.findViewById(R.id.thumbnail)
    }
}
