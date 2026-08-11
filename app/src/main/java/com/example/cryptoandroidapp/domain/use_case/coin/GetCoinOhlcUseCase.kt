package com.example.cryptoandroidapp.domain.use_case.coin

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.CandleModel
import com.example.cryptoandroidapp.domain.repository.ICryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCoinOhlcUseCase @Inject constructor(
    private val repository: ICryptoRepository
) {
    operator fun invoke(id: String, days: String): Flow<Resource<List<CandleModel>>> = flow {
        emit(Resource.Loading())
        emit(repository.getCoinOhlc(id, days))
    }
}