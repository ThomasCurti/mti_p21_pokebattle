package com.epita.pokebattle

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epita.pokebattle.model.PokemonListItem
import kotlin.reflect.KFunction3

class Adapter(
    val data: List<PokemonListItem>,
    val context: Context,
    val itemClickListener: View.OnClickListener,
    val layout: Int,
    val binder: KFunction3<@ParameterName(name = "holder") ViewHolder, @ParameterName(name = "position") Int, @ParameterName(name = "data") List<PokemonListItem>, Unit>,
    val nameId: Int,
    val firstAttrId: Int,
    val secAttrId: Int) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(itemView : View, name: Int, firstAttr: Int, secAttr: Int) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(name)
        val firstAttribute : ImageView = itemView.findViewById(firstAttr)
        val secondAttribute : ImageView = itemView.findViewById(secAttr)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView : View = LayoutInflater.from(context).inflate(layout, parent, false)
        rowView.setOnClickListener(itemClickListener)
        return ViewHolder(rowView, nameId, firstAttrId, secAttrId)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binder.call(holder, position, data)
    }
}