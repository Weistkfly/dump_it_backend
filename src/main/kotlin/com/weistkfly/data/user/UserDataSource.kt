package com.weistkfly.data.user

interface UserDataSource {
    suspend fun getUserByUserName(email : String) : User?
    suspend fun getUserByUserId(id : String) : User?
    suspend fun insertUser(user: User): Boolean
    suspend fun updateUser(user: User, newPassword: String, newSalt: String): Boolean
    suspend fun incrementRatesCount(userId: String): Boolean
    suspend fun changeUserIcon(userId: String, newIconId: Int): Boolean
    suspend fun deleteAccount(userId: String): Boolean
}