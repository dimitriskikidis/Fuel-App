package com.dimitriskikidis.admin.fuelapp.data.remote

import com.dimitriskikidis.admin.fuelapp.data.remote.dtos.BrandDto
import com.dimitriskikidis.admin.fuelapp.data.remote.dtos.BrandFuelDto
import com.dimitriskikidis.admin.fuelapp.data.remote.dtos.FuelTypeDto
import com.dimitriskikidis.admin.fuelapp.data.remote.requests.*
import com.dimitriskikidis.admin.fuelapp.data.remote.responses.AdminSignInSignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FuelApi {

    @POST("admins/signIn")
    suspend fun signIn(
        @Body request: AdminSignInRequest
    ): Response<AdminSignInSignUpResponse>

    @POST("admins/signUp")
    suspend fun signUp(
        @Body request: AdminSignUpRequest
    ): Response<AdminSignInSignUpResponse>

    @GET("brands")
    suspend fun getBrands(): Response<List<BrandDto>>

    @POST("brands")
    suspend fun createBrand(
        @Body request: BrandCreateUpdateRequest
    ): Response<Unit>

    @PUT("brands/{brandId}")
    suspend fun updateBrand(
        @Path("brandId") brandId: Int,
        @Body request: BrandCreateUpdateRequest
    ): Response<Unit>

    @DELETE("brands/{brandId}")
    suspend fun deleteBrand(
        @Path("brandId") brandId: Int
    ): Response<Unit>

    @GET("fuelTypes")
    suspend fun getFuelTypes(): Response<List<FuelTypeDto>>

    @POST("fuelTypes")
    suspend fun createFuelType(
        @Body request: FuelTypeCreateUpdateRequest
    ): Response<Unit>

    @PUT("fuelTypes/{fuelTypeId}")
    suspend fun updateFuelType(
        @Path("fuelTypeId") fuelTypeId: Int,
        @Body request: FuelTypeCreateUpdateRequest
    ): Response<Unit>

    @DELETE("fuelTypes/{fuelTypeId}")
    suspend fun deleteFuelType(
        @Path("fuelTypeId") fuelTypeId: Int
    ): Response<Unit>

    @GET("brandFuels")
    suspend fun getBrandFuels(): Response<List<BrandFuelDto>>

    @PUT("brandFuels/{brandFuelId}")
    suspend fun updateBrandFuel(
        @Path("brandFuelId") brandFuelId: Int,
        @Body request: BrandFuelUpdateRequest
    ): Response<Unit>

    companion object {
        const val BASE_URL = "http://10.0.2.2:8080/api/v1/"
    }
}