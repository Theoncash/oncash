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
    private  var userData:MutableLiveData<UserData1> = MutableLiveData()

    fun addUser(userNumber : Long ) {

        viewModelScope.launch {
            withContext(Dispatchers.Main) {

                userData.value = get_UserInfo_UseCase().loginManager(userNumber)

            }
        }
    }

    fun getUserData1(): MutableLiveData<UserData1>{
        return userData
    }



}