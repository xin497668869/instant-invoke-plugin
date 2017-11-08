package util

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
        Messages.showErrorDialog(e.msg, "提示")
    }catch (e: Exception) {
        Messages.showErrorDialog(e.message, "异常提示")
    }
}