package com.weistkfly.data.user

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class MongoUserDataSource(
    db: CoroutineDatabase
): UserDataSource {

    private val users = db.getCollection<User>()

    override suspend fun getUserByUserName(email: String): User? {
        return users.findOne(User::email eq email)
    }

    override suspend fun getUserByUserId(id: String): User? {
        return users.findOne(User::id eq id)
    }

    override suspend fun insertUser(user: User): Boolean {
        //wasAcknowledged() says whether it was or not successful
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun updateUser(user: User, newPassword: String, newSalt: String): Boolean {
        users.updateOne(User::email eq user.email, setValue(User::salt, newSalt))
        return users.updateOne(User::email eq user.email, setValue(User::password, newPassword))
            .wasAcknowledged()
    }
}