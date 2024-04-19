package com.example.footballclub10

import android.app.Application
import androidx.room.Room
import com.example.footballclub10.data.AppDatabase

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDatabase::class.java, "football_database").build()
    }
}

lateinit var db: AppDatabase