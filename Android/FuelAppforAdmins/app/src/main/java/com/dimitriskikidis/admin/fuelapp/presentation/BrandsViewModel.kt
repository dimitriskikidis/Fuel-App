package com.dimitriskikidis.admin.fuelapp.presentation

import androidx.lifecycle.ViewModel
import com.dimitriskikidis.admin.fuelapp.domain.models.Brand
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BrandsViewModel @Inject constructor() : ViewModel() {

    var currentBrand: Brand? = null
    var brandNames: List<String> = emptyList()
        private set

    fun mapBrandNames(brands: List<Brand>) {
        brandNames = brands.map { it.name }
    }
}