package com.example.thenotesapp.repository

import com.example.thenotesapp.database.NoteDatabase
import com.example.thenotesapp.model.Note

class NoteRepository(private val db: NoteDatabase) {

    suspend fun insertNote(note: Note) = db.getNoteDao().insertNote(note)
    suspend fun deleteNote(note: Note) = db.getNoteDao().deleteNote(note.id)
    suspend fun updateNote(note: Note) = db.getNoteDao().updateNote(note)

    suspend fun thoroughDeleteNote(note: Note) = db.getNoteDao().thoroughDeleteNote(note)
    suspend fun recoverNote(note: Note) = db.getNoteDao().recoverNote(note.id)

    fun getAllNotes() = db.getNoteDao().getAllNotes()
    fun getDeleteNotes() = db.getNoteDao().getDeleteNotes()
    fun searchNote(query: String?) = db.getNoteDao().searchNote(query)
}