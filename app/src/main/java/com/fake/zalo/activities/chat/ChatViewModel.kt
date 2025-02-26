package com.fake.zalo.activities.chat

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fake.zalo.helper.DatabaseHelper
import com.fake.zalo.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private lateinit var dbHelper: DatabaseHelper
    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    fun init(context: Context) {
        dbHelper = DatabaseHelper(context)
    }

    fun loadGuest(currentPhone: String, onResult: (Pair<String, String>) -> Unit) {
        db.collection("users")
            .whereNotEqualTo("phone", currentPhone)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onResult.invoke("" to "")
                } else {
                    val guestName = result.documents.firstOrNull()?.getString("display_name") ?: ""
                    val guestAvatar = result.documents.firstOrNull()?.getString("avatar") ?: ""
                    onResult.invoke(guestName to guestAvatar)
                }
            }
    }

    fun insertMessage(message: Message) {
        viewModelScope.launch {
            val result = dbHelper.insertMessage(message)
            Log.d("GT45_x", "insert id = $result")
        }
    }

    fun getLimitedMessages(limit: Int = 30, onResult: ((List<Message>) -> Unit)) {
        viewModelScope.launch {
            val result = dbHelper.getLimitedMessages(limit)
            viewModelScope.launch(Dispatchers.Main) {
                onResult.invoke(result)
            }
        }
    }
}