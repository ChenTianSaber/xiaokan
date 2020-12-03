package com.chentian.xiangkan.view.content

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.chentian.xiangkan.*
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.utils.HtmlUtils
import com.chentian.xiangkan.utils.RssUtils
import com.githang.statusbar.StatusBarCompat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * 内容页fragment，专门用来展示视频类内容
 * 这个页面和其他的业务应该是独立的，它就是用来展示内容而已
 */
class ContentWebVideoActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ContentFragment"
    }

    //  region field

    /**
     * 当选择网页模式的时候会展示这个
     */
    private lateinit var fullWebView: WebView

    private lateinit var backBtn: ImageView
    private lateinit var modeBtn: ImageView

    private var isTextMode: Boolean = true

    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_content_webvideo)
        StatusBarCompat.setTranslucent(window, true)

        initView()
        initData()
    }

    private fun initView() {
        fullWebView = findViewById(R.id.full_web_view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fullWebView.setOnScrollChangeListener { _, scrollX, _, _, _ ->  fullWebView.scrollTo(scrollX,0)}
        }

        backBtn = findViewById(R.id.back_btn)
        modeBtn = findViewById(R.id.mode_btn)

        backBtn.setOnClickListener {
            finish()
        }

        modeBtn.setOnClickListener {
            if (isTextMode) {
                // 这个时候转为网页模式

                Glide.with(this).load(R.mipmap.compass).into(modeBtn)
            } else {
                // 这个时候转为文本模式

                Glide.with(this).load(R.mipmap.book).into(modeBtn)
            }
            isTextMode = !isTextMode
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initData() {

        val data: RssItem = intent.getParcelableExtra<RssItem>("RssItem") as RssItem
        Log.d(TAG, "initData: $data")

        // 对B站up主动态做适配
        // 把模式默认设置为网页模式，取出description里的视频链接，赋值给link
        if (data.link.startsWith("https://t.bilibili.com")) {
            val m = Pattern.compile("视频地址.*?<br>").matcher(data.description)
            if (m.find()) {
                val str = m.group()
                Log.d(TAG, "videoUrl: $str")
                val videoUrl = str.substring(5, str.length - 4)
                Log.d(TAG, "videoUrl: $videoUrl")
                isTextMode = false
                data.link = videoUrl
            }
        }

        val webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
            ): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading: ${request?.url}")
                if (request?.url.toString().startsWith("bilibili://")) {
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        fullWebView.settings.javaScriptEnabled = true
        fullWebView.settings.domStorageEnabled = true
        fullWebView.settings.blockNetworkImage = false
        fullWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        fullWebView.webViewClient = webViewClient

//            fullWebView.loadUrl(data.link)
        fullWebView.loadDataWithBaseURL("file:///android_asset/", HtmlUtils.buildHtml(data), "text/html", "UTF-8", null)

    }

}