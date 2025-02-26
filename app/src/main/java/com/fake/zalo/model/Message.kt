package com.fake.zalo.model

import androidx.recyclerview.widget.DiffUtil

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val content: String = "",
    val images: List<String> = listOf(),
    val type: String, // "text", "image"
    val timestamp: Long = System.currentTimeMillis()
)

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.messageId == newItem.messageId
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}