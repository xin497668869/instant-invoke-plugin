package action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import gui.setting.SettingDialog

/**
 *
 * @author linxixin@cvte.com
 */
class SettingAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent?) {
        SettingDialog(e!!.project).apply {
            pack()
            isVisible = true
        }

    }

}