package com.example.footballclub10

import android.app.Application
import androidx.room.Room
import com.example.footballclub10.data.AppDatabase
import com.example.footballclub10.data.ClubDao

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDatabase::class.java, "football_database").build()
        club_dao = db.getClubDao()
    }
}

lateinit var db: AppDatabase
lateinit var club_dao: ClubDao