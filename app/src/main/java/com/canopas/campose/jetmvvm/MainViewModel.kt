package com.canopas.campose.jetmvvm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {

    val userState = MutableStateFlow<UserState>(UserState.StartState)

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            userState.tryEmit(UserState.LoadingState)
            withContext(Dispatchers.IO) {
                try {
                    val user = userService.fetchUser()
                    userState.tryEmit(UserState.Success(user))
                } catch (e: Exception) {
                    userState.tryEmit(UserState.Error(e.localizedMessage))
                }
            }
        }
    }
}

sealed class UserState {
    object StartState : UserState()
    object LoadingState : UserState()
    data class Success(val users: List<User>) : UserState()
    data class Error(val errorMessage: String) : UserState()
}