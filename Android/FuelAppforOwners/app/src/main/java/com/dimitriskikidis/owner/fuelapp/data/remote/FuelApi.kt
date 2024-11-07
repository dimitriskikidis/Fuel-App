package com.dimitriskikidis.owner.fuelapp.data.remote

import com.dimitriskikidis.owner.fuelapp.data.remote.dtos.*
import com.dimitriskikidis.owner.fuelapp.data.remote.requests.*
import com.dimitriskikidis.owner.fuelapp.data.remote.responses.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FuelApi {

    @POST("owners/signIn")
    suspend fun signIn(
        @Body request: OwnerSignInRequest
    ): Response<OwnerSignInResponse>

    @POST("owners/signUp")
    suspend fun signUp(
        @Body request: OwnerSignUpRequest
    ): Response<OwnerSignUpResponse>

    @GET("brands")
    suspend fun getBrands(): Response<List<BrandDto>>

    @GET("fuelStations/{fuelStationId}")
    suspend fun getFuelStationById(
        @Path("fuelStationId") fuelStationId: Int
    ): Response<FuelStationDto>

    @GET("fuelStations/owners/{ownerId}")
    suspend fun getFuelStationByOwnerId(
        @Path("ownerId") ownerId: Int
    ): Response<FuelStationDto>

    @POST("fuelStations/owners/{ownerId}")
    suspend fun createFuelStation(
        @Path("ownerId") ownerId: Int,
        @Body request: FuelStationCreateUpdateRequest
    ): Response<FuelStationCreateResponse>

    @PUT("fuelStations/{fuelStationId}")
    suspend fun updateFuelStation(
        @Path("fuelStationId") fuelStationId: Int,
        @Body request: FuelStationCreateUpdateRequest
    ): Response<Unit>

    @GET("brandFuels/fuelStations/{fuelStationId}")
    suspend fun getBrandFuelsByFuelStationId(
        @Path("fuelStationId") fuelStationId: Int
    ): Response<List<BrandFuelDto>>

    @GET("reviews/fuelStations/{fuelStationId}")
    suspend fun getReviewsByFuelStationId(
        @Path("fuelStationId") fuelStationId: Int
    ): Response<List<ReviewDto>>

    @GET("fuels/fuelStations/{fuelStationId}")
    suspend fun getFuelsByFuelStationId(
        @Path("fuelStationId") fuelStationId: Int
    ): Response<List<FuelDto>>

    @POST("fuels/fuelStations/{fuelStationId}")
    suspend fun createFuel(
        @Path("fuelStationId") fuelStationId: Int,
        @Body request: FuelCreateRequest
    ): Response<Unit>

    @PUT("fuels/{fuelId}")
    suspend fun updateFuel(
        @Path("fuelId") fuelId: Int,
        @Body request: FuelUpdateRequest
    ): Response<Unit>

    @DELETE("fuels/{fuelId}")
    suspend fun deleteFuel(
        @Path("fuelId") fuelId: Int
    ): Response<Unit>

    companion object {
        const val BASE_URL = "http://10.0.2.2:8080/api/v1/"
    }
}