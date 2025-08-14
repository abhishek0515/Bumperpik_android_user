package com.bumperpick.bumperpickvendor.API.Provider

import DataStoreManager
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.bumperpick.bumperickUser.API.New_model.error_model
import com.bumperpick.bumperickUser.API.New_model.refreshtoken
import com.bumperpick.bumperpickvendor.API.Model.success_model
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.java.KoinJavaComponent.getKoin
import retrofit2.Response
import java.io.File
import kotlin.coroutines.cancellation.CancellationException
import kotlin.jvm.java

sealed class ApiResult<out T, out E> {
    data class Success<out T>(val data: T) : ApiResult<T, Nothing>()
    data class Error<out E>(val error: E, val code: Int? = null) : ApiResult<Nothing, E>()
}

suspend fun <T, E> safeApiCall(
    context: Context? = getKoin().get(),
    api: suspend () -> Response<T>,
    errorBodyParser: (String) -> E
): ApiResult<T, E> {
    return try {
        val response = api()
        Log.d("RESPONSE", response.body().toString())

        if (response.isSuccessful) {
            response.body()?.let { ApiResult.Success(it) }
                ?: ApiResult.Error(errorBodyParser("Empty body"), response.code())
        } else {
            val errorBodyStr = response.errorBody()?.string().orEmpty()
            val parsedError = errorBodyParser(errorBodyStr)

            if (errorBodyStr.contains("Unauthenticated", true) || response.code() == 401) {
                val tokenRefreshed = refreshTokenDirectly(context)
                if (tokenRefreshed) {
                    val retryResponse = api()
                    return if (retryResponse.isSuccessful) {
                        retryResponse.body()?.let { ApiResult.Success(it) }
                            ?: ApiResult.Error(errorBodyParser("Empty body on retry"), retryResponse.code())
                    } else {
                        val retryErrorStr = retryResponse.errorBody()?.string().orEmpty()
                        ApiResult.Error(errorBodyParser(retryErrorStr), retryResponse.code())
                    }
                } else {
                    ApiResult.Error(parsedError, response.code())
                }
            } else {
                ApiResult.Error(parsedError, response.code())
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiResult.Error(errorBodyParser(e.localizedMessage ?: "Unknown error"))
    }
}





suspend fun refreshTokenDirectly(context: Context?): Boolean {
    if (context == null) return false

    val dataStoreManager = DataStoreManager(context)
    val apiService: ApiService = getKoin().get()

    return try {
        val previousToken = dataStoreManager.getToken.firstOrNull()?:""
        val call = apiService.token_refresh(previousToken)

        if (call.isSuccessful) {
            call.body()?.meta?.let { newMeta ->
                dataStoreManager.saveUserId(token = newMeta.token,call.body()?.data?.customer_id.toString())
                Log.d("Token", "Token refreshed and saved.")
                return true
            }
        } else {
            val errorBody = call.errorBody()?.string().orEmpty()
            Log.d("RefreshTokenError", errorBody)
        }
        false
    } catch (e: Exception) {
        Log.e("Exception", "Token refresh failed: ${e.localizedMessage}")
        false
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
