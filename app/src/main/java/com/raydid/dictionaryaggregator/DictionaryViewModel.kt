package com.raydid.dictionaryaggregator

import androidx.lifecycle.MutableLiveData

class DictionaryViewModel {
    val wordOfTheDay = MutableLiveData<String>()
    val definitionOfTheDay = MutableLiveData<String>()
    val searchResults = MutableLiveData<org.json.JSONArray>()
    val searchWord = MutableLiveData<String>()
}