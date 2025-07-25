// DataStoreExtensions.kt (create a separate file or place at top of DataStoreManager.kt)
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.core.DataStore



import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")
class DataStoreManager(private val context: Context) {

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val TOKEN=stringPreferencesKey("token")
    }

    private val dataStore = context.dataStore
    suspend fun clearToken(){
        dataStore.edit { prefs->
            prefs.remove(USER_ID)
            prefs.remove(TOKEN)
        }
    }
    suspend fun saveUserId(token: String,userId:String) {
        dataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[TOKEN] =token
        }
    }

    val getUserId: Flow<String?> = dataStore.data.map { prefs ->
        prefs[USER_ID]
    }

    val getToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[TOKEN]
    }

}
