package com.dimitriskikidis.admin.fuelapp.presentation.addeditbrand

import android.graphics.Bitmap
import com.dimitriskikidis.admin.fuelapp.domain.models.Brand

sealed class AddEditBrandEvent {
    data class OnInitBrand(val currentBrand: Brand) : AddEditBrandEvent()
    data class OnNameChange(val name: String) : AddEditBrandEvent()
    data class OnIconChange(val icon: Bitmap) : AddEditBrandEvent()
    data class OnAddSave(val brandNames: List<String>) : AddEditBrandEvent()
}