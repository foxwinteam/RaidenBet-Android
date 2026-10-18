package com.raidenbet.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    companion object {
        // ═══════════════════════════════════════════════════════════
        //  آدرس سایت — اگر خواستید سایت را عوض کنید فقط همین خط را تغییر دهید
        // ═══════════════════════════════════════════════════════════
        const val SITE_URL = "https://raidenbet.com"

        // مدت نمایش صفحه شروع (بر حسب میلی‌ثانیه) — ۲۰۰۰ یعنی ۲ ثانیه
        const val SPLASH_DURATION = 3000L
    }

    private var webView: WebView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ۱) اول صفحه شروع (لوگو) نشان داده می‌شود
        setContentView(R.layout.splash_screen)

        // ۲) بعد از مدت مشخص‌شده، صفحه اصلی با وب‌ویو باز می‌شود
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                showMainScreen(savedInstanceState)
            }
        }, SPLASH_DURATION)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showMainScreen(savedState: Bundle?) {
        setContentView(R.layout.activity_main)

        val webView = findViewById<WebView>(R.id.webView)
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        this.webView = webView
        this.swipeRefresh = swipeRefresh

        // ─── تنظیمات وب‌ویو ───
        webView.settings.apply {
            javaScriptEnabled = true        // تقریباً همه سایت‌ها به جاوااسکریپت نیاز دارند
            domStorageEnabled = true        // برای ذخیره نشست ورود (لاگین ماندن)
            useWideViewPort = true
            loadWithOverviewMode = true
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // وقتی صفحه کامل لود شد، انیمیشن رفرش متوقف می‌شود
                swipeRefresh.isRefreshing = false
            }
        }

        // ─── کشیدن از بالا به پایین برای رفرش ───
        swipeRefresh.setColorSchemeColors(getColor(R.color.gold))
        swipeRefresh.setProgressBackgroundColorSchemeColor(getColor(R.color.bg_card))
        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }

        // ─── دکمه بازگشت: اول به صفحه قبلیِ سایت برمی‌گردد، بعد از اپ خارج می‌شود ───
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })

        // ─── بارگذاری سایت (یا بازیابی صفحه قبلی بعد از چرخش گوشی) ───
        if (savedState != null) {
            webView.restoreState(savedState)
        } else {
            webView.loadUrl(SITE_URL)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView?.saveState(outState)
    }
}
