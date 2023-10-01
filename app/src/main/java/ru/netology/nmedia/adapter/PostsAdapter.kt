package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.clicksCount

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onViews(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onRunVideo(post: Post) {}
    fun onViewPost(post: Post) {}
    fun onSend(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            likesButton.isChecked = post.likedByMe
            likesButton.text = clicksCount(post.likes)
            shareButton.text = clicksCount(post.shareCount)
            viewsButton.text = clicksCount(post.viewsCount)
            if (post.linkVideo != null) {
                groupVideo.visibility = View.VISIBLE
            } else {
                groupVideo.visibility = View.GONE
            }
            if (post.unposted == 1) {
               send.visibility = View.VISIBLE
            } else {
                send.visibility = View.GONE
            }

            val urlAvatar = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
            Glide.with(binding.avatar)
                .load(urlAvatar)
                .circleCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .timeout(10_000)
                .into(binding.avatar)

            if (post.attachment != null) {
                val url = "http://10.0.2.2:9999/images/${post.attachment.url}"
                Glide.with(binding.Attachment)
                    .load(url)
                    .timeout(10_000)
                    .into(binding.Attachment)
                Attachment.visibility = View.VISIBLE
            } else {
                Attachment.visibility = View.GONE
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.option_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            likesButton.setOnClickListener {
                likesButton.isChecked = !likesButton.isChecked
                onInteractionListener.onLike(post)
            }
            shareButton.setOnClickListener { onInteractionListener.onShare(post) }
            viewsButton.setOnClickListener { onInteractionListener.onViews(post) }
            groupVideo.setAllOnClickListener { onInteractionListener.onRunVideo(post) }
            root.setOnClickListener { onInteractionListener.onViewPost(post) }
            send.setOnClickListener { onInteractionListener.onSend(post) }
        }
    }
}

fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}

class PostDiffCallBack : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}