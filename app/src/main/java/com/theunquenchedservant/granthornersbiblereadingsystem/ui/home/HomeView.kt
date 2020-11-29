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
    val list1: LiveData<ReadingLists> = readingListRepository.getList("list1")
    val list2: LiveData<ReadingLists> = readingListRepository.getList("list2")
    val list3: LiveData<ReadingLists> = readingListRepository.getList("list3")
    val list4: LiveData<ReadingLists> = readingListRepository.getList("list4")
    val list5: LiveData<ReadingLists> = readingListRepository.getList("list5")
    val list6: LiveData<ReadingLists> = readingListRepository.getList("list6")
    val list7: LiveData<ReadingLists> = readingListRepository.getList("list7")
    val list8: LiveData<ReadingLists> = readingListRepository.getList("list8")
    val list9: LiveData<ReadingLists> = readingListRepository.getList("list9")
    val list10: LiveData<ReadingLists> = readingListRepository.getList("list10")
    val listsDone: LiveData<ListsDone> = listsDoneRepository.getListsDone()
}