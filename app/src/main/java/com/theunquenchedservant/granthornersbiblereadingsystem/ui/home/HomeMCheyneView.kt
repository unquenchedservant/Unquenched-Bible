package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ListsDone
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ListsDoneRepository
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ReadingListRepository
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ReadingLists

class HomeMCheyneView () : ViewModel() {
    private val readingListRepository: ReadingListRepository = ReadingListRepository()
    private val listsDoneRepository = ListsDoneRepository()
    val list1: LiveData<ReadingLists> = readingListRepository.getList("mcheyneList1")
    val list2: LiveData<ReadingLists> = readingListRepository.getList("mcheyneList2")
    val list3: LiveData<ReadingLists> = readingListRepository.getList("mcheyneList3")
    val list4: LiveData<ReadingLists> = readingListRepository.getList("mcheyneList4")
    val listsDone: LiveData<ListsDone> = listsDoneRepository.getListsDone()
}