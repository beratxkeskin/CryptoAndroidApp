package com.example.cryptoandroidapp.data.repository

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.UserPortfolioAsset
import com.example.cryptoandroidapp.domain.repository.IPortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePortfolioRepository : IPortfolioRepository {

    // Test senaryolarındaki sahte portföy listesi
    private val portfolioList = mutableListOf(
        UserPortfolioAsset(
            coinId = "bitcoin",
            symbol = "BTC",
            amount = 0.5,
            addedPriceUsd = 55000.0,
            timestamp = 0L
        )
    )

    var shouldReturnError = false
    var errorMessage = "Firestore portföy hatası"

    override fun getUserPortfolio(userId: String): Flow<Resource<List<UserPortfolioAsset>>> {
        if (shouldReturnError) {
            return flowOf(Resource.Error(errorMessage))
        }
        return flowOf(Resource.Success(portfolioList.toList()))
    }

    override suspend fun addAssetToPortfolio(userId: String, asset: UserPortfolioAsset): Resource<Unit> {
        if (shouldReturnError) return Resource.Error(errorMessage)
        portfolioList.removeAll { it.coinId == asset.coinId }
        portfolioList.add(asset)
        return Resource.Success(Unit)
    }

    override suspend fun removeAssetFromPortfolio(userId: String, coinId: String): Resource<Unit> {
        if (shouldReturnError) return Resource.Error(errorMessage)
        portfolioList.removeAll { it.coinId == coinId }
        return Resource.Success(Unit)
    }
}