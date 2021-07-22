package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ListsDone
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ListsDoneRepository
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ReadingListRepository
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ReadingLists

class HomeView () : ViewModel() {
    private val readingListRepository: ReadingListRepository = ReadingListRepository()
    private val listsDoneRepository = ListsDoneRepository()
    val pgh1: LiveData<ReadingLists> = readingListRepository.getList("pgh1")
    val pgh2: LiveData<ReadingLists> = readingListRepository.getList("pgh2")
    val pgh3: LiveData<ReadingLists> = readingListRepository.getList("pgh3")
    val pgh4: LiveData<ReadingLists> = readingListRepository.getList("pgh4")
    val pgh5: LiveData<ReadingLists> = readingListRepository.getList("pgh5")
    val pgh6: LiveData<ReadingLists> = readingListRepository.getList("pgh6")
    val pgh7: LiveData<ReadingLists> = readingListRepository.getList("pgh7")
    val pgh8: LiveData<ReadingLists> = readingListRepository.getList("pgh8")
    val pgh9: LiveData<ReadingLists> = readingListRepository.getList("pgh9")
    val pgh10: LiveData<ReadingLists> = readingListRepository.getList("pgh10")
    val mcheyne1: LiveData<ReadingLists> = readingListRepository.getList("mcheyne1")
    val mcheyne2: LiveData<ReadingLists> = readingListRepository.getList("mcheyne2")
    val mcheyne3: LiveData<ReadingLists> = readingListRepository.getList("mcheyne3")
    val mcheyne4: LiveData<ReadingLists> = readingListRepository.getList("mcheyne4")
    val listsDone: LiveData<ListsDone> = listsDoneRepository.getListsDone()
}