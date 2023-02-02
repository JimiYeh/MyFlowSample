package com.example.myflowsample

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(private val stateHandle: SavedStateHandle) : ViewModel() {

  companion object {
    const val KEY_NUMBER = "key_number"
  }

  private val numberFlow = MutableStateFlow<Int?>(null)
  val uiStateFlow: StateFlow<UiState> = numberFlow.mapLatest { number ->
    if (number == null)
      UiState.Loading
    else
      UiState.Success(number)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Lazily,
    initialValue = UiState.Loading
  )

  private var job: Job? = null

  init {
    Log.e("Jimi", "MainViewModel init")

    stateHandle.get<Int>(KEY_NUMBER)?.let {
      viewModelScope.launch {
        numberFlow.emit(it)
      }
    } ?: kotlin.run {
      Log.e("Jimi", "null number")
      getNumber()
    }

    viewModelScope.launch {
      numberFlow.collect { number ->
        if (number == null)
          UiState.Loading
        else
          UiState.Success(number)
      }
    }
  }

  // 用來模擬網路動作
  fun getNumber() {
    job?.cancel()
    job = viewModelScope.launch(Dispatchers.IO) {
      numberFlow.emit(null)
      stateHandle[KEY_NUMBER] = null
      delay(5000)
      Random().nextInt().let {
        numberFlow.emit(it)
        stateHandle[KEY_NUMBER] = it
      }
    }
  }

  sealed class UiState {
    object Loading : UiState()
    data class Success(val number: Int) : UiState()
  }
}
