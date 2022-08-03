package com.weistkfly.data.professor

interface ProfessorDataSource {
    suspend fun addProfessor(professor: Professor): Boolean
    suspend fun getAllProfessors(): List<Professor?>
    suspend fun getProfessorById(professorId: String): Professor?
    suspend fun getProfessorsByName(professorName: String): List<Professor?>
    suspend fun updateProfessor(professorId: String, professor: Professor):Boolean
}