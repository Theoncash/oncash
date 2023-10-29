package `in`.oncash.oncash.Component


import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import `in`.oncash.oncash.R

class TimerService : Service() {
    private var overlayView: View? = null
    private var timerTextView: TextView? = null
    private val handler = Handler()
    private var secondsElapsed = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.timer_overlay, null)
        timerTextView = overlayView?.findViewById(R.id.timerTextView)

        windowManager.addView(overlayView, params)

        // Update the timer text every second
        handler.postDelayed(object : Runnable {
            override fun run() {
                secondsElapsed++
                timerTextView?.text = "Time: $secondsElapsed seconds"
                handler.postDelayed(this, 1000)
            }
        }, 1000)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (overlayView != null) {
            windowManager.removeView(overlayView)
        }
        handler.removeCallbacksAndMessages(null)
    }
}
