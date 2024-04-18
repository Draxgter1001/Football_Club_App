package com.example.footballclub10.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class League(
    @PrimaryKey(autoGenerate = true) val idLeague: Int = 0,
    val strLeague: String,
    val strSport: String,
    val strLeagueAlternate: String
)