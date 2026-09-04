package com.example.yra.ui.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yra.data.local.StatsDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class LobbyViewModel(private val statsDao: StatsDao) : ViewModel() {

    private val startOfDayMs: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

    private val startOfWeekMs: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

    val totalTimeListenedToday: StateFlow<Long> = 
        statsDao.getTotalListeningTimeSince(startOfDayMs)
            .map { it ?: 0L }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0L)
            
    val totalTimeListenedWeek: StateFlow<Long> = 
        statsDao.getTotalListeningTimeSince(startOfWeekMs)
            .map { it ?: 0L }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0L)
}

class LobbyViewModelFactory(private val statsDao: StatsDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LobbyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LobbyViewModel(statsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
