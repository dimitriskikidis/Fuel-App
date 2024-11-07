package com.dimitriskikidis.admin.fuelapp.presentation

import androidx.lifecycle.ViewModel
import com.dimitriskikidis.admin.fuelapp.domain.models.BrandFuel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BrandFuelsViewModel @Inject constructor() : ViewModel() {

    lateinit var currentBrandFuel: BrandFuel
}