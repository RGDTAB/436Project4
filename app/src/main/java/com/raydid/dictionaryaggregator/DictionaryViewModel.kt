package com.raydid.dictionaryaggregator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DictionaryViewModel: ViewModel() {
    val wordOfTheDay = MutableLiveData<String>()
    val definitionOfTheDay = MutableLiveData<String>()
    val searchResults = MutableLiveData<org.json.JSONArray>()
    val searchWord = MutableLiveData<String>()
}