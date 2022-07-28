package com.weistkfly.data.mail

import com.commit451.mailgun.Contact
import com.commit451.mailgun.Mailgun
import com.commit451.mailgun.SendMessageRequest

class MailImpl : Mail {
    private val key = System.getenv("MailAPI")
    private val domain = System.getenv("Domain")
    private val mailgun = Mailgun.Builder(domain, key)
        .build()

    override suspend fun sendResetPasswordMail(name: String, email: String, newPassword: String): Boolean {
        return try {
            val from = Contact("JCV@$domain", "Weistkfly")
            val to = mutableListOf<Contact>()
            to.add(Contact(email, name))
            val requestBuilder = SendMessageRequest.Builder(from)
                .to(to)
                .subject("Password reset request")
                .text("Hi dear user, this your new password $newPassword try to not forget it again :)")
            mailgun.sendMessage(requestBuilder.build())
                .blockingGet()
            true
        } catch (e: Exception) {
            false
        }
    }
}