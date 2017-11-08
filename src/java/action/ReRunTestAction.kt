package action

import base.SettingProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import org.apache.commons.lang3.StringUtils
import vo.MethodVo

/**
 *
 * @author linxixin@cvte.com
 */
class ReRunTestAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent?) {
        val lastRequest = SettingProperty.getLastRequest(e!!.project!!)

        if (StringUtils.isEmpty(lastRequest)) {
            RunTestAction.requestInvoke(ObjectMapper().readValue(lastRequest, MethodVo::class.java), e.project!!)
            Messages.showInfoMessage("第一次还没有发送", "提示")
        }
    }

}