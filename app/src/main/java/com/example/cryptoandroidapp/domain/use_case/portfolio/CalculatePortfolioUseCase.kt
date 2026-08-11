package com.example.cryptoandroidapp.domain.use_case.portfolio

import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.domain.model.UserPortfolioAsset
import javax.inject.Inject

data class PortfolioCalculationResult(
    val totalValueUsd: Double,
    val totalChangeUsd: Double,
    val totalChangePercent: Double,
    val isPositive: Boolean
)

/**
 * Portföy bakiyesi ve kâr/zarar oranını hesaplayan tekil Domain İş Kuralı (UseCase).
 * HomeViewModel, PortfolioViewModel ve ProfileViewModel arasındaki kod tekrarını (DRY) önler.
 */
class CalculatePortfolioUseCase @Inject constructor() {

    operator fun invoke(assets: List<UserPortfolioAsset>, coins: List<CryptoModel>): PortfolioCalculationResult {
        if (assets.isEmpty()) {
            return PortfolioCalculationResult(
                totalValueUsd = 0.0,
                totalChangeUsd = 0.0,
                totalChangePercent = 0.0,
                isPositive = true
            )
        }

        val totalValue = assets.sumOf { asset ->
            val coin = coins.find { it.id == asset.coinId || it.symbol.equals(asset.symbol, ignoreCase = true) }
            val currentPrice = coin?.currentPrice ?: 0.0
            currentPrice * asset.amount
        }

        val previousValue = assets.sumOf { asset ->
            val coin = coins.find { it.id == asset.coinId || it.symbol.equals(asset.symbol, ignoreCase = true) }
            val price = coin?.currentPrice ?: 0.0
            val change = coin?.priceChangePercentage24h ?: 0.0
            if (change == -100.0) 0.0 else (price / (1 + change / 100.0)) * asset.amount
        }

        val diffUsd = totalValue - previousValue
        val percent = if (previousValue > 0) (diffUsd / previousValue) * 100 else 0.0

        return PortfolioCalculationResult(
            totalValueUsd = totalValue,
            totalChangeUsd = diffUsd,
            totalChangePercent = percent,
            isPositive = percent >= 0
        )
    }
}
