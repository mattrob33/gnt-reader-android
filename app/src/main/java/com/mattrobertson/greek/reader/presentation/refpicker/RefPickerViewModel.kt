package com.mattrobertson.greek.reader.presentation.refpicker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mattrobertson.greek.reader.model.Book

class RefPickerViewModel : ViewModel() {

    val book: MutableLiveData<Book> = MutableLiveData(null)
    val chapter = MutableLiveData(0)

}