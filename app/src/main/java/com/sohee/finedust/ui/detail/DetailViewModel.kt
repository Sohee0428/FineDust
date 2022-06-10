package com.sohee.finedust.ui.detail

import androidx.lifecycle.ViewModel
import com.sohee.finedust.data.DetailIntentData
import kotlinx.coroutines.flow.MutableStateFlow

class DetailViewModel : ViewModel() {
    val detailDustData = MutableStateFlow<DetailIntentData?>(null)
}