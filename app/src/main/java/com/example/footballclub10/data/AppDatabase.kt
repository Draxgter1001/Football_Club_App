package com.example.footballclub10.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Club::class, League::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun getLeagueDao(): LeagueDAO
    abstract fun getClubDao(): ClubDAO

}