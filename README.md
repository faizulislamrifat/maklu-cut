# MAKLU CUT

Turn long videos, podcasts, and live streams into viral captioned clips ready to post on YouTube Shorts, Instagram Reels, TikTok, and Facebook. Free and open source: run it on your own computer or server, with your own AI key.

Created by **[Faizul Islam Rifat](https://github.com/faizulislamrifat)** · [Instagram](https://www.instagram.com/iamfaizulislamrifat/)

Paste a YouTube, Twitch or Kick link (or upload a video file) and MAKLU CUT automatically finds the moments worth clipping, formats them into **9:16 (Shorts/Reels)**, **1:1 (Square)**, or **16:9 (Landscape)**, adds animated word-by-word captions with custom fonts, and can auto-post them directly to your connected social channels.

## Key Features

- **AI-Powered Clip Scout**: Transcribes the audio, analyzes conversational peaks, emotional moments, and laughter, then selects high-engagement standalone clips.
- **Bengali & Multilingual Support**: Custom Bengali typography (`Hind Siliguri`, `Kalpurush`, `Noto Sans Bengali`) with accurate conjunct letter rendering, Bengali hook generation, and Whisper punctuation formatting.
- **Multiple Video Formats**: Supports **9:16 Vertical** (Shorts/Reels/TikTok), **1:1 Square** (Instagram/Facebook Feed), and **16:9 Landscape** (YouTube Widescreen).
- **Customizable Captions**: Word-by-word bouncy pop animation, custom highlight color picker, adjustable font sizes, and on-screen positioning (Top, Middle, Bottom).
- **Social Media Auto-Posting**: Connect YouTube Shorts, Instagram Reels, TikTok, and Facebook Video/Reels in one click; post now or add to queue.
- **Live Stream Clipping**: Records a live stream into a rolling buffer and clips viral highlights in real time while the stream is ongoing.
- **Creator Workspaces**: Multi-creator management with dedicated channels, clip presets, and scheduling rules.
- **Single-User or Multi-User Mode**: Opens straight into the dashboard on your computer with zero login hassle, or enable multi-user accounts (`PEAKCUT_ACCOUNTS=1`) for team/server deployment.
- **Bring Your Own AI**: Connect Google Gemini, OpenAI, Groq, Anthropic Claude, xAI Grok, OpenRouter, or run 100% offline local models via Ollama. No key also works via audio loudness detection!

## Quick start

You need Python 3.10 or newer and `ffmpeg`.
- **Windows**: `winget install Gyan.FFmpeg`
- **macOS**: `brew install ffmpeg`
- **Linux**: `sudo apt install ffmpeg`

Clone the repository into a permanent folder:

```bash
git clone https://github.com/faizulislamrifat/maklu-cut.git C:\maklu-cut
cd C:\maklu-cut
pip install -r requirements.txt
python -m studio.server
```

```bash
python -m studio.server
```

The first start downloads the speech model by itself (about 480 MB, once).

Open http://127.0.0.1:8765. There is no sign-in: it opens straight into the dashboard. Go to **Settings, AI**, choose
your AI service, paste your key and press **Save and test** (see [Choose your AI](#choose-your-ai)).

## Choose your AI

PeakCut needs a chat model that can return JSON. The easy way is in the dashboard: **Settings, AI**. Pick the
service, paste your key, press **Find models** to choose from the service's own list, then **Save and test**. The
key is kept in the data folder on your machine and is never shown again.

If you prefer a file (or run a server), the same three things can go in `.env`. What is saved in the dashboard wins:

```ini
PEAKCUT_AI_PROVIDER=gemini
PEAKCUT_AI_MODELS=gemini-2.5-flash
PEAKCUT_AI_KEY=your-key-here
```

| `PEAKCUT_AI_PROVIDER` | Service | Where to get a key |
|---|---|---|
| `gemini` | Google Gemini | https://aistudio.google.com/apikey |
| `openai` | OpenAI | https://platform.openai.com/api-keys |
| `xai` | Grok (xAI) | https://console.x.ai |
| `groq` | Groq | https://console.groq.com/keys |
| `openrouter` | OpenRouter (many models, one key) | https://openrouter.ai/keys |
| `anthropic` | Claude | https://console.anthropic.com |
| `ollama` | A model on your own machine, no key | https://ollama.com |
| `custom` | Any OpenAI-compatible address: also set `PEAKCUT_AI_BASE_URL` | |

- `PEAKCUT_AI_MODELS` is the model id exactly as the service names it. Model names change often, so check the
  service's current list. Several ids separated by commas are tried in order, which gives you a fallback.
- A fast, inexpensive model is the right choice. A one-hour video is read in about four requests.
- You pay the AI service directly for what you use. The **AI usage** page in the app shows how many requests and
  tokens each video took.
- Speech to text does not use the AI service. It runs on your own machine, so audio never leaves it.

## Settings

Everything is set through environment variables, normally in a `.env` file next to this README.
[`.env.example`](.env.example) lists every setting with a short explanation. The ones that matter first:

| Setting | What it is for |
|---|---|
| `PEAKCUT_AI_PROVIDER`, `PEAKCUT_AI_MODELS`, `PEAKCUT_AI_KEY` | The AI that picks clips and writes titles (or set it in the dashboard) |
| `PEAKCUT_ACCOUNTS` | `1` makes everyone sign in. Use it on any server other people can reach |
| `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` | Optional: Sign in with Google, and connecting YouTube |
| `PEAKCUT_SMTP_*`, `PEAKCUT_MAIL_FROM` | Optional: confirmation and password emails |
| `PEAKCUT_DATA` | Where videos, clips and the database are kept (default `./data`) |

`.env`, the `secrets/` folder and all user data are ignored by git. Keep keys there, never in the code.

## Admins

This only matters with accounts switched on (`PEAKCUT_ACCOUNTS=1`); without them you are the owner and can do
everything. An account is an admin only when the database says so. No email address is special in the code and no web request
can change a role. Do it on the machine that runs PeakCut:

```bash
python -m studio.admin grant you@example.com
```

`revoke` and `list` work the same way. The admin dashboard is at `/admin` and answers "not found" to everyone else.

## How it is built

| Part | What it is |
|---|---|
| `studio/` | The server: FastAPI, the clip pipeline, live recording, accounts |
| `studio/ai.py` | The only file that talks to an AI service. Add a provider here |
| `web/` | The pages: landing, sign-in, the app, the admin dashboard (plain HTML, CSS and JavaScript, no build step) |
| `tests/` | Tests that run against a throwaway copy of the app |
| `deploy/`, `Dockerfile`, `docker-compose.yml` | One-server deployment with PostgreSQL and automatic HTTPS |

Video work is done with `ffmpeg` and `yt-dlp`. Speech is transcribed with `sherpa-onnx` and the Parakeet model.
Data is kept per user: accounts in the database (SQLite on your own computer, PostgreSQL on a server), videos and
clips in each user's own folder. [AGENTS.md](AGENTS.md) is a short map of the code for contributors and AI coding
assistants.

## Where your videos and clips are kept

Everything PeakCut keeps is in one folder: videos, clips, recordings, creators, settings and the speech model. By
default it is the `data` folder inside PeakCut. **Settings, Storage** shows the folder and how much room its drive
has, opens it in one click, and lets you move it to another drive: choose the new folder, and PeakCut moves
everything there the next time it starts. If the drive is nearly full, PeakCut says so and stops taking new videos
until there is room. (`PEAKCUT_DATA` in `.env` still works and takes priority.)

## Updating

PeakCut checks GitHub once a minute while it runs. When a newer version is out, the dashboard shows a notice
within a minute or two (no restart needed), with what changed
and how to update: ask your AI coding assistant to do it (the notice has a sentence to paste), or run
`git pull && pip install -r requirements.txt` in the PeakCut folder and restart it (with Docker:
`git pull && docker compose up -d --build`). Your `.env`, keys and clips are not touched. Only the two commit ids
are sent to GitHub; set `PEAKCUT_UPDATE_CHECK=0` to turn the check off.

## Deploy

[DEPLOY.md](DEPLOY.md) walks through a single server with Docker: the app, PostgreSQL, and Caddy for HTTPS.

```bash
docker compose up -d --build
```

## How it behaves under load

- The page asks the server one small question every few seconds. When nothing has changed, the answer comes
  straight from memory.
- Each person's videos wait in their own line and the lines take turns, so one person cannot hold everyone up.
- The server measures its own processor, memory and disk every second. Extra videos and recordings start only
  while there is room, wait briefly when it is nearly full, and continue the moment it frees up.
- There are no limits on upload size, video length, recordings, creators or storage unless you set them in
  `.env`. Five videos can be sent at once. Request sizes are always capped, so junk can't fill the memory.

## Tests

```bash
python tests/run.py
```

This starts a copy of the app on a spare port with an empty temporary data folder, runs the permission, abuse and
queue tests against it, then removes it. It needs no keys and never touches real data.

## Security

- With sign-in off, PeakCut listens on this computer only (`127.0.0.1`), refuses changes sent by other websites,
  and only answers to its own address. Do not expose it to the internet that way: turn accounts on.
- Passwords are stored only as salted scrypt hashes. Sessions are random tokens stored hashed; cookies are HTTP-only.
- Every route works out who is asking from the session, never from the request, so users only reach their own data.
- Only known video sites are fetched, and text from videos is treated as data when it is shown to the AI.
- Found a problem? Please report it privately through the repository's security advisories instead of opening a
  public issue.

## Using it responsibly

Clip videos you made, or have permission to use. You are responsible for the rights to what you download, clip and
post, and for following the terms of the sites you fetch from and post to.

## Licence

[MIT](LICENSE). Caption fonts in `studio/fonts/` (Anton, Archivo Black, Poppins) are under the SIL Open Font
License; the licence texts are in the same folder.
