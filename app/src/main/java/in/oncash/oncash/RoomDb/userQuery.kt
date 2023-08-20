package `in`.oncash.oncash.RoomDb

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface userQuery {

    @Insert
    fun addUser(user : User)


    @androidx.room.Query("SELECT userTableId FROM User")
    fun getUserId():String

    @androidx.room.Query("SELECT userNumber FROM User")
    fun getUserNumber():Long
}