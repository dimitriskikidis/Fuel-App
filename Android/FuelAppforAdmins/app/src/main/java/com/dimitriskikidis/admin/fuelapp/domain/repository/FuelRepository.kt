package com.dimitriskikidis.admin.fuelapp.domain.repository

import com.dimitriskikidis.admin.fuelapp.data.remote.requests.*
import com.dimitriskikidis.admin.fuelapp.data.remote.responses.AdminSignInSignUpResponse
import com.dimitriskikidis.admin.fuelapp.domain.models.Brand
import com.dimitriskikidis.admin.fuelapp.domain.models.BrandFuel
import com.dimitriskikidis.admin.fuelapp.domain.models.FuelType
import com.dimitriskikidis.admin.fuelapp.util.Resource

interface FuelRepository {

    suspend fun signIn(
        request: AdminSignInRequest
    ): Resource<AdminSignInSignUpResponse>

    suspend fun signUp(
        request: AdminSignUpRequest
    ): Resource<AdminSignInSignUpResponse>

    suspend fun getBrands(): Resource<List<Brand>>

    suspend fun createBrand(
        request: BrandCreateUpdateRequest
    ): Resource<Unit>

    suspend fun updateBrand(
        brandId: Int,
        request: BrandCreateUpdateRequest
    ): Resource<Unit>

    suspend fun deleteBrand(
        brandId: Int
    ): Resource<Unit>

    suspend fun getFuelTypes(): Resource<List<FuelType>>

    suspend fun createFuelType(
        request: FuelTypeCreateUpdateRequest
    ): Resource<Unit>

    suspend fun updateFuelType(
        fuelTypeId: Int,
        request: FuelTypeCreateUpdateRequest
    ): Resource<Unit>

    suspend fun deleteFuelType(
        fuelTypeId: Int
    ): Resource<Unit>

    suspend fun getBrandFuels(): Resource<List<BrandFuel>>

    suspend fun updateBrandFuel(
        brandFuelId: Int,
        request: BrandFuelUpdateRequest
    ): Resource<Unit>
}