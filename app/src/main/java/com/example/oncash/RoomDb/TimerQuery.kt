package com.example.oncash.RoomDb

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface TimerQuery {

    @Insert
    fun addTimer(endTime : Timer)


    @androidx.room.Query("SELECT endTime FROM Timer")
    fun getEndTime():Long


}