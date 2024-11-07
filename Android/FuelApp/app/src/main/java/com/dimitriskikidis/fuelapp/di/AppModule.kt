package com.dimitriskikidis.fuelapp.di

import android.app.Application
import com.dimitriskikidis.fuelapp.data.preferences.PreferencesManager
import com.dimitriskikidis.fuelapp.data.remote.AuthInterceptor
import com.dimitriskikidis.fuelapp.data.remote.FuelApi
import com.dimitriskikidis.fuelapp.data.remote.UserManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

    @Provides
    @Singleton
    fun providePreferencesManager(application: Application): PreferencesManager {
        return PreferencesManager(application.applicationContext)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(application: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(application)
    }
}