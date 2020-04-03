package com.epita.pokebattle

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epita.pokebattle.methods.getImageFromType
import com.epita.pokebattle.model.PokemonListItem

class PokedexAdapter(val data : List<PokemonListItem>,
                     val context: Context,
                     val itemClickListener: View.OnClickListener) : RecyclerView.Adapter<PokedexAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.pokedex_list_item_name_txt)
        val firstAttribute : ImageView = itemView.findViewById(R.id.pokedex_list_item_first_attr_img)
        val secondAttribute : ImageView = itemView.findViewById(R.id.pokedex_list_item_sec_attr_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView : View = LayoutInflater.from(context).inflate(R.layout.pokedex_list_item, parent, false)
        rowView.setOnClickListener(itemClickListener)
        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = data[position]

        holder.name.text =  data[position].name.capitalize()

        if(data[position].types.isNotEmpty())
            holder.firstAttribute.setImageResource(getImageFromType(data[position].types[0].name))

        //TODO delete this one
        if (data[position].name == "accelgor")
            Log.d("ACCELEGOR", "" + data[position].types.size)

        if (data[position].types.size > 1)
            holder.secondAttribute.setImageResource(getImageFromType(data[position].types[1].name))
    }
}