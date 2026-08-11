package com.example.cryptoandroidapp.data.repository

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.repository.IFavoritesRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : IFavoritesRepository {

    override fun getUserFavorites(userId: String): Flow<Resource<List<String>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val listener = firestore.collection("users")
            .document(userId)
            .collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.localizedMessage ?: "Unknown error"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val coinIds = snapshot.documents.map { it.id }
                    trySend(Resource.Success(coinIds))
                }
            }
            
        awaitClose { listener.remove() }
    }

    override suspend fun addFavorite(userId: String, coinId: String): Resource<Unit> {
        return try {
            val data = mapOf("timestamp" to System.currentTimeMillis())
            firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(coinId)
                .set(data)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to add favorite")
        }
    }

    override suspend fun removeFavorite(userId: String, coinId: String): Resource<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(coinId)
                .delete()
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to remove favorite")
        }
    }

    override suspend fun isFavorite(userId: String, coinId: String): Resource<Boolean> {
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(coinId)
                .get()
                .await()
            Resource.Success(doc.exists())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to check favorite")
        }
    }
}
