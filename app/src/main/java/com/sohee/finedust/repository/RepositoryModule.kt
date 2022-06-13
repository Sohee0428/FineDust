package com.sohee.finedust.repository

import com.sohee.finedust.repository.local.LocalDataSource
import com.sohee.finedust.repository.local.LocalDataSourceImpl
import com.sohee.finedust.repository.remote.RemoteDataSource
import com.sohee.finedust.repository.remote.RemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MainBindRepository {
    @Binds
    abstract fun mainBindRepository(repository: MainRepositoryImpl): MainRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalBindRepository {
    @Binds
    abstract fun localBindRepository(repository: LocalDataSourceImpl): LocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteBindRepository {
    @Binds
    abstract fun remoteBindRepository(repository: RemoteDataSourceImpl): RemoteDataSource
}