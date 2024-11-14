package com.example.loginapp

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.layout.size
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(private val notesList: MutableList<Note>) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeTextView: TextView = itemView.findViewById(R.id.type_Expense_ds)
        val amountTextView: TextView = itemView.findViewById(R.id.amount_Expense_ds)
        val dateTextView: TextView = itemView.findViewById(R.id.date_Expense_ds)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.payment_expense, parent, false) // Assuming your sticky note layout is named sticky_note_item.xml
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notesList[position]
        holder.typeTextView.text = currentNote.type
        holder.amountTextView.text = currentNote.amount
        holder.dateTextView.text = currentNote.date
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    // Function to add a new note
    fun addNote(note: Note) { // Change parameter type to your custom Note class
        notesList.add(note)
        notifyItemInserted(notesList.size - 1)
    }

    // Function to remove a note
    fun removeNote(position: Int) {
        notesList.removeAt(position)
        notifyItemRemoved(notesList.size - 1)
    }
}