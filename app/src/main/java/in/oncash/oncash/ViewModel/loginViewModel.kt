package `in`.oncash.oncash.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.oncash.oncash.Component.get_UserInfo_UseCase
import `in`.oncash.oncash.DataType.UserData1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class loginViewModel:ViewModel() {
    private  var userData:MutableLiveData<Boolean> = MutableLiveData()

    fun addUser(userNumber : Long  , referred_code : Int) {

        viewModelScope.launch {
            withContext(Dispatchers.Main) {

                userData.value = get_UserInfo_UseCase().loginManager(userNumber , referred_code )

            }
        }
    }

    fun getUserData1(): MutableLiveData<Boolean>{
        return userData
    }



}