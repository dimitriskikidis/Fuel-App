package com.dimitriskikidis.fuelapp.data.remote

import com.dimitriskikidis.fuelapp.data.remote.dtos.*
import com.dimitriskikidis.fuelapp.data.remote.requests.ConsumerSignInRequest
import com.dimitriskikidis.fuelapp.data.remote.requests.ConsumerSignUpRequest
import com.dimitriskikidis.fuelapp.data.remote.requests.FuelSearchRequest
import com.dimitriskikidis.fuelapp.data.remote.requests.ReviewCreateUpdateRequest
import com.dimitriskikidis.fuelapp.data.remote.responses.ConsumerSignInResponse
import com.dimitriskikidis.fuelapp.data.remote.responses.ConsumerSignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FuelApi {

    @POST("consumers/signIn")
    suspend fun signIn(
        @Body request: ConsumerSignInRequest
    ): Response<ConsumerSignInResponse>

    @POST("consumers/signUp")
    suspend fun signUp(
        @Body request: ConsumerSignUpRequest
    ): Response<ConsumerSignUpResponse>

    @GET("brands")
    suspend fun getBrands(): Response<List<BrandDto>>

    @GET("fuelTypes")
    suspend fun getFuelTypes(): Response<List<FuelTypeDto>>

    @POST("fuels/search")
    suspend fun searchFuels(
        @Body request: FuelSearchRequest
    ): Response<List<FuelSearchResultDto>>

    @GET("fuelStations/{fuelStationId}")
    suspend fun getFuelStationById(
        @Path("fuelStationId") fuelStationId: Int
    ): Response<FuelStationDto>

    @GET("fuels/fuelStations/{fuelStationId}")
    suspend fun getFuelsByFuelStationId(
        @Path("fuelStationId") fuelStationId: Int
    ): Response<List<FuelDto>>

    @GET("reviews/fuelStations/{fuelStationId}")
    suspend fun getReviewsByFuelStationId(
        @Path("fuelStationId") fuelStationId: Int
    ): Response<List<ReviewDto>>

    @GET("reviews/consumers/{consumerId}")
    suspend fun getUserReviewsByConsumerId(
        @Path("consumerId") consumerId: Int
    ): Response<List<ReviewDto>>

    @POST("reviews/fuelStations/{fuelStationId}/consumers/{consumerId}")
    suspend fun createReview(
        @Path("fuelStationId") fuelStationId: Int,
        @Path("consumerId") consumerId: Int,
        @Body request: ReviewCreateUpdateRequest
    ): Response<Unit>

    @PUT("reviews/{reviewId}")
    suspend fun updateReview(
        @Path("reviewId") reviewId: Int,
        @Body request: ReviewCreateUpdateRequest
    ): Response<Unit>

    @DELETE("reviews/{reviewId}")
    suspend fun deleteReview(
        @Path("reviewId") reviewId: Int
    ): Response<Unit>

    companion object {
        const val BASE_URL = "http://10.0.2.2:8080/api/v1/"
    }
}