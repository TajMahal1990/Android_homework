package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding

import ru.netology.nmedia.utils.StringProperty
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        arguments?.textArg?.let {
            binding.editText.setText(it)
        }
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
        if (binding.editText.text.isNullOrBlank()) {
            binding.editText.setText(viewModel.edited.value?.content.toString())
        }
        binding.editText.requestFocus()
        binding.btnOk.setOnClickListener {
            val content = binding.editText.text.toString()
            if (content.isNotBlank()) {
                viewModel.changeContent(content)
                viewModel.save()
            //    AndroidUtils.hideKeyboard(requireView())
            } else {
                viewModel.clear()
                binding.editText.clearFocus()
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.edited.value?.id == 0L) {
                        val content = binding.editText.text.toString()
                        viewModel.changeContent(content)
                    } else {
                        viewModel.clear()
                    }
                    findNavController().navigateUp()
                }
            }
        )

        return binding.root
    }

    companion object {
        var Bundle.textArg by StringProperty
    }
}