package com.dimitriskikidis.fuelapp.domain.repository

import com.dimitriskikidis.fuelapp.data.remote.requests.ConsumerSignInRequest
import com.dimitriskikidis.fuelapp.data.remote.requests.ConsumerSignUpRequest
import com.dimitriskikidis.fuelapp.data.remote.requests.FuelSearchRequest
import com.dimitriskikidis.fuelapp.data.remote.requests.ReviewCreateUpdateRequest
import com.dimitriskikidis.fuelapp.data.remote.responses.ConsumerSignInResponse
import com.dimitriskikidis.fuelapp.data.remote.responses.ConsumerSignUpResponse
import com.dimitriskikidis.fuelapp.domain.models.*
import com.dimitriskikidis.fuelapp.presentation.DataState
import com.dimitriskikidis.fuelapp.util.Resource

interface FuelRepository {

    suspend fun updateDataState(dataState: DataState)

    suspend fun signIn(
        request: ConsumerSignInRequest
    ): Resource<ConsumerSignInResponse>

    suspend fun signUp(
        request: ConsumerSignUpRequest
    ): Resource<ConsumerSignUpResponse>

    suspend fun getBrands(): Resource<List<Brand>>

    suspend fun getFuelTypes(): Resource<List<FuelType>>

    suspend fun searchFuels(
        request: FuelSearchRequest
    ): Resource<List<FuelSearchResult>>

    suspend fun getFuelStationById(
        fuelStationId: Int
    ): Resource<FuelStation>

    suspend fun getFuelsByFuelStationId(
        fuelStationId: Int
    ): Resource<List<Fuel>>

    suspend fun getReviewsByFuelStationId(
        fuelStationId: Int
    ): Resource<List<Review>>

    suspend fun getUserReviewsByConsumerId(
        consumerId: Int
    ): Resource<List<UserReview>>

    suspend fun createReview(
        fuelStationId: Int,
        consumerId: Int,
        request: ReviewCreateUpdateRequest
    ): Resource<Unit>

    suspend fun updateReview(
        reviewId: Int,
        request: ReviewCreateUpdateRequest
    ): Resource<Unit>

    suspend fun deleteReview(
        reviewId: Int
    ): Resource<Unit>
}