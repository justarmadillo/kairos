package com.taha.kairos.authorization

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthorizationModule {
    @Binds
    @Singleton
    abstract fun bindAuthorizationClock(
        implementation: SystemAuthorizationClock,
    ): AuthorizationClock

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        implementation: ConnectivityNetworkMonitor,
    ): NetworkMonitor
}
