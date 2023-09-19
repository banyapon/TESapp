package com.daydev.tesapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity() {
    var showText: TextView? = null
    var showDetail: TextView? = null
    var showWeight: TextView? = null
    var showLatLon: TextView? = null
    var showImg: ImageView? = null
    var showQR: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        showText = findViewById<TextView>(R.id.showText)
        showDetail = findViewById<TextView>(R.id.showDetail)
        showWeight = findViewById<TextView>(R.id.showWeight)
        showLatLon = findViewById<TextView>(R.id.showLatLon)
        showImg = findViewById<ImageView>(R.id.showImg)
        showQR = findViewById<ImageView>(R.id.showQR)

        var intent = intent
        showText!!.text = ""+intent.getStringExtra("title")
        showDetail!!.text = ""+intent.getStringExtra("content")
        showWeight!!.text = ""+intent.getStringExtra("weight")+" kg."
        showLatLon!!.text = ""+intent.getStringExtra("location")
        Picasso.get().load(intent.getStringExtra("thumbnail"))
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(showImg)
        var qrImg : String? = null
        qrImg = "https://chart.googleapis.com/chart?cht=qr&chl=https://tesprojectapp-default-rtdb.firebaseio.com/app/data/"+intent.getStringExtra("key")+".json&chs=177x177"
        Picasso.get().load(qrImg)
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(showQR)
    }
}