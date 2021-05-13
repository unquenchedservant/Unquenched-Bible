package com.theunquenchedservant.granthornersbiblereadingsystem.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        debugLog(message="Refreshed token: $token")
        sendRegistrationToServer()
    }
    private fun sendRegistrationToServer(){
        // TODO: Send token to our server
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        debugLog(message="Message: $message")
    }

}