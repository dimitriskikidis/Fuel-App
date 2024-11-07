package com.dimitriskikidis.fuelapp.data.repository

import com.dimitriskikidis.fuelapp.data.mappers.*
import com.dimitriskikidis.fuelapp.data.remote.FuelApi
import com.dimitriskikidis.fuelapp.data.remote.dtos.*
import com.dimitriskikidis.fuelapp.data.remote.requests.ConsumerSignInRequest
import com.dimitriskikidis.fuelapp.data.remote.requests.ConsumerSignUpRequest
import com.dimitriskikidis.fuelapp.data.remote.requests.FuelSearchRequest
import com.dimitriskikidis.fuelapp.data.remote.requests.ReviewCreateUpdateRequest
import com.dimitriskikidis.fuelapp.data.remote.responses.ConsumerSignInResponse
import com.dimitriskikidis.fuelapp.data.remote.responses.ConsumerSignUpResponse
import com.dimitriskikidis.fuelapp.domain.models.*
import com.dimitriskikidis.fuelapp.domain.repository.FuelRepository
import com.dimitriskikidis.fuelapp.presentation.DataState
import com.dimitriskikidis.fuelapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class DefaultFuelRepository @Inject constructor(
    private val api: FuelApi
) : FuelRepository {

    private val _dataState = MutableStateFlow(DataState())
    val dataState = _dataState.asStateFlow()

    override suspend fun updateDataState(dataState: DataState) {
        _dataState.update { dataState }
    }

    override suspend fun signIn(
        request: ConsumerSignInRequest
    ): Resource<ConsumerSignInResponse> {
        return handleApiCall {
            api.signIn(request)
        }
    }

    override suspend fun signUp(
        request: ConsumerSignUpRequest
    ): Resource<ConsumerSignUpResponse> {
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

    override suspend fun searchFuels(
        request: FuelSearchRequest
    ): Resource<List<FuelSearchResult>> {
        return handleApiCall(
            apiCall = {
                api.searchFuels(request)
            },
            mapper = { fuelSearchResultDtos: List<FuelSearchResultDto>? ->
                fuelSearchResultDtos?.map { it.toFuelSearchResult() } ?: emptyList()
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

    override suspend fun getUserReviewsByConsumerId(consumerId: Int): Resource<List<UserReview>> {
        return handleApiCall(
            apiCall = {
                api.getUserReviewsByConsumerId(consumerId)
            },
            mapper = { reviewDtos: List<ReviewDto>? ->
                reviewDtos?.map { it.toUserReview() } ?: emptyList()
            }
        )
    }

    override suspend fun createReview(
        fuelStationId: Int,
        consumerId: Int,
        request: ReviewCreateUpdateRequest
    ): Resource<Unit> {
        return handleApiCall {
            api.createReview(fuelStationId, consumerId, request)
        }
    }

    override suspend fun updateReview(
        reviewId: Int,
        request: ReviewCreateUpdateRequest
    ): Resource<Unit> {
        return handleApiCall {
            api.updateReview(reviewId, request)
        }
    }

    override suspend fun deleteReview(
        reviewId: Int
    ): Resource<Unit> {
        return handleApiCall {
            api.deleteReview(reviewId)
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
                        connectionTimedOut = false,
                        isUnauthorized = isUnauthorized
                    )
                }
            } catch (e: HttpException) {
                Resource.Error(
                    "An error has occurred.",
                    connectionTimedOut = false,
                    isUnauthorized = false
                )
            } catch (e: IOException) {
                Resource.Error(
                    "The connection has timed out.",
                    connectionTimedOut = true,
                    isUnauthorized = false
                )
            } catch (e: Exception) {
                Resource.Error(
                    "An error has occurred.",
                    connectionTimedOut = false,
                    isUnauthorized = false
                )
            }
        }
    }
}