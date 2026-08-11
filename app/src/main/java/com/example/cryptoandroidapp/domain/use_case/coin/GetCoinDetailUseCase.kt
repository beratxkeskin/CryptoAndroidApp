package com.example.cryptoandroidapp.domain.use_case.coin

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.CryptoDetailModel
import com.example.cryptoandroidapp.domain.repository.ICryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCoinDetailUseCase @Inject constructor(
    private val repository: ICryptoRepository
) {
    // invoke operatörü sayesinde bu sınıf "getCoinDetailUseCase(id)" şeklinde fonksiyon gibi çağrılabilir
    operator fun invoke(id: String): Flow<Resource<CryptoDetailModel>> = flow {
        emit(Resource.Loading())
        emit(repository.getCoinDetail(id))
    }
}
