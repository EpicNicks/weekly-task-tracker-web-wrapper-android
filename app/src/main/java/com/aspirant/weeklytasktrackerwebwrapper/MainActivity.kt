package com.aspirant.weeklytasktrackerwebwrapper

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aspirant.weeklytasktrackerwebwrapper.model.auth.SharedPreferencesAuthService
import com.aspirant.weeklytasktrackerwebwrapper.ui.theme.WeeklyTaskTrackerWebWrapperTheme
import com.aspirant.weeklytasktrackerwebwrapper.view.LoginScreen
import com.aspirant.weeklytasktrackerwebwrapper.view.LoginViewModel
import com.aspirant.weeklytasktrackerwebwrapper.view.RegisterScreen
import com.aspirant.weeklytasktrackerwebwrapper.view.RegisterViewModel


@SuppressLint("SetJavaScriptEnabled")
class MainActivity : ComponentActivity() {
    private val webView: WebView by lazy {
        WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            webViewClient = WebViewClient()
            loadUrl("https://freetime.49385219.xyz/")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeeklyTaskTrackerWebWrapperTheme {
                val authService = remember { SharedPreferencesAuthService(this) }
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    App(webView = webView, authService = authService)
                }
            }
        }
    }
}


@Composable
fun App(webView: WebView, authService: SharedPreferencesAuthService) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = LoginViewModel(
                    onNavigateToTaskTracker = { navController.navigate("taskTrackerToday") },
                    onNavigateToRegister = { navController.navigate("register") },
                    authService = authService
                ),
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = RegisterViewModel(
                    onNavigateToLogin = { navController.navigate("login") },
                    authService = authService,
                )
            )
        }
        composable("taskTrackerToday") {
            AndroidView(
                factory = {
                    // Remove WebView from previous parent if any
                    val parent = webView.parent as? ViewGroup
                    parent?.removeView(webView)
                    webView
                },
                modifier = Modifier.fillMaxSize(),
            ) { androidView ->
                // Handle page finished event to execute JavaScript code
                androidView.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        webView.evaluateJavascript(
                            "sessionStorage.setItem('userToken', '${authService.getAuthToken()}')",
                            null
                        )
                    }
                }
            }
            BackHandler {
                if (webView.canGoBack()) {
                    webView.goBack()
                }
            }
        }
    }
}

