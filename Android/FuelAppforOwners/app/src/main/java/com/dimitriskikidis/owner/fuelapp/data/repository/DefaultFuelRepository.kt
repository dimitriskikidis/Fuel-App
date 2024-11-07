package com.dimitriskikidis.owner.fuelapp.data.repository

import com.dimitriskikidis.owner.fuelapp.data.mappers.*
import com.dimitriskikidis.owner.fuelapp.data.remote.FuelApi
import com.dimitriskikidis.owner.fuelapp.data.remote.dtos.BrandDto
import com.dimitriskikidis.owner.fuelapp.data.remote.dtos.BrandFuelDto
import com.dimitriskikidis.owner.fuelapp.data.remote.dtos.FuelDto
import com.dimitriskikidis.owner.fuelapp.data.remote.dtos.ReviewDto
import com.dimitriskikidis.owner.fuelapp.data.remote.requests.*
import com.dimitriskikidis.owner.fuelapp.data.remote.responses.FuelStationCreateResponse
import com.dimitriskikidis.owner.fuelapp.data.remote.responses.OwnerSignInResponse
import com.dimitriskikidis.owner.fuelapp.data.remote.responses.OwnerSignUpResponse
import com.dimitriskikidis.owner.fuelapp.domain.models.*
import com.dimitriskikidis.owner.fuelapp.domain.repository.FuelRepository
import com.dimitriskikidis.owner.fuelapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class DefaultFuelRepository @Inject constructor(
    private val api: FuelApi
) : FuelRepository {

    override suspend fun signIn(request: OwnerSignInRequest): Resource<OwnerSignInResponse> {
        return handleApiCall {
            api.signIn(request)
        }
    }

    override suspend fun signUp(request: OwnerSignUpRequest): Resource<OwnerSignUpResponse> {
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

    override suspend fun getFuelStationById(
        fuelStationId: Int
    ): Resource<FuelStation> {
        return handleApiCall(
            apiCall = {
                api.getFuelStationById(fuelStationId)
            },
            mapper = { it?.toFuelStation() }
        )
    }

    override suspend fun getFuelStationByOwnerId(
        ownerId: Int
    ): Resource<FuelStation> {
        return handleApiCall(
            apiCall = {
                api.getFuelStationByOwnerId(ownerId)
            },
            mapper = { it?.toFuelStation() }
        )
    }

    override suspend fun createFuelStation(
        ownerId: Int,
        request: FuelStationCreateUpdateRequest
    ): Resource<FuelStationCreateResponse> {
        return handleApiCall {
            api.createFuelStation(ownerId, request)
        }
    }

    override suspend fun updateFuelStation(
        fuelStationId: Int,
        request: FuelStationCreateUpdateRequest
    ): Resource<Unit> {
        return handleApiCall {
            api.updateFuelStation(fuelStationId, request)
        }
    }

    override suspend fun getBrandFuelsByFuelStationId(
        fuelStationId: Int
    ): Resource<List<BrandFuel>> {
        return handleApiCall(
            apiCall = {
                api.getBrandFuelsByFuelStationId(fuelStationId)
            },
            mapper = { brandFuelDtos: List<BrandFuelDto>? ->
                brandFuelDtos?.map { it.toBrandFuel() } ?: emptyList()
            }
        )
    }

    override suspend fun getReviewsByFuelStationId(
        fuelStationId: Int
    ): Resource<List<Review>> {
        return handleApiCall(
            apiCall = {
                api.getReviewsByFuelStationId(fuelStationId)
            },
            mapper = { reviewDtos: List<ReviewDto>? ->
                reviewDtos?.map { it.toReview() } ?: emptyList()
            }
        )
    }

    override suspend fun getFuelsByFuelStationId(
        fuelStationId: Int
    ): Resource<List<Fuel>> {
        return handleApiCall(
            apiCall = {
                api.getFuelsByFuelStationId(fuelStationId)
            },
            mapper = { fuelDtos: List<FuelDto>? ->
                fuelDtos?.map { it.toFuel() } ?: emptyList()
            }
        )
    }

    override suspend fun createFuel(
        fuelStationId: Int,
        request: FuelCreateRequest
    ): Resource<Unit> {
        return handleApiCall {
            api.createFuel(fuelStationId, request)
        }
    }

    override suspend fun updateFuel(
        fuelId: Int,
        request: FuelUpdateRequest
    ): Resource<Unit> {
        return handleApiCall {
            api.updateFuel(fuelId, request)
        }
    }

    override suspend fun deleteFuel(
        fuelId: Int
    ): Resource<Unit> {
        return handleApiCall {
            api.deleteFuel(fuelId)
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