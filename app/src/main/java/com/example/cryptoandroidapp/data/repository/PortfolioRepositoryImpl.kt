package com.example.cryptoandroidapp.data.repository

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.UserPortfolioAsset
import com.example.cryptoandroidapp.domain.repository.IPortfolioRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PortfolioRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : IPortfolioRepository {

    override fun getUserPortfolio(userId: String): Flow<Resource<List<UserPortfolioAsset>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val listener = firestore.collection("users")
            .document(userId)
            .collection("portfolio")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.localizedMessage ?: "Unknown error"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val assets = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(UserPortfolioAsset::class.java)
                    }
                    trySend(Resource.Success(assets))
                }
            }
            
        awaitClose { listener.remove() }
    }

    override suspend fun addAssetToPortfolio(userId: String, asset: UserPortfolioAsset): Resource<Unit> {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .collection("portfolio")
                .document(asset.coinId)
            val existing = document.get().await().toObject(UserPortfolioAsset::class.java)
            val mergedAsset = if (existing != null && existing.amount > 0.0) {
                val totalAmount = existing.amount + asset.amount
                val totalCost = (existing.amount * existing.addedPriceUsd) +
                    (asset.amount * asset.addedPriceUsd)
                asset.copy(
                    amount = totalAmount,
                    addedPriceUsd = if (totalAmount > 0.0) totalCost / totalAmount else asset.addedPriceUsd,
                    timestamp = minOf(existing.timestamp, asset.timestamp)
                )
            } else {
                asset
            }
            document.set(mergedAsset)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to add asset")
        }
    }

    override suspend fun removeAssetFromPortfolio(userId: String, coinId: String): Resource<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("portfolio")
                .document(coinId)
                .delete()
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to delete asset")
        }
    }
}
