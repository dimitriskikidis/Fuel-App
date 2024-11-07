package com.dimitriskikidis.admin.fuelapp.data.repository

import com.dimitriskikidis.admin.fuelapp.data.mappers.toBrand
import com.dimitriskikidis.admin.fuelapp.data.mappers.toBrandFuel
import com.dimitriskikidis.admin.fuelapp.data.mappers.toFuelType
import com.dimitriskikidis.admin.fuelapp.data.remote.FuelApi
import com.dimitriskikidis.admin.fuelapp.data.remote.dtos.BrandDto
import com.dimitriskikidis.admin.fuelapp.data.remote.dtos.BrandFuelDto
import com.dimitriskikidis.admin.fuelapp.data.remote.dtos.FuelTypeDto
import com.dimitriskikidis.admin.fuelapp.data.remote.requests.*
import com.dimitriskikidis.admin.fuelapp.data.remote.responses.AdminSignInSignUpResponse
import com.dimitriskikidis.admin.fuelapp.domain.models.Brand
import com.dimitriskikidis.admin.fuelapp.domain.models.BrandFuel
import com.dimitriskikidis.admin.fuelapp.domain.models.FuelType
import com.dimitriskikidis.admin.fuelapp.domain.repository.FuelRepository
import com.dimitriskikidis.admin.fuelapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class DefaultFuelRepository @Inject constructor(
    private val api: FuelApi
) : FuelRepository {

    override suspend fun signIn(request: AdminSignInRequest): Resource<AdminSignInSignUpResponse> {
        return handleApiCall {
            api.signIn(request)
        }
    }

    override suspend fun signUp(request: AdminSignUpRequest): Resource<AdminSignInSignUpResponse> {
        return handleApiCall {
            api.signUp(request)
        }
    }

    override suspend fun getBrands(): Resource<List<Brand>> {
        return handleApiCall(
            apiCall = {
                api.getBrands()
            },
            mapper = { brandDtos: List<BrandDto>? ->
                brandDtos?.map { it.toBrand() } ?: emptyList()
            }
        )
    }

    override suspend fun createBrand(
        request: BrandCreateUpdateRequest
    ): Resource<Unit> {
        return handleApiCall {
            api.createBrand(request)
        }
    }

    override suspend fun updateBrand(
        brandId: Int,
        request: BrandCreateUpdateRequest
    ): Resource<Unit> {
        return handleApiCall {
            api.updateBrand(brandId, request)
        }
    }

    override suspend fun deleteBrand(
        brandId: Int
    ): Resource<Unit> {
        return handleApiCall {
            api.deleteBrand(brandId)
        }
    }

    override suspend fun getFuelTypes(): Resource<List<FuelType>> {
        return handleApiCall(
            apiCall = {
                api.getFuelTypes()
            },
            mapper = { fuelTypeDtos: List<FuelTypeDto>? ->
                fuelTypeDtos?.map { it.toFuelType() } ?: emptyList()
            }
        )
    }

    override suspend fun createFuelType(request: FuelTypeCreateUpdateRequest): Resource<Unit> {
        return handleApiCall {
            api.createFuelType(request)
        }
    }

    override suspend fun updateFuelType(
        fuelTypeId: Int,
        request: FuelTypeCreateUpdateRequest
    ): Resource<Unit> {
        return handleApiCall {
            api.updateFuelType(fuelTypeId, request)
        }
    }

    override suspend fun deleteFuelType(fuelTypeId: Int): Resource<Unit> {
        return handleApiCall {
            api.deleteFuelType(fuelTypeId)
        }
    }

    override suspend fun getBrandFuels(): Resource<List<BrandFuel>> {
        return handleApiCall(
            apiCall = {
                api.getBrandFuels()
            },
            mapper = { brandFuelDtos: List<BrandFuelDto>? ->
                brandFuelDtos?.map { it.toBrandFuel() } ?: emptyList()
            }
        )
    }

    override suspend fun updateBrandFuel(
        brandFuelId: Int,
        request: BrandFuelUpdateRequest
    ): Resource<Unit> {
        return handleApiCall {
            api.updateBrandFuel(brandFuelId, request)
        }
    }

    private suspend fun <T : Any> handleApiCall(
        apiCall: suspend () -> Response<T>
    ): Resource<T> {
        return handleApiCall(apiCall) { t: T? -> t }
    }

    private suspend fun <TIn : Any, TOut : Any> handleApiCall(
        apiCall: suspend () -> Response<TIn>,
        mapper: (TIn?) -> TOut?
    ): Resource<TOut> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                val body = response.body()
                if (response.isSuccessful) {
                    Resource.Success(mapper(body))
                } else {
                    val responseMessage = response.errorBody()?.string()
                    response.errorBody()?.close()
                    val message = if (!responseMessage.isNullOrEmpty()) {
                        responseMessage
                    } else {
                        "An error has occurred."
                    }
                    val isUnauthorized = response.code() == 401
                    Resource.Error(
                        message,
                        isUnauthorized
                    )
                }
            } catch (e: HttpException) {
                Resource.Error(
                    "An error has occurred.",
                    false
                )
            } catch (e: IOException) {
                Resource.Error(
                    "The connection has timed out.",
                    false
                )
            } catch (e: Exception) {
                Resource.Error(
                    "An error has occurred.",
                    false
                )
            }
        }
    }
}