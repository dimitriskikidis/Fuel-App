package com.dimitriskikidis.admin.fuelapp.presentation.brandlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.admin.fuelapp.data.remote.UserManager
import com.dimitriskikidis.admin.fuelapp.domain.repository.FuelRepository
import com.dimitriskikidis.admin.fuelapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandListViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        data class ShowMessage(val title: String, val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(BrandListUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            getBrands()
        }
    }

    fun onEvent(event: BrandListEvent) {
        when (event) {
            is BrandListEvent.OnBrandAddEditComplete -> {
                viewModelScope.launch {
                    getBrands()
                }
            }
            is BrandListEvent.OnBrandDeleteConfirm -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(isLoading = true)
                    }

                    val brand = event.brand

                    when (val response = repository.deleteBrand(brand.id)) {
                        is Resource.Success -> {
                            _uiEvent.send(
                                UiEvent.ShowMessage(
                                    title = "Success",
                                    message = "The brand '${brand.name}' was deleted successfully."
                                )
                            )
                        }
                        is Resource.Error -> {
                            if (response.isUnauthorized) {
                                handleUnauthorizedError()
                                return@launch
                            }
                            val message = response.message!!
                            _uiEvent.send(
                                UiEvent.ShowMessage(
                                    title = "Error",
                                    message = message
                                )
                            )
                        }
                    }

                    getBrands()
                }
            }
        }
    }

    private suspend fun getBrands() {
        _state.update {
            it.copy(isLoading = true)
        }

        when (val response = repository.getBrands()) {
            is Resource.Success -> {
                val brands = response.data!!
                _state.update {
                    it.copy(
                        isLoading = false,
                        brands = brands
                    )
                }
            }
            is Resource.Error -> {
                if (response.isUnauthorized) {
                    handleUnauthorizedError()
                    return
                }
                val message = response.message!!
                _uiEvent.send(
                    UiEvent.ShowMessage(
                        title = "Error",
                        message = message
                    )
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = message,
                        brands = emptyList()
                    )
                }
            }
        }
    }

    private suspend fun handleUnauthorizedError() {
        userManager.clear()
        _uiEvent.send(
            UiEvent.ShowMessageAndSignOut(
                "Your session has expired. You must sign in to continue" +
                        " using the app."
            )
        )
    }
}