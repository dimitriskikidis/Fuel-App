package com.dimitriskikidis.owner.fuelapp.domain.repository

import com.dimitriskikidis.owner.fuelapp.data.remote.requests.*
import com.dimitriskikidis.owner.fuelapp.data.remote.responses.FuelStationCreateResponse
import com.dimitriskikidis.owner.fuelapp.data.remote.responses.OwnerSignInResponse
import com.dimitriskikidis.owner.fuelapp.data.remote.responses.OwnerSignUpResponse
import com.dimitriskikidis.owner.fuelapp.domain.models.*
import com.dimitriskikidis.owner.fuelapp.util.Resource

interface FuelRepository {

    suspend fun signIn(
        request: OwnerSignInRequest
    ): Resource<OwnerSignInResponse>

    suspend fun signUp(
        request: OwnerSignUpRequest
    ): Resource<OwnerSignUpResponse>

    suspend fun getBrands(): Resource<List<Brand>>

    suspend fun getFuelStationById(
        fuelStationId: Int
    ): Resource<FuelStation>

    suspend fun getFuelStationByOwnerId(
        ownerId: Int
    ): Resource<FuelStation>

    suspend fun createFuelStation(
        ownerId: Int,
        request: FuelStationCreateUpdateRequest
    ): Resource<FuelStationCreateResponse>

    suspend fun updateFuelStation(
        fuelStationId: Int,
        request: FuelStationCreateUpdateRequest
    ): Resource<Unit>

    suspend fun getBrandFuelsByFuelStationId(
        fuelStationId: Int
    ): Resource<List<BrandFuel>>

    suspend fun getReviewsByFuelStationId(
        fuelStationId: Int
    ): Resource<List<Review>>

    suspend fun getFuelsByFuelStationId(
        fuelStationId: Int
    ): Resource<List<Fuel>>

    suspend fun createFuel(
        fuelStationId: Int,
        request: FuelCreateRequest
    ): Resource<Unit>

    suspend fun updateFuel(
        fuelId: Int,
        request: FuelUpdateRequest
    ): Resource<Unit>

    suspend fun deleteFuel(
        fuelId: Int
    ): Resource<Unit>
}