package com.example.oncash.RoomDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    val userNumber : Long ,
    @PrimaryKey
    val userTableId : String
)