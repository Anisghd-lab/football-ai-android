package com.footballai.app.core.network

/**
 * Sealed hierarchy modeling network operation outcomes.
 */
sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T, val statusCode: Int = 200) : NetworkResult<T>
    
    data class HttpError(
        val code: Int,
        val message: String,
        val errorBody: String? = null
    ) : NetworkResult<Nothing>
    
    data class NetworkFailure(
        val exception: Throwable,
        val message: String = exception.localizedMessage ?: "Erreur de connexion réseau"
    ) : NetworkResult<Nothing>
}
