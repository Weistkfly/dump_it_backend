package com.weistkfly.data.mail

interface Mail {
    suspend fun sendResetPasswordMail(name: String, email: String, newPassword: String): Boolean
}