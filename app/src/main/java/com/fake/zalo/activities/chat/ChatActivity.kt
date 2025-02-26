package com.fake.zalo.activities.chat

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fake.zalo.R
import com.fake.zalo.databinding.ActivityChatBinding
import com.fake.zalo.model.Message
import com.fake.zalo.ultis.getNavigationBarHeight
import com.fake.zalo.ultis.getStatusBarHeight
import java.util.UUID

class ChatActivity : AppCompatActivity() {

    private var _binding: ActivityChatBinding? = null
    private val binding: ActivityChatBinding get() = requireNotNull(_binding)
    private lateinit var viewModel: ChatViewModel

    private val currentUserId = "id_1"
    private val messageAdapter: MessageAdapter by lazy {
        MessageAdapter(currentUserId)
    }
    private val messages = arrayListOf<Message>()
    private val currentId: String by lazy {
        intent?.getStringExtra("current_id") ?: ""
    }
    private val currentPhone: String by lazy {
        intent?.getStringExtra("phone") ?: ""
    }

    private val statusBarHeight: Int by lazy {
        getStatusBarHeight(this)
    }
    private val navigationBarHeight: Int by lazy {
        getNavigationBarHeight(this, binding.root)
    }
    private var heightStatusBar: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this).get(ChatViewModel::class)
        viewModel.init(this)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.loadGuest(currentPhone) {
            binding.tvName.text = it.first
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
            if (heightStatusBar == -1) {
                heightStatusBar = statusBarHeight
                Log.d("GT45_x", "height status bar = $heightStatusBar")
                binding.header.setPadding(0, heightStatusBar, 0, 0)
            }
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.height
            val keypadHeight = screenHeight - rect.bottom

            val transY = -keypadHeight.toFloat() + navigationBarHeight
            if (keypadHeight > screenHeight * 0.15) {
                binding.bottom.translationY = transY
                updateLayoutParamsListMessages(keypadHeight)
            } else {
                binding.bottom.translationY = 0f
                updateLayoutParamsListMessages(keypadHeight)
            }
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun updateLayoutParamsListMessages(keypadHeight: Int) {
        binding.recyclerView.updateLayoutParams {
            height = 0
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.main)
            constraintSet.clear(binding.recyclerView.id, ConstraintSet.TOP)
            constraintSet.clear(binding.recyclerView.id, ConstraintSet.BOTTOM)
            constraintSet.connect(
                binding.recyclerView.id,
                ConstraintSet.BOTTOM,
                binding.main.id,
                ConstraintSet.BOTTOM,
                keypadHeight
            )
            constraintSet.connect(
                binding.recyclerView.id,
                ConstraintSet.TOP,
                binding.header.id,
                ConstraintSet.BOTTOM
            )
            constraintSet.applyTo(binding.main)
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.insertMessage(
//            Message(
//                UUID.randomUUID().toString(),
//                "id_1",
//                "Tin nhắn 11",
//                listOf("A1", "A2", "A3", "A4"),
//                "text"
//            )
//        )
//        viewModel.insertMessage(
//            Message(
//                UUID.randomUUID().toString(),
//                "id_2",
//                "Tin nhắn 12",
//                listOf("A1", "A2", "A3", "A4"),
//                "text"
//            )
//        )
//        viewModel.insertMessage(
//            Message(
//                UUID.randomUUID().toString(),
//                "id_2",
//                "Tin nhắn 2",
//                listOf("A1", "A2", "A3", "A4"),
//                "text"
//            )
//        )
//        viewModel.insertMessage(
//            Message(
//                UUID.randomUUID().toString(),
//                "id_2",
//                "Tin nhắn 3",
//                listOf("A1", "A2", "A3", "A4"),
//                "text"
//            )
//        )
//        viewModel.insertMessage(
//            Message(
//                UUID.randomUUID().toString(),
//                "id_1",
//                "Tin nhắn 4",
//                listOf("A1", "A2", "A3", "A4"),
//                "text"
//            )
//        )
//        viewModel.insertMessage(
//            Message(
//                UUID.randomUUID().toString(),
//                "id_1",
//                "Tin nhắn 5",
//                listOf("A1", "A2", "A3", "A4"),
//                "image"
//            )
//        )
        viewModel.getLimitedMessages() {
            messages.clear()
            messages.addAll(it)
            messageAdapter.submitList(messages.toList())
        }
    }

    private fun sendMessage(msg: String) {
        if (msg.isEmpty()) return
        val message = Message("temp_send" + System.currentTimeMillis(), currentUserId, msg, listOf(), "text")
        messages.add(message)
        messageAdapter.submitList(messages.toList())
    }

    private fun loadMessages() {
//        db.collection("messages")
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .limit(30)
//            .startAfter()
//            .get()
//            .addOnSuccessListener {
//
//            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}