package com.example.thenotesapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val noteTitle: String,
    val noteDesc: String,
    val isDeleted: Boolean,
    val imageUri: String?
) : Parcelable {

    @ColumnInfo(name = "year")
    var year: Int = 0

    @ColumnInfo(name = "month")
    var month: Int = 0

    @ColumnInfo(name = "day")
    var day: Int = 0

}
