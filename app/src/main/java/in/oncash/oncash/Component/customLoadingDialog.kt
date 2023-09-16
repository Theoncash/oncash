package `in`.oncash.oncash.Component

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.TextView
import `in`.oncash.oncash.R
import kotlinx.coroutines.CoroutineScope

class customLoadingDialog(context: Context) {

    private val dialog = Dialog(context)

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_loading_dialog)
        dialog.setCancelable(false)
    }

    fun setMessage(message: String) {
        val messageTextView: TextView = dialog.findViewById(R.id.loadingMessage)
        messageTextView.text = message
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
