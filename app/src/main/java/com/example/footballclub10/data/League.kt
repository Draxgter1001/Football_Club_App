package com.example.footballclub10.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class League (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var strLeague: String?,
    var strSport: String?,
    var strLeagueAlternate: String?
)