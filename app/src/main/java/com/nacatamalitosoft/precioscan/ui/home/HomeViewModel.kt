package com.nacatamalitosoft.precioscan.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _store = MutableLiveData<String>()
    val store: LiveData<String> = _store;

    fun setStore(storeName: String){
        _store.value = storeName;
    }
}