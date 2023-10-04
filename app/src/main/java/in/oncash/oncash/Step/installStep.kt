package `in`.oncash.oncash.Step

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import ernestoyaquello.com.verticalstepperform.Step
import `in`.oncash.oncash.R

class installStep(stepTitle: String) : Step<String>(stepTitle) {

    private lateinit var installText: TextView

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun createStepContentLayout(): View {
        val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.custom_step, null)
        /*installText = customView.findViewById(R.id.install_text)*/

        installText.text = "Install App."

        return customView
    }

    override fun isStepDataValid(stepData: String?): IsDataValid {
        // Since this step displays non-editable text, it is always considered valid
        return IsDataValid(true, "")
    }

    override fun getStepData(): String {
        // In this case, you might return some identifier or key related to this step, but it's not editable text.
        return "Installation Completed"
    }

    override fun getStepDataAsHumanReadableString(): String {
        // Return a description of the step for display purposes
        return "Install App"
    }

    override fun onStepOpened(animated: Boolean) {}

    override fun onStepClosed(animated: Boolean) {}

    override fun onStepMarkedAsCompleted(animated: Boolean) {}

    override fun onStepMarkedAsUncompleted(animated: Boolean) {}

    override fun restoreStepData(stepData: String?) {
        // You can implement this if needed to restore step data, but it's not applicable for non-editable text.
    }

    fun markStepCompleted() {
        markAsCompleted(true)
    }
}
