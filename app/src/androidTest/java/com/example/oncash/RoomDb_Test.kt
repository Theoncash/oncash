package com.example.oncash

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.oncash.RoomDb.User
import com.example.oncash.RoomDb.userDb
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(AndroidJUnit4::class)
class RoomDb_Test {
    lateinit var roomDb: userDb
    @Before
    fun setUp(){
         roomDb= Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            userDb::class.java
        ).allowMainThreadQueries().build()
        roomDb.userQuery().addUser(User(9901905025, "usertableid"))
    }

    @Test
    fun user_db_test(){
       Assert.assertEquals("usertableid" , roomDb.userQuery().getUserId())
        Assert.assertEquals(9901905025 , roomDb.userQuery().getUserNumber())
    }

    @After
    fun after(){
        roomDb.close()
    }
}