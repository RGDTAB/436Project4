package com.raydid.dictionaryaggregator


import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.raydid.dictionaryaggregator.databinding.FragmentSecondBinding
import org.json.JSONArray




class SecondFragment : Fragment() {
    private lateinit var viewModel: DictionaryViewModel
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)


        // Setup for Filter Spinner
        val filters = arrayOf("All", "Noun", "Verb", "Adjective", "Adverb")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filters)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterSpinner?.adapter = adapter


        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[DictionaryViewModel::class.java]


        // restore search word and results on rotation
        viewModel.searchWord.observe(viewLifecycleOwner) { word ->
            binding.tvSearchWord.text = word
        }


        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            results?.let {
                updateRecyclerView(it, binding.filterSpinner?.selectedItem.toString())
            }
        }

        viewModel.audioUrl.observe(viewLifecycleOwner) { url ->
            binding.btnPlayAudio?.visibility = if (url.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.phonetic.observe(viewLifecycleOwner) { phonetic ->
            binding.tvPhonetic?.text = phonetic
        }


        binding.btnPlayAudio?.setOnClickListener {
            viewModel.audioUrl.value?.let { url -> playAudio(url) }
        }


        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.filterSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{


            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long){



                val selectedFilter = parent.getItemAtPosition(pos).toString()


                viewModel.currentFilter.value = selectedFilter


                viewModel.searchResults.value?.let { results ->
                    updateRecyclerView(results, selectedFilter)


                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }


        binding.etSearchSecond.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {


                val word = binding.etSearchSecond.text.toString().trim()


                when {


                    word.isEmpty() -> binding.etSearchSecond.error = getString(R.string.error_empty)


                    !word.all { it.isLetter() } -> binding.etSearchSecond.error = getString(R.string.error_letters)


                    else -> fetchDefinition(word)
                }
                true
            } else {
                false
            }
        }
    }


    override fun onStart() {
        super.onStart()


        // only fetch from arguments if we don't already have data saved
        if (viewModel.searchResults.value == null) {
            arguments?.let {
                val args = SecondFragmentArgs.fromBundle(it)


                fetchDefinition(args.searchQuery)
            }
        }
    }
    private fun updateRecyclerView(results: JSONArray, filter: String) {


        binding.recyclerView.visibility = View.VISIBLE
        binding.recyclerView.adapter = DefinitionAdapter(results, filter)
    }


    fun fetchDefinition(word : String) {
        binding.tvSearchWord.text = word
        val queue = Volley.newRequestQueue(requireContext())
        val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"


        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                val firstEntry = response.getJSONObject(0)
                val meanings = firstEntry.getJSONArray("meanings")


                // Extract Audio url for button
                val phonetics = firstEntry.optJSONArray("phonetics")
                val phoneticText = firstEntry.optString("phonetic", "")
                viewModel.phonetic.value = phoneticText
                var audioFound = ""


                phonetics?.let {
                    for (i in 0 until it.length()) {
                        val p = it.getJSONObject(i)
                        val audioUrl = p.optString("audio")
                        if (audioUrl.isNotEmpty()) {
                            audioFound = audioUrl
                            break
                        }
                    }
                }


                viewModel.searchWord.value = word
                viewModel.searchResults.value = meanings
                viewModel.audioUrl.value = audioFound
                binding.btnPlayAudio?.visibility = if (audioFound.isEmpty()) View.GONE else View.VISIBLE
            },
            //if unsuccessful return error messages
            {


                binding.tvSearchWord.text = getString(R.string.not_found, word)
                binding.recyclerView.visibility = View.GONE
                binding.btnPlayAudio?.visibility = View.GONE
            }
        )
        //trigger volley to send a network call
        queue.add(request)
    }


    private fun playAudio(url: String) {


        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build())


            setDataSource(url)
            prepareAsync()


            setOnPreparedListener { start() }
        }
    }




    override fun onDestroyView(){


        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }


}




