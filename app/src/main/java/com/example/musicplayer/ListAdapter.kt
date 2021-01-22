package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Field

class ListAdapter(private val mContext: Context,
                  private val songsIDs: MutableList<Int>): RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    class ListViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val nameTV: TextView = view.findViewById(R.id.songNameTV)
        val playIB: ImageButton = view.findViewById(R.id.playIB)
    }
    private val fields: Array<Field> = R.raw::class.java.fields
    private val songsTitles: MutableList<String> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return  ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songsIDs.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        fields.forEach { field ->
            songsTitles.add(field.name)
        }
        holder.nameTV.text = songsTitles[position]
        holder.playIB.setOnClickListener{
            val intent = Intent(mContext, Player::class.java).apply {
                putExtra("PlayTitle", songsTitles[position])
                putExtra("PlayID", songsIDs[position])
            }
            mContext.startActivity(intent)
        }
    }
}