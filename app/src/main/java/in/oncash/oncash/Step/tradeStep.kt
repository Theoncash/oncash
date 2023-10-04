package `in`.oncash.oncash.Step

import android.view.View
import android.widget.TextView
import ernestoyaquello.com.verticalstepperform.Step

class tradeStep(stepTitle: String) : Step<String>(stepTitle) {

    private lateinit var installText: TextView

    override fun createStepContentLayout(): View {
        installText = TextView(context)
        installText.text = "Complete a trade in the app to get your reward "

        return installText
    }

    override fun isStepDataValid(stepData: String?): IsDataValid {
        // Since this step displays non-editable text, it is always considered valid
        return IsDataValid(true, "")
    }

    override fun getStepData(): String {
        // In this case, you might return some identifier or key related to this step, but it's not editable text.
        return "InstallationStepCompleted"
    }

    override fun getStepDataAsHumanReadableString(): String {
        // Return a description of the step for display purposes
        return "Make 1st Trade"
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
