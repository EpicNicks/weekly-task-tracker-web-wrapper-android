package com.aspirant.weeklytasktrackerwebwrapper.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.lifecycle.ViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.aspirant.weeklytasktrackerwebwrapper.model.auth.AuthService
import com.aspirant.weeklytasktrackerwebwrapper.view.common.AnimatedLinearGradientBackground
import com.aspirant.weeklytasktrackerwebwrapper.model.entity.response.ApiResponse
import com.aspirant.weeklytasktrackerwebwrapper.view.common.OutlinedTextFieldBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(viewModel: RegisterViewModel) {

    val (focusRequester) = FocusRequester.createRefs()
    val isLoading by rememberUpdatedState(newValue = viewModel.isLoading)
    val errorMsg by rememberUpdatedState(newValue = viewModel.errorMsg)

    AnimatedLinearGradientBackground(
        colors = listOf(
            Color(0xFF579ED1), // #579ed1
            Color(0xFF1FC8DB), // #1fc8db
            // third color breaks it because it lerps from first to last
//            Color(0xFF579ED1)  // #579ed1
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Adjust padding as needed
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1F73AE), shape = RoundedCornerShape(4.dp))
                        .fillMaxWidth()
                        .height(100.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "FreeTime",
                        color = Color.White,
                        fontSize = 42.sp,
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
                OutlinedTextFieldBackground(color = Color.White) {
                    OutlinedTextField(
                        value = viewModel.username,
                        onValueChange = { username -> viewModel.updateUsername(username) },
                        label = { Text("Username") },
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .fillMaxWidth()
                    )
                }
                OutlinedTextFieldBackground(color = Color.White) {
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { password ->
                            viewModel.updatePassword(password)
                        },
                        label = { Text("Password") },
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester.requestFocus() }
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .fillMaxWidth()
                    )
                }
                OutlinedTextFieldBackground(color = Color.White) {
                    OutlinedTextField(
                        value = viewModel.reenteredPassword,
                        onValueChange = { password ->
                            viewModel.updateReenteredPassword(password)
                        },
                        label = { Text("Password") },
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { viewModel.register() }
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .fillMaxWidth()
                    )
                }
                Text(errorMsg, color = Color.Red)
                Button(
                    onClick = {
                        if (viewModel.password != viewModel.reenteredPassword){
                            viewModel.setErrorMessage("Passwords do not match")
                        }
                        else {
                            viewModel.register()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xFF1F73AE),
                        containerColor = Color.White,
                        disabledContentColor = Color(0x901F73AE),
                        disabledContainerColor = Color.Gray,
                    ),
                    enabled = !isLoading,
                    modifier = Modifier
                        .border(width = 1.5.dp, color = Color(0xFF1F73AE), shape = RoundedCornerShape(32.dp))
                        .fillMaxWidth()
                ) {
                    Text("Register")
                }
                Button(
                    onClick = { viewModel.login() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Login")
                }
            }
        }
    }
}


class RegisterViewModel(
    private val authService: AuthService,
    private val onNavigateToLogin: () -> Unit
) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var errorMsg by mutableStateOf("")
        private set
    var username by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var reenteredPassword by mutableStateOf("")
        private set

    fun updateUsername(input: String) { username = input }

    fun updatePassword(input: String) { password = input }

    fun updateReenteredPassword(input: String) { reenteredPassword = input }
    fun setErrorMessage(message: String) { errorMsg = message }

    fun login() {
        onNavigateToLogin()
    }

    fun register() {
        viewModelScope.launch {
            errorMsg = ""
            isLoading = true
            authService.register(username, password, onRegisterResponse = { registerResponse ->
                val registerResponseTag = "login Response"
                if (registerResponse != null) {
                    when (registerResponse) {
                        is ApiResponse.Success -> {
                            Log.i(registerResponseTag, "token: $registerResponse")
                            onNavigateToLogin()
                        }

                        is ApiResponse.Failure -> {
                            Log.e(registerResponseTag, "error: ${registerResponse.error}")
                            errorMsg = "The username was taken"
                        }
                    }
                } else {
                    Log.e(registerResponseTag, "response was null")
                }
                isLoading = false
            })
        }
    }
}