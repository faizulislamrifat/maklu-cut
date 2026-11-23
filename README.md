# MAKLU CUT 🎬

Turn long videos, podcasts, and live streams into viral, captioned short clips ready to post on **YouTube Shorts**, **Instagram Reels**, **TikTok**, and **Facebook**. 

Free and open-source: run it 100% locally on your computer or host it on a server with your own AI key.

Created by **[Faizul Islam Rifat](https://github.com/faizulislamrifat)** · [Instagram](https://www.instagram.com/iamfaizulislamrifat/)

---

## ⚡ What is MAKLU CUT?

Paste a YouTube, Twitch, or Kick video link (or upload any local video file), and MAKLU CUT automatically:
1. **Scouts Viral Highlights**: Analyzes conversation peaks, punchlines, emotional moments, and laughter using AI.
2. **Formats Aspect Ratio**: Renders in **9:16 Vertical** (Shorts/Reels), **1:1 Square** (Feed posts), or **16:9 Landscape** (Widescreen).
3. **Adds Dynamic Subtitles**: Word-by-word bouncy pop animated captions with custom Bengali fonts (`Hind Siliguri`, `Kalpurush`, `Noto Sans Bengali`) and Latin fonts (`Poppins`, `Anton`, `Archivo Black`).
4. **Auto-Generates Catchy Hooks**: High-CTR header titles placed directly on the video.
5. **Direct Social Auto-Posting**: Connect YouTube, Instagram, TikTok, and Facebook to post instantly or schedule into a publishing queue.

---

## 🚀 How to Use MAKLU CUT (User Guide)

### 📱 Android App (APK)
MAKLU CUT now comes with an official Android mobile application:
- **Download APK**: Download [`maklu-cut.apk`](maklu-cut.apk) directly from this repository root.
- **How to Use**:
  1. Transfer `maklu-cut.apk` to your Android device and tap to install.
  2. Ensure your PC is running MAKLU CUT (`start.bat`) on the same Wi-Fi network.
  3. Open the app, tap ⚙️ (Settings) to enter your PC's Wi-Fi IP (e.g. `http://192.168.1.15:8765`), and clip videos on the go!

### Prerequisites
Make sure your computer has:
1. **Python 3.10+** ([python.org](https://www.python.org/downloads/)) — *Make sure to check "Add Python to PATH" during installation.*
2. **FFmpeg** (For video processing):
   - **Windows**: `winget install Gyan.FFmpeg`
   - **macOS**: `brew install ffmpeg`
   - **Linux**: `sudo apt install ffmpeg`

---

### Method 1: Super Easy 1-Click Launch (Recommended)

1. Clone or download this repository:
   ```bash
   git clone https://github.com/faizulislamrifat/maklu-cut.git C:\maklu-cut
   ```
2. Open the `maklu-cut` folder in File Explorer.
3. **Double-click `start.bat`**!
   - It will automatically set up Python dependencies on the first run.
   - It starts the MAKLU CUT server.
   - It automatically opens your web browser at **`http://127.0.0.1:8765`**!

---

### Method 2: Manual Terminal Launch

```bash
git clone https://github.com/faizulislamrifat/maklu-cut.git C:\maklu-cut
cd C:\maklu-cut
pip install -r requirements.txt
python -m studio.server
```
Then open **`http://127.0.0.1:8765`** in your browser.

---

## 🛠️ Step-by-Step Workflow

### Step 1: Connect your AI (One-Time Setup)
1. Open the dashboard at `http://127.0.0.1:8765`.
2. Go to **Settings ➔ AI**.
3. Choose your AI provider (e.g., **Google Gemini**, **OpenAI**, **Groq**, **Claude**, or offline **Ollama**).
4. Paste your free Gemini API key from [Google AI Studio](https://aistudio.google.com/apikey) and click **"Save and test"**.
*(Note: If you don't have an AI key, MAKLU CUT will still work by finding loud conversational audio peaks!)*

### Step 2: Make Viral Clips
1. Paste a video link (YouTube, Twitch, Kick) or drop in a video file.
2. Click **Options** to customize:
   - **Layout**: `9:16 Blur / Zoom / Crop`, `1:1 Square (Feed)`, or `16:9 Landscape`.
   - **Caption style**: 
     - `Hind Siliguri (বাংলা)` — Clean, elegant Bengali subtitles.
     - `MAKLU (Orange)` — Signature orange pop highlight.
     - `Kalpurush (কালপুরুষ)` — Classic Bengali look.
     - `Bounce / Hype / Clean` — High-energy English fonts.
3. Click **"Make clips"**.
4. The AI will transcribe the audio, detect the best moments, generate titles and hashtags, and render ready-to-post clips.

### Step 3: Customize & Post
- Click on any generated clip to review it.
- Customize the Hook title, Caption Position (**Top**, **Middle**, or **Bottom**), and choose an **Active Highlight Color**.
- Click **"Download"** to save the `.mp4` file directly.
- Or under **Post it**, select your connected accounts (YouTube, Instagram, TikTok, Facebook) and hit **"Post now"** or **"Add to queue"**!

---

## 🌟 Key Features

| Feature | Description |
|---|---|
| **AI Clip Scout** | Detects emotional climaxes, viral arguments, jokes, and reactions. |
| **Bengali Typography** | Native conjunct character support with `Hind Siliguri`, `Kalpurush`, and `Noto Sans Bengali`. |
| **Multi-Aspect Ratios** | One-click formatting for 9:16 (Shorts/Reels), 1:1 (Square), and 16:9 (Landscape). |
| **Bouncy Pop Captions** | Word-by-word synchronized animations with customizable colors and positions. |
| **Auto-Posting** | Direct upload to YouTube Shorts, Instagram Reels, TikTok, and Facebook. |
| **Live Stream Clipping** | Clip highlights in real-time from ongoing live streams. |
| **Creator Management** | Manage multiple streamers and creators with customized preferences and channel links. |
| **Zero-Friction Access** | Starts straight into the dashboard locally without mandatory login prompts. |

---

## 🤖 Supported AI Providers

Set in the dashboard under **Settings ➔ AI**, or in `.env`:

| Provider | Service | Get Free API Key |
|---|---|---|
| `gemini` | Google Gemini (Fast & Generous Free Tier) | https://aistudio.google.com/apikey |
| `openai` | OpenAI (GPT-4o, GPT-4o-mini) | https://platform.openai.com/api-keys |
| `groq` | Groq (Ultra-fast Llama 3.3) | https://console.groq.com/keys |
| `xai` | Grok (xAI) | https://console.x.ai |
| `anthropic` | Claude | https://console.anthropic.com |
| `ollama` | 100% Offline Local LLMs (No Key) | https://ollama.com |

---

## ⚙️ Configuration & `.env`

All settings can be configured via environment variables or in a `.env` file (see [`.env.example`](.env.example)):

```ini
# Multi-user accounts (0 = single user direct dashboard, 1 = multi-user login)
PEAKCUT_ACCOUNTS=0

# AI Configuration
PEAKCUT_AI_PROVIDER=gemini
PEAKCUT_AI_MODELS=gemini-2.5-flash
PEAKCUT_AI_KEY=your-api-key

# Social Media Auto-Posting (Optional)
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
PEAKCUT_IG_APP_ID=
PEAKCUT_IG_APP_SECRET=
PEAKCUT_TIKTOK_CLIENT_KEY=
PEAKCUT_TIKTOK_CLIENT_SECRET=
PEAKCUT_FB_APP_ID=
PEAKCUT_FB_APP_SECRET=
```

---

## 🔒 Security & Privacy

- **Your Data Stays Yours**: Videos, transcripts, and AI keys are stored locally on your machine (`data/` is excluded by `.gitignore`).
- **No Third-Party Audio Leakage**: Speech-to-text transcription runs 100% offline on your computer using local ONNX speech models.
- **CSRF & Origin Verification**: All sensitive state mutations are restricted to trusted origins.
- **Enterprise-Grade Tests**: Includes an exhaustive 18-suite test pipeline verifying data isolation, permissions, and security hardening (`python tests/run.py`).

---

## 📦 Deployment (Docker)

To deploy on a Linux VPS / cloud server with PostgreSQL and automatic HTTPS (Caddy):

```bash
docker compose up -d --build
```
See [DEPLOY.md](DEPLOY.md) for full server deployment details.

---

## 📜 License

This project is licensed under the **[MIT License](LICENSE)**.  
Caption fonts in `studio/fonts/` are licensed under the SIL Open Font License (OFL).
