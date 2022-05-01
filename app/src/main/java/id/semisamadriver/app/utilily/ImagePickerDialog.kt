package id.semisamadriver.app.utilily

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.semisamadriver.app.R

class ImagePickerDialog(private val context: Context) {

    companion object {
        const val SELECT_GALLERY = "SELECT_GALLERY"
        const val SELECT_CAPTURE = "SELECT_CAPTURE"
    }

    fun show() {
        val arrayChoice = context.resources.getStringArray(R.array.labelChooseImages)
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(context.getString(R.string.labelChooseImage))
        builder.setItems(arrayChoice) { _, which ->
            when(which) {
                0 -> { bridge?.selectedCaptureMode(SELECT_GALLERY) }
                1 -> { bridge?.selectedCaptureMode(SELECT_CAPTURE) }
            }
        }.show()
    }
    private var bridge: Listener? = null

    fun setListener(bridge: Listener) {
        this.bridge = bridge
    }

    interface Listener {
        fun selectedCaptureMode(selected: String)
    }

}