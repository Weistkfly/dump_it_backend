package com.weistkfly.data.professor

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.regex

class MongoProfessorDataSource(
    db: CoroutineDatabase
): ProfessorDataSource {

    private val professors = db.getCollection<Professor>()

    override suspend fun addProfessor(professor: Professor): Boolean {
        return professors.insertOne(professor).wasAcknowledged()
    }

    override suspend fun getAllProfessors(): List<Professor?> {
        return professors.find().toList()
    }

    override suspend fun getProfessorById(professorId: String): Professor? {
        return professors.findOneById(professorId)
    }

    override suspend fun getProfessorsByName(professorName: String): List<Professor?> {
        return professors.find(Professor::fullName regex  professorName).toList()
    }

    override suspend fun updateProfessor(professorId: String, professor: Professor): Boolean {
        return professors.replaceOneById(professorId, professor).wasAcknowledged()
    }
}