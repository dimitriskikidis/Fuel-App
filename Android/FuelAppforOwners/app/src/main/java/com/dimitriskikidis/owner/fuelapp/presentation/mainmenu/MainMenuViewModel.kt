package com.dimitriskikidis.owner.fuelapp.presentation.mainmenu

import androidx.lifecycle.ViewModel
import com.dimitriskikidis.owner.fuelapp.data.remote.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {

    private val _state = MutableStateFlow(MainMenuUiState())
    val state = _state.asStateFlow()

    init {
        val hasFuelStation = userManager.getFuelStationId() != null
        _state.update {
            it.copy(hasFuelStation = hasFuelStation)
        }
    }

    fun onCreateFuelStation() {
        val hasFuelStation = userManager.getFuelStationId() != null
        _state.update {
            it.copy(hasFuelStation = hasFuelStation)
        }
    }
}