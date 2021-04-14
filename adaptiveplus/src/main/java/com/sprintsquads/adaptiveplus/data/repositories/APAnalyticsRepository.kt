package com.sprintsquads.adaptiveplus.data.repositories

import com.sprintsquads.adaptiveplus.core.managers.APAuthCredentialsManager
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager


internal class APAnalyticsRepository(
    networkManager: NetworkServiceManager,
    authCredentialsManager: APAuthCredentialsManager,
    userRepository: APUserRepository
) : APBaseRepository(networkManager, authCredentialsManager, userRepository)