package com.dimitriskikidis.admin.fuelapp.presentation.addeditbrand

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.admin.fuelapp.data.remote.UserManager
import com.dimitriskikidis.admin.fuelapp.data.remote.requests.BrandCreateUpdateRequest
import com.dimitriskikidis.admin.fuelapp.domain.models.Brand
import com.dimitriskikidis.admin.fuelapp.domain.repository.FuelRepository
import com.dimitriskikidis.admin.fuelapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.inject.Inject

@HiltViewModel
class AddEditBrandViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        data class ShowMessageAndNavigateBack(
            val title: String,
            val message: String
        ) : UiEvent()

        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(AddEditBrandUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var currentBrand: Brand? = null
        private set

    var brandName: String? = null
        private set
    var brandIcon: Bitmap? = null
        private set

    fun onEvent(event: AddEditBrandEvent) {
        when (event) {
            is AddEditBrandEvent.OnInitBrand -> {
                currentBrand = event.currentBrand
                brandName = event.currentBrand.name
                brandIcon = event.currentBrand.iconBitmap
            }
            is AddEditBrandEvent.OnNameChange -> {
                brandName = event.name
            }
            is AddEditBrandEvent.OnIconChange -> {
                brandIcon = event.icon
            }
            is AddEditBrandEvent.OnAddSave -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            nameError = null,
                            iconError = null,
                            isLoading = true
                        )
                    }

                    val nameExists = event.brandNames.any {
                        it == brandName && it != currentBrand?.name
                    }
                    var nameError: String? = null
                    if (brandName.isNullOrBlank()) {
                        nameError = "This field is required." // "The name cannot be empty."
                    } else if (nameExists) {
                        nameError = "The brand '$brandName' already exists."
                    }

                    var iconError: String? = null
                    if (brandIcon == null) {
                        iconError = "Select an icon."
                    }

                    val errors = listOf(nameError, iconError)
                    val hasErrors = errors.any { it != null }

                    if (hasErrors) {
                        _state.update {
                            it.copy(
                                nameError = nameError,
                                iconError = iconError,
                                isLoading = false
                            )
                        }
                        return@launch
                    }

                    val iconBytes = bitmapToBase64String(brandIcon!!)
                    val request = BrandCreateUpdateRequest(
                        name = brandName!!,
                        iconBytes = iconBytes
                    )

                    if (currentBrand == null) {
                        when (val response = repository.createBrand(request)) {
                            is Resource.Success -> {
                                _uiEvent.send(
                                    UiEvent.ShowMessageAndNavigateBack(
                                        title = "Success",
                                        message = "The brand '$brandName' was added successfully."
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
                                    UiEvent.ShowMessageAndNavigateBack(
                                        title = "Error",
                                        message = message
                                    )
                                )
                            }
                        }
                    } else {
                        val brandId = currentBrand!!.id

                        when (val response = repository.updateBrand(brandId, request)) {
                            is Resource.Success -> {
                                _uiEvent.send(
                                    UiEvent.ShowMessageAndNavigateBack(
                                        title = "Success",
                                        message = "The brand '${currentBrand!!.name}' was updated successfully."
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
                                    UiEvent.ShowMessageAndNavigateBack(
                                        title = "Error",
                                        message = message
                                    )
                                )
                            }
                        }
                    }

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private fun bitmapToBase64String(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.getMimeEncoder().encodeToString(byteArray)
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