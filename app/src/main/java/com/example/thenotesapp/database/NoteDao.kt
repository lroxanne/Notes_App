package com.example.thenotesapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.thenotesapp.model.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("UPDATE NOTES SET isDeleted=1 WHERE id =:noteId")
    suspend fun deleteNote(noteId: Int)

    @Delete
    suspend fun thoroughDeleteNote(note: Note)

    @Query("UPDATE NOTES SET isDeleted=0 WHERE id =:noteId")
    suspend fun recoverNote(noteId: Int)

    @Query("SELECT * FROM NOTES WHERE isDeleted is 0 ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM NOTES WHERE isDeleted is 1 ORDER BY id DESC")
    fun getDeleteNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM NOTES WHERE lower(noteTitle) LIKE lower(:query) OR UPPER(noteDesc) LIKE UPPER(:query)")
    fun searchNote(query: String?): LiveData<List<Note>>

    @Query("UPDATE NOTES SET imageUri = :imageUri WHERE id = :noteId")
    suspend fun updateNoteImageUri(noteId: Int, imageUri: String)

}
