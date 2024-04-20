package com.example.footballclub10.data

class ClubRepository(private val clubDao: ClubDao) {
    suspend fun searchClubs(query: String): List<Club> {
        return clubDao.searchClubs(query)
    }
}