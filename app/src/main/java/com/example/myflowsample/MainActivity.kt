package com.example.myflowsample

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.ui.AppBarConfiguration
import com.example.myflowsample.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Log.e("Jimi", "onCreate")

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.fab.setOnClickListener { _ ->
      viewModel.getNumber()
    }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiStateFlow.collect { uiState ->
          when(uiState) {
            MainViewModel.UiState.Loading -> binding.number.text = "Loading"
            is MainViewModel.UiState.Success -> binding.number.text = uiState.number.toString()
          }
        }
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
  }
}