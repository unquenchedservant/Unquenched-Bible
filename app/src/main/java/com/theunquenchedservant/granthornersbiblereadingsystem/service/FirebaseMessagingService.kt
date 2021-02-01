package com.theunquenchedservant.granthornersbiblereadingsystem.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        log(logString="Refreshed token: $token")
        sendRegistrationToServer(token)
    }
    private fun sendRegistrationToServer(token:String){
        // TODO: Send token to our server
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        log(logString="Message: $message")
    }

}