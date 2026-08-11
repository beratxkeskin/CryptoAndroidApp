package com.example.cryptoandroidapp.domain.use_case.coin

import com.example.cryptoandroidapp.domain.repository.ICryptoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLivePriceUseCase @Inject constructor(
    private val cryptoRepository: ICryptoRepository
) {
    operator fun invoke(symbol: String): Flow<Double> {
        return cryptoRepository.getCryptoPriceStream(symbol)
    }
}
