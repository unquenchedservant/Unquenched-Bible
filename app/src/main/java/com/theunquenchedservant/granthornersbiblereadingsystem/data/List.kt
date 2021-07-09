package com.theunquenchedservant.granthornersbiblereadingsystem.data

data class List(
    var listName: String="",
    var listReading: String="Loading...",
    var listIndex: Int = 0,
    var listDone: Boolean=false,
    var listDoneDaily: Boolean=false
)