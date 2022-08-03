package com.weistkfly.data.user

interface UserDataSource {
    suspend fun getUserByUserName(email : String) : User?
    suspend fun getUserByUserId(id : String) : User?
    suspend fun insertUser(user: User): Boolean
    suspend fun updateUser(user: User, newPassword: String, newSalt: String): Boolean
}