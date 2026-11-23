package com.example.maklucut.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation3.runtime.NavKey

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("maklu_prefs", Context.MODE_PRIVATE) }
    val defaultUrl = "http://10.0.2.2:8765" // standard emulator loopback, or LAN IP for physical device

    var serverUrl by remember {
        mutableStateOf(prefs.getString("server_url", defaultUrl) ?: defaultUrl)
    }
    var showDialog by remember { mutableStateOf(false) }
    var inputUrl by remember { mutableStateOf(serverUrl) }
    var isConnected by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    var filePathCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }
    val fileChooserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val results = WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data)
        filePathCallback?.onReceiveValue(results)
        filePathCallback = null
    }

    BackHandler(enabled = webViewRef?.canGoBack() == true) {
        webViewRef?.goBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF161618))
            .statusBarsPadding()
    ) {
        // --- Top Brand Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF1E1E22))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Connection indicator dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isConnected) Color(0xFF4CAF50) else Color(0xFFE53935))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "MAKLU",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFE6230),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "CUT",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    isConnected = true
                    errorMessage = null
                    webViewRef?.reload()
                }) {
                    Text("↻", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = {
                    inputUrl = serverUrl
                    showDialog = true
                }) {
                    Text("⚙", fontSize = 22.sp, color = Color(0xFFFE6230), fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFE6230),
                trackColor = Color(0xFF2C2C30)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewRef = this
                        setBackgroundColor(0xFF161618.toInt())

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            allowFileAccess = true
                            allowContentAccess = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            mediaPlaybackRequiresUserGesture = false
                            builtInZoomControls = false
                            displayZoomControls = false
                            userAgentString = "$userAgentString MakluCutMobile/1.0"
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                isConnected = true
                                errorMessage = null
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                if (request?.isForMainFrame == true) {
                                    isLoading = false
                                    isConnected = false
                                    errorMessage = error?.description?.toString() ?: "Connection failed"
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                isLoading = newProgress < 100
                            }

                            override fun onShowFileChooser(
                                wv: WebView?,
                                callback: ValueCallback<Array<Uri>>?,
                                params: FileChooserParams?
                            ): Boolean {
                                filePathCallback?.onReceiveValue(null)
                                filePathCallback = callback
                                val intent = params?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                                    type = "video/*"
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                }
                                try {
                                    fileChooserLauncher.launch(intent)
                                } catch (e: Exception) {
                                    filePathCallback = null
                                    return false
                                }
                                return true
                            }
                        }

                        loadUrl(serverUrl)
                    }
                },
                update = { view ->
                    if (view.url != serverUrl && isConnected) {
                        view.loadUrl(serverUrl)
                    }
                }
            )

            // Connection Error / Setup Overlay when PC server is unreachable
            if (!isConnected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF161618))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF222226)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "MAKLU CUT সার্ভারে সংযোগ করা যায়নি",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "১. আপনার কম্পিউটারে start.bat চালু রাখুন।\n" +
                                        "২. আপনার ফোন ও পিসি একই Wi-Fi নেটওয়ার্কে যুক্ত করুন।\n" +
                                        "৩. কম্পিউটারের লোকাল IP অ্যাড্রেস লিখুন।\n\n" +
                                        "বর্তমান ঠিকানা: $serverUrl",
                                fontSize = 14.sp,
                                color = Color(0xFFB0B0B8),
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        inputUrl = serverUrl
                                        showDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF33333A)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("IP বদলান", color = Color.White)
                                }
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        isConnected = true
                                        errorMessage = null
                                        webViewRef?.loadUrl(serverUrl)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE6230)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("আবার চেষ্টা", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Server URL Dialog ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "MAKLU CUT Server IP",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "পিসির IP ও পোর্ট লিখুন (যেমন: http://192.168.1.5:8765 বা http://10.0.2.2:8765):",
                        fontSize = 13.sp,
                        color = Color(0xFFB0B0B8)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFE6230),
                            unfocusedBorderColor = Color(0xFF44444C),
                            cursorColor = Color(0xFFFE6230)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        var clean = inputUrl.trim()
                        if (!clean.startsWith("http://") && !clean.startsWith("https://")) {
                            clean = "http://$clean"
                        }
                        if (!clean.contains(":") || clean.endsWith(".com") || clean.endsWith(".org")) {
                            if (!clean.contains(":8765")) {
                                clean = "$clean:8765"
                            }
                        }
                        serverUrl = clean
                        prefs.edit().putString("server_url", clean).apply()
                        showDialog = false
                        isConnected = true
                        errorMessage = null
                        webViewRef?.loadUrl(clean)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE6230))
                ) {
                    Text("সংযুক্ত করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("বাতিল", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF222226),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
