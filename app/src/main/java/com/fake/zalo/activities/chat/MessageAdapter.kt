package com.fake.zalo.activities.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fake.zalo.databinding.ItemMessageReceivedBinding
import com.fake.zalo.databinding.ItemMessageSentBinding
import com.fake.zalo.model.Message
import com.fake.zalo.model.MessageDiffCallback
import com.fake.zalo.ultis.HH_MM_DD_MM_YYYY
import com.fake.zalo.ultis.dp
import com.fake.zalo.ultis.formatDateTime
import com.fake.zalo.ultis.formatTime
import com.fake.zalo.ultis.isDifferentDay

class MessageAdapter(private val currentUserId: String) :
    ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentMessageViewHolder(binding)
        } else {
            val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        val lastMessage = try {
            getItem(position - 1)
        } catch (ex: Exception) {
            null
        }
        val nextMessage = try {
            getItem(position + 1)
        } catch (ex: Exception) {
            null
        }
        if (holder is SentMessageViewHolder) {
            holder.bind(message, lastMessage, nextMessage)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message, lastMessage, nextMessage)
        }
    }

    inner class SentMessageViewHolder(private val binding: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, lastMessage: Message?, nextMessage: Message?) {
            binding.tvMessage.text = message.content
            binding.tvTime.text = formatTime(message.timestamp)
            if (nextMessage == null) {
                binding.layoutStatus.isVisible = true
                binding.tvTime.isVisible = true
                binding.tvMessage.updatePadding(10f.dp, 10f.dp, 10f.dp, 4f.dp)
            } else {
                if (nextMessage.senderId == currentUserId) {
                    // message sent
                    binding.layoutStatus.isVisible = false
                    binding.tvTime.isVisible = false
                    binding.tvMessage.updatePadding(10f.dp, 10f.dp, 10f.dp, 10f.dp)
                } else {
                    // message received
                    binding.layoutStatus.isVisible = false
                    binding.tvTime.isVisible = true
                    binding.tvMessage.updatePadding(10f.dp, 10f.dp, 10f.dp, 4f.dp)
                }
            }
            if (lastMessage == null) {
                binding.tvDateTime.isVisible = true
                binding.tvDateTime.text = formatDateTime(message.timestamp, HH_MM_DD_MM_YYYY)
            } else {
                if (isDifferentDay(message.timestamp, lastMessage.timestamp)) {
                    // different day
                    binding.tvDateTime.isVisible = true
                    binding.tvDateTime.text = formatDateTime(message.timestamp, HH_MM_DD_MM_YYYY)
                } else {
                    // same day
                    binding.tvDateTime.isVisible = false
                }
            }
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, lastMessage: Message?, nextMessage: Message?) {
            binding.tvMessage.text = message.content
            binding.tvTime.text = formatTime(message.timestamp)
            if (nextMessage == null) {
                binding.tvTime.isVisible = true
                binding.tvMessage.updatePadding(10f.dp, 10f.dp, 10f.dp, 4f.dp)
                binding.container.updateLayoutParams<RecyclerView.LayoutParams> {
                    setMargins(0, 0, 0, 30F.dp)
                }
            } else {
                if (nextMessage.senderId != currentUserId) {
                    // message sent
                    binding.tvTime.isVisible = false
                    binding.tvMessage.updatePadding(10f.dp, 10f.dp, 10f.dp, 10f.dp)
                } else {
                    // message received
                    binding.tvTime.isVisible = true
                    binding.tvMessage.updatePadding(10f.dp, 10f.dp, 10f.dp, 4f.dp)
                }
            }
            if (lastMessage != null) {
                if (lastMessage.senderId != currentUserId) {
                    binding.cardAvatar.visibility = View.INVISIBLE
                } else {
                    binding.cardAvatar.isVisible = true
                }
                if (isDifferentDay(message.timestamp, lastMessage.timestamp)) {
                    // different day
                    binding.tvDateTime.isVisible = true
                    binding.tvDateTime.text = formatDateTime(message.timestamp, HH_MM_DD_MM_YYYY)
                } else {
                    // same day
                    binding.tvDateTime.isVisible = false
                }
            } else {
                binding.tvDateTime.isVisible = true
                binding.tvDateTime.text = formatDateTime(message.timestamp, HH_MM_DD_MM_YYYY)
            }
        }
    }

}