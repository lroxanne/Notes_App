package com.example.thenotesapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thenotesapp.database.NoteDatabase
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DateNoteViewModel(private val application: Application) : AndroidViewModel(application) {

    private val noteRepository by lazy { NoteRepository(NoteDatabase(application)) }


    fun queryCurrentDateNote(year: Int, month: Int, day: Int, action: (List<Note>) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {
            val list = noteRepository.queryCurrentDateNote(year, month, day)
            withContext(Dispatchers.Main) {
                action.invoke(list)
            }
        }
    }
}