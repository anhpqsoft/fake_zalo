package com.fake.zalo.activities.chat

import android.graphics.Rect
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.fake.zalo.R
import com.fake.zalo.databinding.ActivityChatBinding
import com.fake.zalo.model.Message
import com.fake.zalo.ultis.getNavigationBarHeight
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private var _binding: ActivityChatBinding? = null
    private val binding: ActivityChatBinding get() = requireNotNull(_binding)

    private val currentUserId = "id_1"
    private val messageAdapter: MessageAdapter by lazy {
        MessageAdapter(currentUserId)
    }
    private val messages = arrayListOf<Message>()
    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        binding.edtMessage.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage(binding.edtMessage.text.toString())
                true
            } else {
                false
            }
        }
        binding.btnSend.setOnClickListener {
            sendMessage(binding.edtMessage.text.toString().trim())
        }
        binding.edtMessage.doAfterTextChanged {
            binding.layoutOptions.isVisible = it.isNullOrEmpty()
            binding.btnSend.isVisible = !it.isNullOrEmpty()
        }
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.height
            val keypadHeight = screenHeight - rect.bottom

            val transY = -keypadHeight.toFloat() + getNavigationBarHeight(this, binding.root)
            if (keypadHeight > screenHeight * 0.15) {
                binding.bottom.translationY = transY
                binding.recyclerView.translationY = transY
            } else {
                binding.bottom.translationY = 0f
                binding.recyclerView.translationY = 0f
            }
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun sendMessage(msg: String) {
        if (msg.isEmpty()) return
        val message = Message("temp_send" + System.currentTimeMillis(), currentUserId, msg, listOf(), "text")
        messages.add(message)
        messageAdapter.submitList(messages.toList())
    }

    private fun loadMessages() {
        db.collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(30)
            .startAfter()
            .get()
            .addOnSuccessListener {

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}