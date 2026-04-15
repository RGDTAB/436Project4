package com.raydid.dictionaryaggregator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.raydid.dictionaryaggregator.databinding.FragmentFirstBinding
import com.android.volley.toolbox.JsonObjectRequest


class FirstFragment : Fragment() {
    private var _binding : FragmentFirstBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    } // onCreate


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
// Inflate the layout for this fragment
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }//end onCreateView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etSearchFirst.setOnEditorActionListener { v, actionId, event -> Boolean
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val userInput = binding.etSearchFirst.text.toString()
                val action = FirstFragmentDirections.mainToSecond(userInput)
                Navigation.findNavController(binding.etSearchFirst).navigate(action)
                true
            } else {
                false
            }
        }

        WordOfTheDay()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun WordOfTheDay() {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "https://wordoftheday.freeapi.me/"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                //if request succeeds get the word, meaning, and part of speech
                val word = response.getString("word")
                val meaning = response.getString("meaning")
                val partOfSpeech = response.getString("partOfSpeech")

                binding.tvWord.text = word
                binding.tvDefinition.text = "($partOfSpeech) $meaning"
            },

            // if unsuccessful return error messages
            { error ->
                binding.tvWord.text = "Unavailable"
                binding.tvDefinition.text = "Could not load word of the day."
            }
        )
        // trigger volley to send a network call
        queue.add(request)
    }

}
