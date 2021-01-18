package com.example.videotest

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class ListAdapter(val activity: Activity, val items: MutableList<Video>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].let { item ->
            holder.itemView.run {
                videoName.text = item.name
                videoName.setOnClickListener {
                    val intent = Intent(activity, PlayerActivity::class.java)
                    intent.putExtra("uri", item.uri.toString())
                    activity.startActivity(intent)
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}