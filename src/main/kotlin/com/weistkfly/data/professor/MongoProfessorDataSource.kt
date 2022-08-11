package com.weistkfly.data.professor

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.regex
import org.litote.kmongo.setValue

class MongoProfessorDataSource(
    db: CoroutineDatabase
) : ProfessorDataSource {
    private val professors = db.getCollection<Professor>()

    override suspend fun getBestRatedProfessors(): List<Professor?> {
        return professors.find()
            .descendingSort(Professor::avgRating)
            .toList()
    }

    override suspend fun addProfessor(professor: Professor): Boolean {
        return professors.insertOne(professor).wasAcknowledged()
    }

    override suspend fun getAllProfessors(): List<Professor?> {
        return professors.find().toList()
    }

    override suspend fun getProfessorById(professorId: String): Professor? {
        return professors.findOneById(professorId)
    }

    override suspend fun getProfessorsBySchool(school: String): List<Professor?> {
        return professors.find(Professor::school regex school).toList()
    }

    override suspend fun updateProfessorRate(professorId: String, rate: Int, difficultyRate: Int): Boolean {
        val prof = professors.findOneById(professorId)
        try {
            professors.updateOneById(professorId, setValue(Professor::reviewCount, prof!!.reviewCount + 1))
            when (rate) {
                1 -> {
                    professors.updateOneById(professorId, setValue(Professor::bad, prof.bad + 1))
                }

                2 -> {
                    professors.updateOneById(professorId, setValue(Professor::notGood, prof.notGood + 1))
                }

                3 -> {
                    professors.updateOneById(professorId, setValue(Professor::good, prof.good + 1))
                }

                4 -> {
                    professors.updateOneById(professorId, setValue(Professor::veryGood, prof.veryGood + 1))
                }

                5 -> {
                    professors.updateOneById(professorId, setValue(Professor::excellent, prof.excellent + 1))
                }
            }
            val avgRating =
                ((prof.bad + prof.notGood * 2 + prof.good * 3 + prof.veryGood * 4 + prof.excellent * 5) / prof.reviewCount).toDouble()
            professors.updateOneById(professorId, setValue(Professor::avgRating, avgRating))
            val avgDiff = (prof.avgDifficulty + difficultyRate) / prof.reviewCount
            professors.updateOneById(professorId, setValue(Professor::avgDifficulty, avgDiff))
            return true
        } catch (e: Exception){
            println(e)
            return false
        }
    }

    override suspend fun getProfessorsByName(professorName: String): List<Professor?> {
        return professors.find(Professor::fullName regex professorName).toList()
    }

    override suspend fun updateProfessor(professorId: String, professor: Professor): Boolean {
        return professors.replaceOneById(professorId, professor).wasAcknowledged()
    }
}