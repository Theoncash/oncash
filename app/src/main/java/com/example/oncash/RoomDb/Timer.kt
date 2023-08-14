package com.example.oncash.RoomDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Timer")
data class Timer(
    @PrimaryKey
    val endTime : Long
)