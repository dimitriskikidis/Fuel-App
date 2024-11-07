package com.dimitriskikidis.admin.fuelapp.di

import com.dimitriskikidis.admin.fuelapp.data.repository.DefaultFuelRepository
import com.dimitriskikidis.admin.fuelapp.domain.repository.FuelRepository
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
        fuelRepository: DefaultFuelRepository
    ): FuelRepository
}