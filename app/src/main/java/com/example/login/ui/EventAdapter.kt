package com.example.login.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login.R
import com.example.login.model.Event

class EventAdapter(
    private val eventList: List<Event>,
    private val onClick: (Event) -> Unit // Callback para manejar clics en los eventos
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvEventTitle)
        val dateTime: TextView = view.findViewById(R.id.tvEventDateTime)
        val location: TextView = view.findViewById(R.id.tvEventLocation)
        val description: TextView = view.findViewById(R.id.tvEventDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.title.text = event.title
        holder.dateTime.text = "${event.date} | ${event.time}"
        holder.location.text = event.location
        holder.description.text = event.description

        // Manejar clics en el elemento
        holder.itemView.setOnClickListener {
            onClick(event)
        }
    }

    override fun getItemCount(): Int = eventList.size
}
