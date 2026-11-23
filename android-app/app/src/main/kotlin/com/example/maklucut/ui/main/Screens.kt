package com.example.maklucut.ui.main

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.maklucut.R
import com.example.maklucut.data.Clip
import com.example.maklucut.data.DownloadHelper
import com.example.maklucut.data.GeminiScout
import com.example.maklucut.data.VideoInfo
import com.example.maklucut.data.YouTubeHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    CLIP_DETAIL,
    SETTINGS
}

// -------------------------------------------------------------
// BRAND LOGO WITH COMFORTABLE GAP / PADDING
// -------------------------------------------------------------
@Composable
fun BrandLogo(
    size: Int = 40,
    padding: Int = 6,
    cornerRadius: Int = 10
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(Color(0xFF222228))
            .border(1.dp, Color(0xFF33333E), RoundedCornerShape(cornerRadius.dp))
            .padding(padding.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "MAKLU CUT Logo",
            modifier = Modifier.fillMaxSize()
        )
    }
}

// -------------------------------------------------------------
// DIRECT 100% IN-APP DOWNLOAD DIALOG (ZERO BROWSER REDIRECT)
// -------------------------------------------------------------
@Composable
fun DirectDownloadDialog(
    video: VideoInfo,
    clip: Clip,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isResolving by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun startDirectVideoDownload(quality: String) {
        scope.launch {
            isResolving = true
            errorMsg = null
            statusText = "ভিডিও স্ট্রিম লিংক প্রস্তুত করা হচ্ছে..."
            try {
                val directUrl = DownloadHelper.resolveDirectMp4Url(video.id, quality)
                DownloadHelper.enqueueDownload(context, directUrl, clip.title, clip)
                Toast.makeText(
                    context,
                    "✅ ডাউনলোড শুরু হয়েছে! ফোনের নোটিফিকেশন বার চেক করুন।",
                    Toast.LENGTH_LONG
                ).show()
                onDismiss()
            } catch (e: Exception) {
                errorMsg = e.localizedMessage ?: "ডাউনলোড সম্পন্ন করা যায়নি। ইন্টারনেট সংযোগ চেক করুন।"
                statusText = ""
            } finally {
                isResolving = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isResolving) onDismiss() },
        containerColor = Color(0xFF1E1E24),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandLogo(size = 32, padding = 4, cornerRadius = 8)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("📥 ডিরেক্ট ইন-অ্যাপ ডাউনলোড", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("কোনো বাইরের ওয়েবসাইটে যাবে না", fontSize = 11.sp, color = Color(0xFF4CAF50))
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "${clip.hook}\n⏱ টাইমস্ট্যাম্প: ${clip.start} - ${clip.end}",
                    color = Color(0xFFFFD700),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = Color(0xFF33333E))

                Text(
                    text = "🎬 ১. সরাসরি ভিডিও ডাউনলোড (MP4):",
                    color = Color(0xFF00E5FF),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                // 720p HD Direct Download
                Button(
                    onClick = { startDirectVideoDownload("720") },
                    enabled = !isResolving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    if (isResolving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(statusText, fontSize = 12.sp)
                    } else {
                        Text("⚡ সরাসরি MP4 ডাউনলোড (720p HD)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // 360p Fast Download
                OutlinedButton(
                    onClick = { startDirectVideoDownload("360") },
                    enabled = !isResolving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("⚡ দ্রুত ডাউনলোড (360p Standard)", fontSize = 12.sp)
                }

                if (errorMsg != null) {
                    Text(
                        text = "⚠️ $errorMsg",
                        color = Color(0xFFFF8A80),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = Color(0xFF33333E))

                Text(
                    text = "📄 ২. সাবটাইটেল ও স্ক্রিপ্ট ডাউনলোড:",
                    color = Color(0xFFFFB74D),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val srt = DownloadHelper.generateSrtContent(clip)
                            val ok = DownloadHelper.saveFileToDownloads(
                                context = context,
                                filename = "MAKLU_${clip.id}_subtitles.srt",
                                mimeType = "application/x-subrip",
                                content = srt
                            )
                            if (ok) {
                                Toast.makeText(context, "✅ .SRT সাবটাইটেল ডাউনলোড ফোল্ডারে সেভ হয়েছে!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "সাবটাইটেল সেভ করা যায়নি।", Toast.LENGTH_SHORT).show()
                            }
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3949AB))
                    ) {
                        Text("📄 .SRT ফাইল", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val fullScript = "${clip.hook}\n\nTitle: ${clip.title}\nTime: ${clip.start} - ${clip.end}\n\nSubtitles:\n${clip.subtitles}\n\nHashtags:\n${clip.hashtags}"
                            val ok = DownloadHelper.saveFileToDownloads(
                                context = context,
                                filename = "MAKLU_${clip.id}_script.txt",
                                mimeType = "text/plain",
                                content = fullScript
                            )
                            if (ok) {
                                Toast.makeText(context, "✅ স্ক্রিপ্ট (.TXT) ডাউনলোড ফোল্ডারে সেভ হয়েছে!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "স্ক্রিপ্ট সেভ করা যায়নি।", Toast.LENGTH_SHORT).show()
                            }
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("📝 স্ক্রিপ্ট (.TXT)", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, enabled = !isResolving) {
                Text("বন্ধ করুন", color = Color(0xFFFE6230), fontWeight = FontWeight.Bold)
            }
        }
    )
}

// -------------------------------------------------------------
// TOP BRAND BAR WITH PADDED LOGO
// -------------------------------------------------------------
@Composable
fun TopBar(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .background(Color(0xFF1E1E22))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Text("←", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            // Beautiful Logo with comfortable gap
            BrandLogo(size = 38, padding = 6, cornerRadius = 10)

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
            if (title != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "• $title",
                    fontSize = 13.sp,
                    color = Color(0xFF9E9EA4),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (onSettingsClick != null) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2A30))
            ) {
                Text("⚙", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// 1. HOME SCREEN
// -------------------------------------------------------------
@Composable
fun HomeScreen(
    apiKey: String,
    modelName: String,
    initialUrl: String = "",
    onClearInitialUrl: () -> Unit = {},
    onOpenClip: (VideoInfo, Clip) -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboard = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    var inputUrl by remember { mutableStateOf(initialUrl) }
    var isLoading by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("") }
    var activeVideo by remember { mutableStateOf<VideoInfo?>(null) }
    var clips by remember { mutableStateOf<List<Clip>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showApiKeyPromptDialog by remember { mutableStateOf(false) }

    // Direct in-app download target
    var downloadClipTarget by remember { mutableStateOf<Clip?>(null) }

    LaunchedEffect(initialUrl) {
        if (initialUrl.isNotBlank()) {
            inputUrl = initialUrl
            onClearInitialUrl()
        }
    }

    fun doScout() {
        if (apiKey.isBlank()) {
            showApiKeyPromptDialog = true
            return
        }

        if (inputUrl.isBlank()) {
            Toast.makeText(context, "অনুগ্রহ করে একটি ইউটিউব লিংক দিন", Toast.LENGTH_SHORT).show()
            return
        }
        val videoId = YouTubeHelper.extractVideoId(inputUrl)
        if (videoId == null) {
            Toast.makeText(context, "সঠিক ইউটিউব লিংক পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            return
        }

        scope.launch {
            isLoading = true
            errorMessage = null
            statusText = "১/৩: ভিডিওর টাইটেল ও অডিও স্ক্যান হচ্ছে..."
            try {
                val video = YouTubeHelper.fetchVideoDetails(videoId)
                activeVideo = video

                statusText = "২/৩: সাবটাইটেল ও ট্রান্সক্রিপ্ট সংগ্রহ হচ্ছে..."
                delay(400)

                statusText = "৩/৩: Gemini AI সেরা ভাইরাল মোমেন্ট নির্বাচন করছে..."
                val foundClips = GeminiScout.scoutClips(
                    video = video,
                    apiKey = apiKey,
                    modelName = modelName
                )
                clips = foundClips
                statusText = ""
                Toast.makeText(context, "${foundClips.size}টি ভাইরাল ক্লিপ পাওয়া গেছে!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "সমস্যা হয়েছে। ইন্টারনেট ও এপিআই কী চেক করুন।"
                statusText = ""
            } finally {
                isLoading = false
            }
        }
    }

    // Missing API Key Dialog
    if (showApiKeyPromptDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyPromptDialog = false },
            containerColor = Color(0xFF1E1E24),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔑 Gemini API Key প্রয়োজন", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "AI দিয়ে ভিডিও থেকে ভাইরাল ক্লিপ বের করতে আপনার ব্যক্তিগত Google Gemini API Key প্রয়োজন।\n\nএটি সম্পূর্ণ ফ্রি! আপনি Google AI Studio থেকে ফ্রি কী নিয়ে সেটিংসে পেস্ট করলেই অ্যাপ কাজ করবে।",
                        fontSize = 13.sp,
                        color = Color(0xFFA0A0A8),
                        lineHeight = 18.sp
                    )

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                    ) {
                        Text("🌐 ফ্রি Gemini কী নিন (Google AI Studio)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showApiKeyPromptDialog = false
                        onOpenSettings()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE6230))
                ) {
                    Text("⚙️ সেটিংসে কী বসান", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyPromptDialog = false }) {
                    Text("পরে করব", color = Color.Gray)
                }
            }
        )
    }

    if (downloadClipTarget != null && activeVideo != null) {
        DirectDownloadDialog(
            video = activeVideo!!,
            clip = downloadClipTarget!!,
            onDismiss = { downloadClipTarget = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141416))
    ) {
        TopBar(onSettingsClick = onOpenSettings)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner with App Logo
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BrandLogo(size = 44, padding = 7, cornerRadius = 12)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "MAKLU CUT",
                                        color = Color.White,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "AI Viral Clip & Subtitles Maker",
                                        color = Color(0xFFFE6230),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF2E7D32))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "📱 100% Mobile",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "ইউটিউব ভিডিও থেকে ভাইরাল রিলস, শর্টস তৈরি ও ডাউনলোড করুন",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "কম্পিউটার বা সার্ভার ছাড়া সরাসরি আপনার ফোনেই চলে। AI দিয়ে বাংলা সাবটাইটেল ও ক্লিপ বের করে অ্যাপের ভেতর থেকেই সরাসরি ফোনে ডাউনলোড করুন।",
                            fontSize = 12.sp,
                            color = Color(0xFFA0A0A8),
                            lineHeight = 16.sp
                        )

                        if (apiKey.isBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenSettings() },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0x33FFB74D))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🔑", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "আপনার Gemini API কী যোগ করতে এখানে ক্লিক করুন (সেটিংস)",
                                        color = Color(0xFFFFB74D),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Input Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ইউটিউব ভিডিও লিংক দিন",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputUrl,
                            onValueChange = { inputUrl = it },
                            placeholder = { Text("https://www.youtube.com/watch?v=...", color = Color(0xFF6B6B75), fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFE6230),
                                unfocusedBorderColor = Color(0xFF33333C),
                                focusedContainerColor = Color(0xFF141416),
                                unfocusedContainerColor = Color(0xFF141416)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick action buttons
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val clip = clipboard.primaryClip
                                    if (clip != null && clip.itemCount > 0) {
                                        inputUrl = clip.getItemAt(0).text?.toString() ?: ""
                                        Toast.makeText(context, "লিংক পেস্ট করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "ক্লিপবোর্ডে কিছু নেই", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("📋 Paste")
                            }

                            if (inputUrl.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { inputUrl = "" },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935))
                                ) {
                                    Text("✕ Clear")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Try Sample Demo Chips
                        Text(
                            text = "বা নমুনা ভিডিও দিয়ে টেস্ট করুন:",
                            color = Color(0xFF8E8E98),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "🎙️ পডকাস্ট ডেমো" to "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                                "⚡ টক শো ডেমো" to "https://www.youtube.com/watch?v=jNQXAC9IVRw",
                                "💡 মোটিভেশন ডেমো" to "https://www.youtube.com/watch?v=kJQP7kiw5Fk"
                            ).forEach { (label, sampleUrl) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF262630))
                                        .clickable {
                                            inputUrl = sampleUrl
                                            Toast.makeText(context, "নমুনা লিংক সেট হয়েছে!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        color = Color(0xFFE0E0E6),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { doScout() },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFE6230),
                                disabledContainerColor = Color(0xFF553322)
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "বিশ্লেষণ হচ্ছে...",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            } else {
                                Text(
                                    text = "✨ ভাইরাল ক্লিপ বের করুন",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        if (statusText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "⏳ $statusText",
                                color = Color(0xFFFFB74D),
                                fontSize = 12.sp
                            )
                        }

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0x33E53935))
                            ) {
                                Text(
                                    text = "⚠️ $errorMessage",
                                    color = Color(0xFFFF8A80),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Active Video Info Card
            if (activeVideo != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "ভিডিও বিশ্লেষণ সম্পন্ন:",
                                color = Color(0xFF9E9EA4),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activeVideo!!.title,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "চ্যানেল: ${activeVideo!!.channel}",
                                color = Color(0xFFFE6230),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Found Clips
            if (clips.isNotEmpty()) {
                item {
                    Text(
                        text = "🏆 পাওয়া গেছে ${clips.size}টি ভাইরাল মোমেন্ট:",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(clips) { clip ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                activeVideo?.let { v -> onOpenClip(v, clip) }
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Score badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFE6230))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "🔥 ${clip.score}% Viral",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                // Timestamp
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF2A2A32))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "⏱ ${clip.start} - ${clip.end} (${clip.endSeconds - clip.startSeconds}s)",
                                        color = Color(0xFF00E5FF),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = clip.hook,
                                color = Color(0xFFFFD700),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = clip.title,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            if (clip.hashtags.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = clip.hashtags,
                                    color = Color(0xFF81D4FA),
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action buttons: Preview, Direct In-App Download, Share
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { activeVideo?.let { v -> onOpenClip(v, clip) } },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                ) {
                                    Text("▶ প্রিভিউ", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { downloadClipTarget = clip },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                                ) {
                                    Text("📥 ডাউনলোড", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val shareText = "${clip.hook}\n\n${clip.title}\n${clip.hashtags}\n\nClip: ${activeVideo?.url}&t=${clip.startSeconds}s"
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Clip"))
                                    },
                                    modifier = Modifier.width(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("📤")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. CLIP DETAIL & PLAYER SCREEN
// -------------------------------------------------------------
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ClipDetailScreen(
    video: VideoInfo,
    clip: Clip,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedRatio by remember { mutableStateOf("9:16") } // "9:16", "1:1", "16:9"
    var subtitleColor by remember { mutableStateOf(Color(0xFFFFD700)) } // Gold
    var subtitleFontSize by remember { mutableFloatStateOf(18f) }
    var subtitlePosition by remember { mutableStateOf("BOTTOM") } // "BOTTOM", "MIDDLE", "TOP"

    var showDownloadDialog by remember { mutableStateOf(false) }

    if (showDownloadDialog) {
        DirectDownloadDialog(
            video = video,
            clip = clip,
            onDismiss = { showDownloadDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141416))
    ) {
        TopBar(
            title = "${clip.start} - ${clip.end}",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Interactive Video Player Box with Aspect Ratio Crop Simulation
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                when (selectedRatio) {
                                    "9:16" -> 400.dp
                                    "1:1" -> 300.dp
                                    else -> 220.dp
                                }
                            )
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        // YouTube iframe embed
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.mediaPlaybackRequiresUserGesture = false
                                    val html = """
                                        <!DOCTYPE html>
                                        <html>
                                        <head>
                                            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
                                            <style>
                                                body, html { margin:0; padding:0; width:100%; height:100%; background:#000; overflow:hidden; }
                                                iframe { width:100%; height:100%; border:none; }
                                            </style>
                                        </head>
                                        <body>
                                            <iframe src="https://www.youtube.com/embed/${video.id}?start=${clip.startSeconds}&end=${clip.endSeconds}&autoplay=1&playsinline=1&controls=1" allow="autoplay; encrypted-media" allowfullscreen></iframe>
                                        </body>
                                        </html>
                                    """.trimIndent()
                                    loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Aspect ratio crop guide overlay
                        if (selectedRatio == "9:16") {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(9f / 16f)
                                    .border(2.dp, Color(0x88FE6230), RoundedCornerShape(8.dp))
                            )
                        } else if (selectedRatio == "1:1") {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                                    .border(2.dp, Color(0x88FE6230), RoundedCornerShape(8.dp))
                            )
                        }

                        // Subtitle Overlay Simulation
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = when (subtitlePosition) {
                                "TOP" -> Alignment.TopCenter
                                "MIDDLE" -> Alignment.Center
                                else -> Alignment.BottomCenter
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xCC000000))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = clip.hook,
                                    color = subtitleColor,
                                    fontSize = subtitleFontSize.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Aspect Ratio Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "📐 ফরম্যাট ও রেশিও নির্বাচন:",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("9:16 (Shorts/Reels)", "1:1 (Square)", "16:9 (Landscape)").forEach { ratioLabel ->
                                val ratioKey = ratioLabel.substringBefore(" ")
                                val isSelected = selectedRatio == ratioKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFFFE6230) else Color(0xFF2A2A32))
                                        .clickable { selectedRatio = ratioKey }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ratioKey,
                                        color = if (isSelected) Color.White else Color(0xFFA0A0A8),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Subtitle Customization
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "🎨 বাংলা সাবটাইটেল স্টাইল ও কালার:",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                Color(0xFFFFD700) to "হলুদ",
                                Color(0xFF00E5FF) to "সায়ান",
                                Color(0xFF00E676) to "সবুজ",
                                Color(0xFFFFFFFF) to "সাদা",
                                Color(0xFFFF5252) to "লাল"
                            ).forEach { (color, _) ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (subtitleColor == color) 3.dp else 0.dp,
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                        .clickable { subtitleColor = color }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Subtitle Position
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("TOP" to "উপরে", "MIDDLE" to "মাঝে", "BOTTOM" to "নিচে").forEach { (posKey, posLabel) ->
                                val isSelected = subtitlePosition == posKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF3949AB) else Color(0xFF2A2A32))
                                        .clickable { subtitlePosition = posKey }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = posLabel,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Clip Details & Subtitles Text
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "📝 হুক ও সাবটাইটেল স্ক্রিপ্ট:",
                            color = Color(0xFF9E9EA4),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = clip.hook,
                            color = Color(0xFFFFD700),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = clip.subtitles.ifBlank { "টাইমস্ট্যাম্প অনুযায়ী সাবটাইটেল স্বয়ংক্রিয়ভাবে যুক্ত করা আছে।" },
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = clip.hashtags,
                            color = Color(0xFF81D4FA),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Direct Actions: DOWNLOAD (Direct in-app), YouTube, Copy, Share
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Big In-App Download Button
                    Button(
                        onClick = { showDownloadDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                    ) {
                        Text("📥 সরাসরি ফোনে ভিডিও ডাউনলোড করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val ytIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://youtu.be/${video.id}?t=${clip.startSeconds}")
                            )
                            context.startActivity(ytIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000))
                    ) {
                        Text("▶ YouTube এ এই ক্লিপটি ওপেন করুন", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val fullText = "${clip.hook}\n\n${clip.title}\n\n${clip.subtitles}\n\n${clip.hashtags}"
                            clipboard.setPrimaryClip(ClipData.newPlainText("MAKLU CUT Clip", fullText))
                            Toast.makeText(context, "সাবটাইটেল ও ট্যাগ কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("📋 হুক ও সাবটাইটেল কপি করুন", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val shareText = "${clip.hook}\n\n${clip.title}\n\n${clip.hashtags}\n\nভিডিও লিংক: https://youtu.be/${video.id}?t=${clip.startSeconds}"
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Clip"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("📤 সোশাল মিডিয়ায় শেয়ার করুন (Shorts/Reels)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. SETTINGS SCREEN
// -------------------------------------------------------------
@Composable
fun SettingsScreen(
    currentApiKey: String,
    currentModel: String,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var modelInput by remember { mutableStateOf(currentModel) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141416))
    ) {
        TopBar(title = "সেটিংস", onBack = onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🤖 Google Gemini AI সেটিংস",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "আপনার ব্যক্তিগত Google Gemini API Key নিচে পেস্ট করুন। এটি সম্পূর্ণ আপনার ফোনের নিরাপদ মেমোরিতে সেভ থাকবে।",
                            color = Color(0xFFA0A0A8),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Get Free Key Button
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                        ) {
                            Text("🌐 ফ্রি Gemini API Key নিন (Google AI Studio)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Gemini API Key:",
                            color = Color(0xFFFE6230),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            placeholder = { Text("আপনার Gemini API Key পেস্ট করুন...", color = Color.Gray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFE6230),
                                unfocusedBorderColor = Color(0xFF33333C),
                                focusedContainerColor = Color(0xFF141416),
                                unfocusedContainerColor = Color(0xFF141416)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "AI Model:",
                            color = Color(0xFFFE6230),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        listOf(
                            "gemini-3.5-flash",
                            "gemini-3-flash-preview",
                            "gemini-flash-latest"
                        ).forEach { m ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { modelInput = m }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = modelInput == m,
                                    onClick = { modelInput = m },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFE6230))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = m, color = Color.White, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onSave(apiKeyInput.trim(), modelInput.trim())
                                Toast.makeText(context, "সেটিংস সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                                onBack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE6230))
                        ) {
                            Text("💾 সেটিংস সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BrandLogo(size = 38, padding = 6, cornerRadius = 10)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "MAKLU CUT",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Version: 2.4 (Standalone Mobile)",
                                    color = Color(0xFF9E9EA4),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Created by Faizul Islam Rifat\n\nবৈশিষ্ট্যসমূহ:\n• ১০০% সরাসরি ফোনে ডাউনলোড (কোনো বাহ্যিক ওয়েবসাইটে নেবে না)।\n• ইউটিউব অ্যাপের Share মেনু থেকে সরাসরি ভিডিও ওপেন করার সুবিধা।\n• এপিআই কী কোনো সার্ভার বা গিটহাবে জমা থাকে না, কেবল আপনার ফোনেই থাকে।\n• আকর্ষণীয় ব্র্যান্ড লোগো ও বাংলা সাবটাইটেল সাপোর্ট।",
                            color = Color(0xFFA0A0A8),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
