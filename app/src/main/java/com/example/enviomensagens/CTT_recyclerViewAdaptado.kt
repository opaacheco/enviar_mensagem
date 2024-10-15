package com.example.enviomensagens

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.enviomensagens.classe.contato

class CTT_recyclerViewAdapter(
    private val context: Context,
    var contatos: List<contato>
) : RecyclerView.Adapter<CTT_recyclerViewAdapter.MyViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.contacto, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull holder: MyViewHolder, position: Int) {
        holder.tvName.text = contatos[position].name
        holder.tvNumber.text = contatos[position].phone
        holder.itemView.setOnClickListener {
            // Chama a função no contexto do MainActivity para abrir o WhatsApp
            (context as MainActivity).sendMessageOnWhatsApp(contatos[position].phone, contatos[position].name)
        }
    }

    override fun getItemCount(): Int {
        return contatos.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.nomeContacto)
        val tvNumber: TextView = itemView.findViewById(R.id.numeroText)

    }
}
