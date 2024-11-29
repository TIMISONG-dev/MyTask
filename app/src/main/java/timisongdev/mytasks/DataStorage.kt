package timisongdev.mytasks

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("userData")

class DataStorage(context: Context) {
    companion object{
        var email_key = stringPreferencesKey("email")
        val pass_key = stringPreferencesKey("password")
        var uid_key = stringPreferencesKey("uid")
        var auth_key = booleanPreferencesKey("auth")
        var name_key = stringPreferencesKey("name")
        var inn_key = stringPreferencesKey("inn")
        var card_key = stringPreferencesKey("card")
        var key_ = stringPreferencesKey("key")
    }

    val email: Flow<String?> = context.dataStore.data
        .map { pref ->
            pref[email_key]
        }
    val pass: Flow<String?> = context.dataStore.data
        .map { pref ->
            pref[pass_key]
        }
    val name: Flow<String?> = context.dataStore.data
        .map { pref ->
            pref[name_key]
        }
    val uid: Flow<String?> = context.dataStore.data
        .map { pref ->
            pref[uid_key]
        }
    val auth: Flow<Boolean?> = context.dataStore.data
        .map { pref ->
            pref[auth_key]
        }
    val inn: Flow<String?> = context.dataStore.data
        .map { pref ->
            pref[inn_key]
        }
    val card: Flow<String?> = context.dataStore.data
        .map { pref ->
            pref[card_key]
        }
    val key: Flow<String?> = context.dataStore.data
        .map { pref ->
            pref[key_]
        }

    suspend fun saveEmail(email: String, context: Context) {
        context.dataStore.edit {
                pref -> pref[email_key] = email
        }
    }
    suspend fun savePass(password: String, context: Context) {
        context.dataStore.edit {
                pref -> pref[pass_key] = password
        }
    }
    suspend fun saveUid(uid: String, context: Context) {
        context.dataStore.edit {
                pref -> pref[uid_key] = uid
        }
    }
    suspend fun saveAuth(auth: Boolean, context: Context){
        context.dataStore.edit {
                pref -> pref[auth_key] = auth
        }
    }
    suspend fun saveName(name: String, context: Context){
        context.dataStore.edit {
                pref -> pref[name_key] = name
        }
    }
    suspend fun saveInn(name: String, context: Context){
        context.dataStore.edit {
                pref -> pref[inn_key] = name
        }
    }
    suspend fun saveCard(name: String, context: Context){
        context.dataStore.edit {
                pref -> pref[card_key] = name
        }
    }
    suspend fun saveKey(name: String, context: Context){
        context.dataStore.edit {
                pref -> pref[key_] = name
        }
    }
}