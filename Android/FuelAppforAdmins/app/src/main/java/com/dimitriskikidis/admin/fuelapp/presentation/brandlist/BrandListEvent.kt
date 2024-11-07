package com.dimitriskikidis.admin.fuelapp.presentation.brandlist

import com.dimitriskikidis.admin.fuelapp.domain.models.Brand

sealed class BrandListEvent {
    object OnBrandAddEditComplete : BrandListEvent()
    data class OnBrandDeleteConfirm(val brand: Brand) : BrandListEvent()
}