package util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import vo.BaseException

/**
 *
 * @author linxixin@cvte.com
 */

fun catchBaseException(tipShow: () -> Unit){
    try {
        tipShow()
    } catch (e: BaseException) {
        ApplicationManager.getApplication().invokeLater {
            Messages.showErrorDialog(e.msg, "提示")
        }

    }catch (e: Exception) {
        ApplicationManager.getApplication().invokeLater {
            Messages.showErrorDialog(e.message, "异常提示")
        }
    }
}