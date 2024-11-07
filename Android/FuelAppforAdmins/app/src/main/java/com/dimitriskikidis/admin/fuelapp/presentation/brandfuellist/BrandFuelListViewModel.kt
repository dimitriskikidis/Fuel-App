package com.dimitriskikidis.admin.fuelapp.presentation.brandfuellist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.admin.fuelapp.data.remote.UserManager
import com.dimitriskikidis.admin.fuelapp.domain.models.Brand
import com.dimitriskikidis.admin.fuelapp.domain.models.BrandFuel
import com.dimitriskikidis.admin.fuelapp.domain.models.FuelType
import com.dimitriskikidis.admin.fuelapp.domain.repository.FuelRepository
import com.dimitriskikidis.admin.fuelapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandFuelListViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        data class ShowMessage(val title: String, val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(BrandFuelListUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var brands: List<Brand> = emptyList()
    private var fuelTypes: List<FuelType> = emptyList()
    private var brandFuels: List<BrandFuel> = emptyList()

    private var selectedBrandId: Int? = null

    var selectedBrandName: String? = null
        private set
    var brandNames: Array<String> = emptyArray()
        private set

    init {
        initData()
    }

    fun onBrandChange(brandName: String) {
        if (brandName.isBlank()) return
        val selectedBrand = brands.first { it.name == brandName }
        selectedBrandId = selectedBrand.id
        selectedBrandName = selectedBrand.name
        val brandFuels = brandFuels
            .filter { it.brandId == selectedBrandId }
            .sortedBy { it.name }
        _state.update {
            it.copy(brandFuels = brandFuels)
        }
    }

    fun onBrandFuelEditComplete() {
        initData()
    }

    private fun initData() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            when (val response = repository.getBrands()) {
                is Resource.Success -> {
                    brands = response.data!!
                    brandNames = brands.map { it.name }.toTypedArray()
                    if (brands.isEmpty()) {
                        selectedBrandId = null
                        selectedBrandName = null
                        _state.update {
                            it.copy(
                                isLoading = false,
                                brandFuels = emptyList()
                            )
                        }
                        return@launch
                    }

                    val selectedBrandExists = brands.any { it.id == selectedBrandId }
                    if (selectedBrandId == null || !selectedBrandExists) {
                        val firstBrand = brands.first()
                        selectedBrandId = firstBrand.id
                        selectedBrandName = firstBrand.name
                    }
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
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = message,
                            brandFuels = emptyList()
                        )
                    }
                    return@launch
                }
            }

            when (val response = repository.getFuelTypes()) {
                is Resource.Success -> {
                    fuelTypes = response.data!!
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
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = message,
                            brandFuels = emptyList()
                        )
                    }
                    return@launch
                }
            }

            when (val response = repository.getBrandFuels()) {
                is Resource.Success -> {
                    brandFuels = response.data!!
                    for (brandFuel in brandFuels) {
                        val fuelType = fuelTypes.first { it.id == brandFuel.fuelTypeId }
                        brandFuel.fuelTypeName = fuelType.name
                    }

                    val brandFuelsFiltered = brandFuels
                        .filter { it.brandId == selectedBrandId }
                        .sortedBy { it.name }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            brandFuels = brandFuelsFiltered
                        )
                    }
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
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = message,
                            brandFuels = emptyList()
                        )
                    }
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