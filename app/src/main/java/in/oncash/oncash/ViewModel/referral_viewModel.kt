package `in`.oncash.oncash.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import `in`.oncash.oncash.DataType.userData

class referral_viewModel:ViewModel() {

    val userData = MutableLiveData<userData>()
}