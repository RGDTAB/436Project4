package com.raydid.dictionaryaggregator

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.raydid.dictionaryaggregator.databinding.FragmentFirstBinding
import com.raydid.dictionaryaggregator.databinding.FragmentSecondBinding


class SecondFragment : Fragment() {
    private var _binding : FragmentSecondBinding? = null
    private val binding get() = _binding!!

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSecondBinding.inflate(inflater)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.etSearchSecond.setOnEditorActionListener { v, actionId, event -> Boolean
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val word = binding.etSearchSecond.text.toString()
                fetchDefinition(word)
                true
            } else {
                false
            }
        }
    }

    override fun onStart() {
        super.onStart()

        arguments?.let {
            val args = SecondFragmentArgs.fromBundle(it)
            val word = args.searchQuery
            binding.etSearchSecond.setText(word)
            fetchDefinition(word)
        }
    }

    fun fetchDefinition(word : String) {
        binding.tvSearchWord.text = word
        val queue = Volley.newRequestQueue(requireContext())
        val url = " https://api.dictionaryapi.dev/api/v2/entries/en/$word"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val meanings = response.getJSONObject(0).getJSONArray("meanings")
                val definitionAdapter = DefinitionAdapter(meanings)

                binding.recyclerView.visibility = View.VISIBLE
                binding.recyclerView.adapter = definitionAdapter
            },

            // if unsuccessful return error messages
            { error ->
                val errorMsg = "$word: $error"
                binding.tvSearchWord.text = errorMsg
                binding.recyclerView.visibility = View.GONE
            }
        )
        // trigger volley to send a network call
        queue.add(request)
    }

}

