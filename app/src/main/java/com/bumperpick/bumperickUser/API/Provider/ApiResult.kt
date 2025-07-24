package com.bumperpick.bumperpickvendor.API.Provider

import DataStoreManager
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.bumperpick.bumperickUser.API.New_model.refreshtoken
import com.bumperpick.bumperpickvendor.API.Model.success_model
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(
    context: Context,
    api: suspend () -> Response<T>,
    refreshTokenApi: suspend (String) -> Response<refreshtoken>,
    dataStoreManager: DataStoreManager
): ApiResult<T> {
    return try {
        val response = api()
        Log.d("RESPONSE", response.toString())

        if (response.isSuccessful) {
            response.body()?.let { ApiResult.Success(it) }
                ?: ApiResult.Error("Empty body", response.code())
        } else {
            // Check for unauthenticated
            if (response.code() == 401 || response.message().contains("Unauthenticated", true)) {
                // Get current token
                val currentToken = dataStoreManager.getToken.firstOrNull()
                if (currentToken != null) {
                    val refreshResponse = refreshTokenApi(currentToken)

                    if (refreshResponse.isSuccessful) {
                        val newToken = refreshResponse.body()?.meta?.token
                        val userId = refreshResponse.body()?.data?.customer_id.toString() // adjust if needed

                        if (!newToken.isNullOrEmpty()) {
                            // Save new token
                            dataStoreManager.saveUserId(newToken, userId)

                            // Retry original API with new token
                            val retryResponse = api()
                            return if (retryResponse.isSuccessful) {
                                retryResponse.body()?.let { ApiResult.Success(it) }
                                    ?: ApiResult.Error("Empty body after retry", retryResponse.code())
                            } else {
                                ApiResult.Error("Retry failed: ${retryResponse.message()}", retryResponse.code())
                            }
                        }
                    }
                    return ApiResult.Error("Token refresh failed: ${refreshResponse.message()}", refreshResponse.code())
                } else {
                    return ApiResult.Error("No token found for refresh")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val gson = Gson()
                val errorResponse = gson.fromJson(errorBody, success_model::class.java)

                Log.d("Error",errorResponse.message?:"")
                ApiResult.Error("Error: ${errorResponse.message}", response.code())
            }
        }
    } catch (e: Exception) {
        ApiResult.Error("Exception: ${e.localizedMessage ?: "Unknown error"}")
    }
}

fun File.toMultipartPart(
    partName: String = "file",
    contentType: String = "application/octet-stream"
): MultipartBody.Part {
    val requestBody = this.asRequestBody(contentType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, this.name, requestBody)
}

fun String.toPlainTextRequestBody(): RequestBody =
    this.toRequestBody("text/plain".toMediaTypeOrNull())
fun Context.uriToFile(uri: Uri): File? {
    val fileName = getFileName(uri) ?: return null
    val inputStream = contentResolver.openInputStream(uri) ?: return null
    val tempFile = File(cacheDir, fileName)

    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }

    return tempFile
}

fun Context.getFileName(uri: Uri): String? {
    var name: String? = null
    if (uri.scheme == "content") {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) name = cursor.getString(idx)
            }
        }
    }
    if (name == null) {
        name = uri.path?.substringAfterLast('/')
    }
    return name
}
