package com.dimitriskikidis.admin.fuelapp.presentation.brandlist

import com.dimitriskikidis.admin.fuelapp.domain.models.Brand

data class BrandListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val brands: List<Brand> = emptyList()
)
