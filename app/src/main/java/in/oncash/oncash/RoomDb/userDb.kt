package `in`.oncash.oncash.RoomDb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class] , version = 2 )
 abstract class userDb : RoomDatabase(){
    abstract fun userQuery():userQuery
}