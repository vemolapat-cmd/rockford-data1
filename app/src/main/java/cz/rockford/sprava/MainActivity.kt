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

    // Sem pozdeji vlozime skutecnou adresu centralniho Rockford datoveho souboru.
    // Aplikace pri spusteni stahne aktualni JSON; kdyz neni internet, pouzije posledni ulozena data.
    private val dataUrl = "https://YOUR-DATA-SERVER.example/rockford-data.json"
    private val prefs by lazy { getSharedPreferences("rockford", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
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

                // Dulezite: index.html obsahuje JavaScriptovou funkci printReminder(),
                // ve ktere je text "</body>" uvnitr retezce. Obycejne replace() by
                // nahradilo tuto prvni vyskyt a WebView by zbytek JavaScriptu zobrazilo
                // jako obycejny text. Proto vkladame mobile.js pouze pred POSLEDNI
                // skutecny uzaviraci </body> dokumentu.
                val withData = base.replace(
                    "window.__ROCKFORD_DATA__ || {}",
                    "JSON.parse($safe)"
                )
                val withCss = withData.replace(
                    "</head>",
                    "<link rel=\"stylesheet\" href=\"mobile.css\"></head>"
                )
                val bodyClose = "</body>"
                val bodyIndex = withCss.lastIndexOf(bodyClose)
                val html = if (bodyIndex >= 0) {
                    withCss.substring(0, bodyIndex) +
                        "<script src=\"mobile.js\"></script>" +
                        withCss.substring(bodyIndex)
                } else {
                    withCss + "<script src=\"mobile.js\"></script>"
                }

                web.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
                if (fresh == null && cached == null) {
                    Toast.makeText(this, "Offline: pouzita data zabalena v aplikaci.", Toast.LENGTH_SHORT).show()
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
