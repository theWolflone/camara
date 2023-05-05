package com.example.camarita

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class Adaptador : RecyclerView.Adapter<Adaptador.ViewHolder>(){
    var fotos:MutableList<MainActivity.foto> = ArrayList()
    lateinit var context: Context
    fun adaptador(foto: MutableList<MainActivity.foto>, context: Context){
        this.fotos = foto
        this.context = context
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val item = fotos.get(position)
        holder.bind(item, context)
    }
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder{
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_fotos_tomadas, parent, false))
    }

    override fun getItemCount(): Int{
        return fotos.size
    }
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val fotot = view.findViewById(R.id.foto) as ImageView
        val botonelinina = view.findViewById(R.id.btnelinina) as Button
        var tecs = view.findViewById<TextView>(R.id.textShow)
        var longitud = view.findViewById<TextView>(R.id.lon)
        var latitud = view.findViewById<TextView>(R.id.lat)
        fun bind(foto: MainActivity.foto, context: Context){
            fotot.setImageURI(
                Uri.parse(foto.imagencruda)
            )
            tecs.text=foto.inputNombre
        }
    }

}