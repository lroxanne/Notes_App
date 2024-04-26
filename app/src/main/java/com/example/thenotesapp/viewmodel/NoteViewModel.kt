package com.example.thenotesapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(app: Application, private val noteRepository: NoteRepository): AndroidViewModel(app) {

    fun addNote(note: Note) =
        viewModelScope.launch {
            noteRepository.insertNote(note)
        }

    fun deleteNote(note: Note) =
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }

    fun thoroughDeleteNote(note: Note) =
        viewModelScope.launch {
            noteRepository.thoroughDeleteNote(note)
        }

    fun recoverNote(note: Note) =
        viewModelScope.launch {
            noteRepository.recoverNote(note)
        }

    fun updateNote(note: Note) =
        viewModelScope.launch {
            noteRepository.updateNote(note)
        }

    fun getAllNotes() = noteRepository.getAllNotes()

    fun getDeleteNotes() = noteRepository.getDeleteNotes()

    fun searchNote(query: String?) =
        noteRepository.searchNote(query)

    fun updateNoteImageUri(noteId: Int, imageUri: String) =
        viewModelScope.launch {
            noteRepository.updateNoteImageUri(noteId, imageUri)
        }
}