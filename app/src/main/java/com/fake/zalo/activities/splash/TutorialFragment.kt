package com.fake.zalo.activities.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.fake.zalo.R
import com.fake.zalo.databinding.FragmentTutorialBinding

class TutorialFragment : Fragment() {

    private var _binding: FragmentTutorialBinding? = null
    private val binding: FragmentTutorialBinding get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTutorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = arguments?.getInt("position", -1) ?: -1
        if (position in 0..3) {
            binding.tvFinal.isVisible = false
            binding.ivIconSplash.isVisible = true
            binding.tvTitle.isVisible = true
            binding.tvDescription.isVisible = true

            when (position) {
                0 -> {
                    binding.ivIconSplash.setImageResource(R.drawable.splash_video_call)
                    binding.tvTitle.text = "Gọi video ổn định"
                    binding.tvDescription.text = "Trò chuyện thật đã với chất lượng video ổn định mọi lúc, mọi nơi"
                }

                1 -> {
                    binding.ivIconSplash.setImageResource(R.drawable.splash_chat_group)
                    binding.tvTitle.text = "Chat nhóm tiện lợi"
                    binding.tvDescription.text =
                        "Nơi cùng nhau trao đổi, giữ liên lạc với gia đình, bạn bè, đồng nghiệp..."
                }

                2 -> {
                    binding.ivIconSplash.setImageResource(R.drawable.splash_send_image)
                    binding.tvTitle.text = "Gửi ảnh nhanh chóng"
                    binding.tvDescription.text =
                        "Trao đổi hình ảnh chất lượng cao với bạn bè và người thân thật nhanh và dễ dàng"
                }

                3 -> {
                    binding.ivIconSplash.setImageResource(R.drawable.splash_friends)
                    binding.tvTitle.text = "Nhật ký bạn bè"
                    binding.tvDescription.text = "Nơi cập nhật hoạt động mới nhất của những người bạn quan tâm!"
                }
            }
        } else {
            binding.tvFinal.isVisible = true
            binding.ivIconSplash.isVisible = false
            binding.tvTitle.isVisible = false
            binding.tvDescription.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}