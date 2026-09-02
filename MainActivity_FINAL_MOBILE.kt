package cz.rockford.sprava

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {
    private lateinit var web: WebView
    private val dataUrl = "https://YOUR-DATA-SERVER.example/rockford-data.json"
    private val prefs by lazy { getSharedPreferences("rockford", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            webViewClient = WebViewClient()
        }
        setContentView(web)
        loadRockford()
    }

    private fun loadRockford() {
        val cached = prefs.getString("data", null)
        Thread {
            val fresh = runCatching { download(dataUrl) }.getOrNull()
            val json = fresh ?: cached ?: assets.open("data.json").bufferedReader().use { it.readText() }
            if (fresh != null) prefs.edit().putString("data", fresh).apply()
            runOnUiThread {
                val safe = JSONObject.quote(json)
                val base = assets.open("index.html").bufferedReader().use { it.readText() }
                val css = assets.open("mobile.css").bufferedReader().use { it.readText() }
                val js = assets.open("mobile.js").bufferedReader().use { it.readText() }
                val injected = base
                    .replace("window.__ROCKFORD_DATA__ || {}", "JSON.parse($safe)")
                    .replace("</head>", "<style>$css</style></head>")
                    .replace("</body>", "<script>$js</script></body>")
                web.loadDataWithBaseURL("file:///android_asset/", injected, "text/html", "UTF-8", null)
                if (fresh == null && cached == null) {
                    Toast.makeText(this, "Offline: použita data zabalená v aplikaci.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun download(url: String): String {
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 8000
        c.readTimeout = 10000
        c.requestMethod = "GET"
        c.setRequestProperty("Accept", "application/json")
        c.connect()
        if (c.responseCode !in 200..299) error("HTTP ${c.responseCode}")
        return c.inputStream.bufferedReader().use { it.readText() }
    }

    override fun onDestroy() { web.destroy(); super.onDestroy() }
}
