package com.example.adminapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val mappingRemoteData: MappingRemoteData,connectivityObserver: ConnectivityObserver):ViewModel(){






    val isConnected = connectivityObserver.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


     val filterFlow = MutableStateFlow(ComplaintFilter.PENDING)

    fun updateState(newState: ComplaintFilter){
        if (filterFlow.value == newState) return
        filterFlow.value = newState
        Log.e("check", "updateState: $newState")
    }



    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
        val complaintsFlow = filterFlow
            .debounce(250)// stop for while than choose one of them
            .flatMapLatest { filter ->
                mappingRemoteData.fetchAllData(filter)
            }
            .cachedIn(viewModelScope)

}


enum class ComplaintFilter {
    PENDING,
    RESOLVED,
    REJECTED
}