package com.example.readcontact.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readcontact.databinding.RvItemBinding
import com.example.readcontact.models.Contacts

interface ItemClick {
    fun popupClick(contact: Contacts, position: Int, view: View)
    fun messageClick(position: Int)
    fun callClick(contact: Contacts)
}

class Adapter(val list: List<Contacts>, val itemClick: ItemClick) :
    RecyclerView.Adapter<Adapter.VH>() {

    inner class VH(var binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.contactName.text = list[position].name
        holder.binding.contactNumber.text = list[position].number

        holder.binding.popup.setOnClickListener {
            itemClick.popupClick(list[position], position, it)
        }

        holder.binding.call.setOnClickListener {
            itemClick.callClick(list[position])
        }

        holder.binding.message.setOnClickListener {
            itemClick.messageClick(position)
        }
    }

    override fun getItemCount(): Int = list.size
}
