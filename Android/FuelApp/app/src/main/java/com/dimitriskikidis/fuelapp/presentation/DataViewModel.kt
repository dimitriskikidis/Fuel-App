package com.dimitriskikidis.fuelapp.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor() : ViewModel() {

    var state = DataState()
}