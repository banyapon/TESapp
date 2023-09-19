package com.daydev.tesapps

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class DataAdapter(val dataModelList: List<DataModel>) : RecyclerView.Adapter<ViewHolder>() {

    private val TAG = "Comic"
    val mylist = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_content,parent,false))
    }

    override fun getItemCount(): Int {
        return dataModelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = dataModelList[position]
        holder.textTitle.text = dataModel.title
        var databaseReference = FirebaseDatabase.getInstance().reference
        databaseReference.child("app/data").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d(TAG, "Count= " + dataSnapshot.childrenCount)
                for (childDataSnapshot in dataSnapshot.children) {
                    Log.d(TAG, "snapshot= " + childDataSnapshot.key!!)
                    mylist.add(childDataSnapshot.key!!)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) { }
        })

        holder.cardViewContent!!.setOnClickListener(View.OnClickListener { v ->
            val readActivity = Intent(v.context, DetailActivity::class.java)
            readActivity.putExtra("keys", mylist[position])
            readActivity.putExtra("title",dataModel.title)
            readActivity.putExtra("content",dataModel.title)
            readActivity.putExtra("thumbnail",dataModel.thumbnail)
            readActivity.putExtra("location",dataModel.location)
            readActivity.putExtra("weight",dataModel.weight)
            readActivity.putExtra("datetime",dataModel.datetime)
            readActivity.putExtra("gallery",dataModel.gallery)
            readActivity.putExtra("uid",dataModel.uid)
            v.context.startActivity(readActivity)
        })

        Picasso.get().load(dataModel.thumbnail)
            .error(R.mipmap.ic_launcher)
            .placeholder(R.mipmap.ic_launcher)
            .into(holder.imageView)
    }
}
