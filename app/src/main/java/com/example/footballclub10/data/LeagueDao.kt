package com.example.footballclub10.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LeagueDao {
    @Query("select * from league")
    suspend fun getAllLeagues(): List<League>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeague(vararg leagues: League)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeagues(leagues: List<League>)
}