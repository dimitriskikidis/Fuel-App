package com.dimitriskikidis.owner.fuelapp.di

import android.app.Application
import com.dimitriskikidis.owner.fuelapp.data.remote.AuthInterceptor
import com.dimitriskikidis.owner.fuelapp.data.remote.FuelApi
import com.dimitriskikidis.owner.fuelapp.data.remote.UserManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApi(okHttpClient: OkHttpClient): FuelApi {
        return Retrofit.Builder()
            .baseUrl(FuelApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(userManager: UserManager): AuthInterceptor {
        return AuthInterceptor(userManager)
    }

    @Provides
    @Singleton
    fun provideUserManager(application: Application): UserManager {
        return UserManager(application.applicationContext)
    }
}