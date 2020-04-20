package com.epita.pokebattle

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.epita.pokebattle.methods.getImageFromType
import com.epita.pokebattle.model.TypeUrlName


class TypeHelperAdapter(
    val data: List<Array<TypeUrlName>>,
    val context: Context
) : RecyclerView.Adapter<TypeHelperAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val type1: ImageView = itemView.findViewById<ImageView>(R.id.typehelper_list_item_type1)
        val type2 : ImageView = itemView.findViewById<ImageView>(R.id.typehelper_list_item_type2)
        val type3 : ImageView = itemView.findViewById<ImageView>(R.id.typehelper_list_item_type3)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView : View = LayoutInflater.from(context).inflate(R.layout.typehelper_list_item , parent, false)
        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val size = data[position].size
        if (size < 3)
        {
            if (size >= 1) //should never happen
                holder.type1.setImageResource(getImageFromType(data[position][0].name))
            if (size >= 2)
                holder.type2.setImageResource(getImageFromType(data[position][1].name))
            if (size >= 3) //should never happen
                holder.type3.setImageResource(getImageFromType(data[position][1].name))
        }
        else { //currentPos < data.size && posMax > data.size
            holder.type1.setImageResource(getImageFromType(data[position][0].name))
            holder.type2.setImageResource(getImageFromType(data[position][1].name))
            holder.type3.setImageResource(getImageFromType(data[position][2].name))
        }

    }
}