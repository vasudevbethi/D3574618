package uk.ac.tees.mad.d3574618.auth

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.data.domain.LoginStatus
import uk.ac.tees.mad.d3574618.data.domain.RegisterState
import uk.ac.tees.mad.d3574618.showToast
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination

object AuthDestination : NavigationDestination {
    override val route: String
        get() = "auth"
    override val titleRes: Int
        get() = R.string.auth

}

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    registerSuccess: () -> Unit = {},
    onForgetPassword: () -> Unit,
    loginSuccess: () -> Unit
) {
    val isLoginSelected = rememberSaveable {
        mutableStateOf(true)
    }
    val currentUserStatus = viewModel.currentUserStatus.collectAsState(initial = null)
    val signInState = viewModel.state.collectAsState().value
    val signInStatus = viewModel.signInState.collectAsState(initial = null)
    val signUpstate = viewModel.signUpState.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            oneTapClient = Identity.getSignInClient(context)
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                scope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    viewModel.onSignInWithGoogleResult(signInResult)
                }
            }
        }
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        AuthSelectBox(
            isLoginSelected = isLoginSelected.value, onLoginClick = {
                isLoginSelected.value = true
            }, onSignupClick = {
                isLoginSelected.value = false
            }
        )

        if (isLoginSelected.value) {
            LoginForm(viewModel, state = signInStatus)
        } else {
            SignUpForm(viewModel, state = signUpstate)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center

        ) {
            Text(
                text = "Forgot Password? Reset here.", modifier = Modifier.clickable {
                    onForgetPassword()
                },
                textDecoration = TextDecoration.Underline
            )
        }


        Spacer(modifier = Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color.Gray.copy(alpha = 0.5f))
            )
            Text(
                text = "OR",
                modifier = Modifier.padding(12.dp),
                fontSize = 20.sp,
                color = Color.Gray.copy(alpha = 0.7f)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color.Gray.copy(alpha = 0.5f))
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = {
                scope.launch {
                    val signInIntentSender = googleAuthUiClient.signIn()
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )

                }
            }, modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Box(Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp), verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = Color.Unspecified
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "Continue with Google", fontSize = 18.sp, color = Color.Black)
                }
            }

        }
        Spacer(modifier = Modifier.height(18.dp))
        var isGoogleSigned by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(currentUserStatus.value?.isSuccess) {
            if (currentUserStatus.value?.isSuccess != null && isGoogleSigned) {
                loginSuccess()
                Log.d("USER", currentUserStatus.value!!.isSuccess.toString())
            }
        }

        LaunchedEffect(currentUserStatus.value?.isError) {
            if (currentUserStatus.value?.isError != null && isGoogleSigned) {
                val user = googleAuthUiClient.getSignedInUser()
                context.showToast("${currentUserStatus.value?.isError}")
                if (user != null) {
                    viewModel.saveUserInFirestore(user)
                }
                registerSuccess()
            }
        }

        LaunchedEffect(key1 = signInState.isSignInSuccessful) {
            if (signInState.isSignInSuccessful) {
                Toast.makeText(
                    context,
                    "Sign in successful",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.getUserDetails()
                isGoogleSigned = true
            }
        }

        LaunchedEffect(key1 = signInStatus.value?.isSuccess) {
            scope.launch {
                if (signInStatus.value?.isSuccess?.isNotEmpty() == true) {
                    focusManager.clearFocus()
                    val success = signInStatus.value?.isSuccess
                    Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                    currentUserStatus.value?.isSuccess.let {

                        if (it?.item?.phone?.isEmpty() == true) {
                            registerSuccess()
                        } else {
                            loginSuccess()
                        }
                    }
                }
            }
        }

        LaunchedEffect(key1 = signInStatus.value?.isError) {
            scope.launch {
                if (signInStatus.value?.isError?.isNotEmpty() == true) {
                    val error = signInStatus.value?.isError
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            }
        }
        LaunchedEffect(key1 = signInState.signInError) {
            scope.launch {
                if (signInState.signInError?.isNotEmpty() == true) {
                    val error = signInState.signInError
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            }
        }
        LaunchedEffect(key1 = signUpstate.value?.isSuccess) {
            scope.launch {
                if (signUpstate.value?.isSuccess?.isNotEmpty() == true) {
                    registerSuccess()
                }
            }
        }

        LaunchedEffect(key1 = signUpstate.value?.isError) {
            scope.launch {
                if (signUpstate.value?.isError?.isNotEmpty() == true) {
                    val error = signUpstate.value?.isError
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

@Composable
fun SignUpForm(viewModel: AuthViewModel, state: State<RegisterState?>) {
    val signUpUiState = viewModel.signUpUiState.collectAsState().value
    val focusManager = LocalFocusManager.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = signUpUiState.name,
            onValueChange = {
                viewModel.updateSignUpState(signUpUiState.copy(name = it))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Full Name")
            },
            maxLines = 1,
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
        )
        OutlinedTextField(
            value = signUpUiState.email,
            onValueChange = {
                viewModel.updateSignUpState(signUpUiState.copy(email = it))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Email")
            },
            maxLines = 1,
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
        )
        OutlinedTextField(
            value = signUpUiState.password,
            onValueChange = {
                viewModel.updateSignUpState(signUpUiState.copy(password = it))
            },

            modifier = Modifier.fillMaxWidth(),

            maxLines = 1,
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Default.Visibility
                else Icons.Filled.VisibilityOff

                val description =
                    if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        description,
                    )
                }
            },
            placeholder = {
                Text(text = "Password")
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            })
        )
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
            },

            modifier = Modifier.fillMaxWidth(),

            maxLines = 1,
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Default.Visibility
                else Icons.Filled.VisibilityOff

                val description =
                    if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        description,
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (confirmPassword == signUpUiState.password) Color.Unspecified else MaterialTheme.colorScheme.error,
            ),
            placeholder = {
                Text(text = "Confirm Password")
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            })
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = {
                viewModel.registerUser(
                    username = signUpUiState.name,
                    email = signUpUiState.email,
                    password = signUpUiState.password
                )
                println(signUpUiState)
            }, modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (state.value?.isLoading == true) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.background)
            } else {
                Text(text = "Sign up", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun LoginForm(viewModel: AuthViewModel, state: State<LoginStatus?>) {
    val loginUiState = viewModel.loginUiState.collectAsState().value
    val focusManager = LocalFocusManager.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = loginUiState.email,
            onValueChange = {
                viewModel.updateLoginState(loginUiState.copy(email = it))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Email")
            },
            maxLines = 1,
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
        )
        OutlinedTextField(
            value = loginUiState.password,
            onValueChange = {
                viewModel.updateLoginState(loginUiState.copy(password = it))
            },

            modifier = Modifier.fillMaxWidth(),

            maxLines = 1,
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Default.Visibility
                else Icons.Filled.VisibilityOff

                val description =
                    if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        description,
                    )
                }
            },
            placeholder = {
                Text(text = "Password")
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            })
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = { viewModel.loginUser(loginUiState.email, loginUiState.password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (state.value?.isLoading == true) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.background)
            } else {
                Text(text = "Log in", fontSize = 20.sp)
            }
        }
    }
}


@Composable
fun AuthSelectBox(isLoginSelected: Boolean, onLoginClick: () -> Unit, onSignupClick: () -> Unit) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onLoginClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Log in",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLoginSelected) Color.Black else Color.Gray
                )

            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .height(if (!isLoginSelected) 1.dp else 3.dp)
                    .fillMaxWidth()
                    .background(if (isLoginSelected) Color.Black else Color.Gray)
            )
        }
        Column(
            Modifier
                .weight(1f)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onSignupClick() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sign up",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isLoginSelected) Color.Black else Color.Gray
                )

            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .height(if (isLoginSelected) 1.dp else 3.dp)
                    .fillMaxWidth()
                    .background(if (!isLoginSelected) Color.Black else Color.Gray)
            )
        }

    }
}