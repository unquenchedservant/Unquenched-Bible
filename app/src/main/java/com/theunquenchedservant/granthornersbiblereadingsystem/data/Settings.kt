package com.theunquenchedservant.granthornersbiblereadingsystem.data

data class Settings(
    var notifications: Boolean = true,
    var psalms: Boolean = false,
    var holdPlan: Boolean = false,
    var vacationMode: Boolean = false,
    var weekendMode: Boolean = false,
    var darkMode: Boolean = true,
    var hasCompletedOnboarding: Boolean = false,
    var dailyNotif: Int = 600, //TODO: find the actual integer for this
    var remindNotif: Int = 1200, //TODO: find the actual integer for this
    var versionNumber: Int = 92, //NOTE: this is the same as the version number in the gradle file
    var planType: String = "horner",
    var bibleVersion: String = "niv",
    var planSystem: String = "pgh"
)
