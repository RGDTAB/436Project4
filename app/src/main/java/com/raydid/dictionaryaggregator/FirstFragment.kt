package com.raydid.dictionaryaggregator


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo


import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.raydid.dictionaryaggregator.databinding.FragmentFirstBinding
import com.android.volley.toolbox.JsonObjectRequest








class FirstFragment : Fragment() {
    private lateinit var viewModel: DictionaryViewModel
    private var _binding : FragmentFirstBinding? = null
    private val binding get() = _binding!!






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
// Inflate the layout for this fragment
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root




    }//end onCreateView




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(requireActivity())[DictionaryViewModel::class.java]




        binding.etSearchFirst.setOnEditorActionListener { _, actionId, _ ->


            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val userInput = binding.etSearchFirst.text.toString()
                //make sure only letters can get searched
                when {
                    userInput.isEmpty() -> {
                        binding.etSearchFirst.error = "Please enter a word"
                    }


                    !userInput.all { it.isLetter() } -> {
                        binding.etSearchFirst.error = "Only letters allowed"
                    }


                    else -> {
                        // Clear previous results before navigating
                        viewModel.searchResults.value = null
                        viewModel.searchWord.value = null
                        viewModel.audioUrl.value = null
                        viewModel.phonetic.value = null
                        val action = FirstFragmentDirections.mainToSecond(userInput)
                        findNavController().navigate(action)
                    }
                }
                true
            } else {
                false
            }
        }


        //save state of word and definition of the day so it doesn't have to reload the api
        viewModel.wordOfTheDay.observe(viewLifecycleOwner) { word ->
            binding.tvWord.text = word
        }


        viewModel.definitionOfTheDay.observe(viewLifecycleOwner) { meaning ->
            binding.tvDefinition.text = meaning
        }


        //call word of day if not saved in viewmodel yet
        if (viewModel.wordOfTheDay.value == null) {
            wordOfTheDay()
        }
    }


    // show the last searched word in the bar after coming back from second fragment
    override fun onResume() {
        super.onResume()
        viewModel.searchWord.value?.let { word ->
            binding.etSearchFirst.setText(word)
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    private fun wordOfTheDay() {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "https://wordoftheday.freeapi.me/"


        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->


                //if request succeeds get the word, meaning, and part of speech
                val word = response.getString("word")
                val meaning = response.getString("meaning")
                val partOfSpeech = response.getString("partOfSpeech")


                viewModel.wordOfTheDay.value = word
                viewModel.definitionOfTheDay.value = "($partOfSpeech) $meaning"


                binding.tvWord.text = word


                val formattedWotd = getString(R.string.wotd_format, partOfSpeech, meaning)
                binding.tvDefinition.text = formattedWotd
            },


            // if unsuccessful return error messages
            { _ ->
                binding.tvWord.text = getString(R.string.unavailable)
                binding.tvDefinition.text = getString(R.string.load_error)
            }
        )
        // trigger volley to send a network call
        queue.add(request)
    }


}
