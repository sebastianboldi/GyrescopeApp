package com.tribeone.gyrescopeapp.ui

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tribeone.gyrescopeapp.R
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private var facingUp: Boolean = true
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mSensorOrientation: Sensor? = null
    private var mSensorProximity: Sensor? = null
    private var drawingCanvasView: DrawingCanvasView? = null
    private var flInfo: FrameLayout? = null
    private var ivBg: ImageView? = null
    private var oldRoll: Float = 0f
    private var webView: WebView? = null
    //private var playerView: PlayerView? = null

    var url: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        url = intent.getStringExtra("url")

        drawingCanvasView = findViewById(R.id.drawingCanvasView)
        flInfo = findViewById(R.id.fl_info)
        ivBg = findViewById(R.id.iv_bg)
        webView = findViewById(R.id.webview)
        //playerView = findViewById(R.id.playerView);

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer =
            mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)  //check device is facing upside
        mSensorOrientation =
            mSensorManager!!.getDefaultSensor(SensorManager.SENSOR_ORIENTATION)   //check left right tilt
        mSensorProximity =
            mSensorManager!!.getDefaultSensor(Sensor.TYPE_PROXIMITY)   //check with camera and object distance

        if (mSensorProximity == null) {
            Toast.makeText(this, "No proximity sensor found in device.", Toast.LENGTH_SHORT).show()
        }

        if (url != null) {
            webView!!.settings.setAppCacheEnabled(true)
            webView!!.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            webView!!.settings.javaScriptEnabled = true
            webView!!.settings.domStorageEnabled = true
            webView!!.settings.mediaPlaybackRequiresUserGesture = false
            webView!!.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            //webView!!.addJavascriptInterface(WebInterface(getApplicationContext()), "JSInterface")
            webView!!.webChromeClient = WebChromeClient()
            webView!!.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    url?.let {
                        view.loadUrl(it)
                    }
                    return true
                }
            }
            url?.let {
                webView!!.loadUrl(it)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        mSensorManager!!.registerListener(
            sensorOrientationListener,
            mSensorOrientation,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        mSensorManager!!.registerListener(
            accelerometerListener,
            mAccelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        mSensorManager!!.registerListener(
            proximitySensorEventListener,
            mSensorProximity,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        mSensorManager!!.unregisterListener(sensorOrientationListener)
        mSensorManager!!.unregisterListener(accelerometerListener)
        mSensorManager!!.unregisterListener(proximitySensorEventListener)
    }


    private val sensorOrientationListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent?) {
            val roll = sensorEvent?.values?.get(1)
            val pitch = sensorEvent?.values?.get(0)

            if (abs(roll ?: (0f - oldRoll)) >= 1) {
                oldRoll = roll ?: 0f
                drawingCanvasView?.calculate(roll, pitch)
            }

            if (facingUp) {
                if (flInfo != null && flInfo!!.visibility != View.GONE) {
                    flInfo?.visibility = View.GONE
                }
            } else {
                if (flInfo != null && flInfo!!.visibility != View.VISIBLE) {
                    flInfo?.visibility = View.VISIBLE
                }
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }
    }

    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent?) {
            val zValue: Float = sensorEvent?.values?.get(2) ?: 0f
            facingUp = zValue >= 0
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }
    }

    private val proximitySensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0f) {
                    drawingCanvasView?.changePathColor(true)
                } else {
                    drawingCanvasView?.changePathColor(false)
                }
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }
    }
}