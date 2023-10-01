package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {
    private var postId: Long = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
        val binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        arguments?.textArg?.let {
            postId = it.toLong()
        }

        val postViewHolder = PostViewHolder(binding.post, object : OnInteractionListener {

            override fun onLike(post: Post) {
                viewModel.likeById(post)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onRunVideo(post: Post) {
                val videoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(post.linkVideo))
                startActivity(videoIntent)
            }

            override fun onViews(post: Post) {
         //       viewModel.viewById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_postFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    }
                )
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.localId)
                findNavController().navigateUp()
            }

            override fun onSend(post: Post) {
                viewModel.send(post)
            }
        })

        viewModel.data.observe(viewLifecycleOwner) { state ->
            val post: Post? = state.posts.find { it.id == postId }
            if (post != null) {
                postViewHolder.bind(post)
            }
        }

        return binding.root
    }
}