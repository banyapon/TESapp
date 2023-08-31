package com.daydev.tesapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity() {
    var showText: TextView? = null
    var showDetail: TextView? = null
    var showImg: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        showText = findViewById<TextView>(R.id.showText)
        showDetail = findViewById<TextView>(R.id.showDetail)
        showImg = findViewById<ImageView>(R.id.showImg)

        var intent = intent
        showText!!.text = ""+intent.getStringExtra("title")
        showDetail!!.text = ""+intent.getStringExtra("content")
        Picasso.get().load(intent.getStringExtra("thumbnail"))
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(showImg)
    }
}