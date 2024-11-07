package com.dimitriskikidis.fuelapp.di

import com.dimitriskikidis.fuelapp.data.repository.DefaultFuelRepository
import com.dimitriskikidis.fuelapp.domain.repository.FuelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFuelRepository(
        fakeFuelRepositoryImpl: DefaultFuelRepository
    ): FuelRepository
}