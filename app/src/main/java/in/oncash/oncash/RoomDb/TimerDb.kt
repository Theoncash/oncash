package `in`.oncash.oncash.RoomDb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Timer::class] , version = 1 )
 abstract class TimerDb : RoomDatabase(){
    abstract fun TimerQuery():TimerQuery
}