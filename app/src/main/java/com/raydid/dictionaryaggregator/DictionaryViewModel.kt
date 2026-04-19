package com.raydid.dictionaryaggregator


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray


class DictionaryViewModel: ViewModel() {
    val wordOfTheDay = MutableLiveData<String>()
    val definitionOfTheDay = MutableLiveData<String>()
    val searchResults = MutableLiveData<JSONArray>()
    val searchWord = MutableLiveData<String>()




    val audioUrl = MutableLiveData<String>()
    val currentFilter = MutableLiveData("All")
    val phonetic = MutableLiveData<String>()
}
