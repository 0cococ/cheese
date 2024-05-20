package coco.cheese.core.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    val textList = mutableStateListOf<String>()
}
val textList = ProfileViewModel().textList
