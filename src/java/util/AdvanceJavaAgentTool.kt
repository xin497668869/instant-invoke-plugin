package util

import base.SettingProperty
import base.SettingProperty.getAdvanceLocation
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import gui.setting.SettingDialog
import java.io.File

/**
 *
 * @author linxixin@cvte.com
 */
object AdvanceJavaAgentTool {

    /**
     * 获取hotdeploy 的javaagent参数
     *
     * @return
     */
    fun getJrebelAgent(): String? {
        return getAdvanceLocation() + File.separator + "jrebel6" + File.separator + "jrebel.jar"
    }

    /**
     * 获取hotdeploy 的javaagent参数
     *
     * @return
     */
    fun getInstantInvokeAgent(project: Project): String? {

        if (SettingProperty.isHotDeploy(project)) {
            val hotDeployFile = File(getAdvanceLocation() + File.separator + SettingProperty.INSTANT_INVOKE_AGENT + File.separator + "instant-invoke-agent.jar")
            if (hotDeployFile.exists()) {
                val availablePort = NetUtils.availablePort
                SettingProperty.setProjectPort(project, availablePort)
                return """ -javaagent:"${hotDeployFile.absolutePath}"="$availablePort" """
//                return """ -javaagent:"G:\workspaces\idea\维护\instant-invoke\instant-invoke-agent\target\instant-invoke-agent-1.0-SNAPSHOT.jar"="$availablePort" """
            } else {
                Messages.showInfoMessage("hot_deploy不存在插件当中,无法使用,  请禁用 " + hotDeployFile.absolutePath, "提示")
                val dialog = SettingDialog(project)
                dialog.pack()
                dialog.isVisible = true
            }
        }
        return null
    }

    fun getXrebelAgent(project: Project): String? {

        if (SettingProperty.isXrebel(project)) {
            val xrebelFile = File(getAdvanceLocation() + File.separator + "xrebel" + File.separator + "xrebel.jar")
            if (xrebelFile.exists()) {
                return """-javaagent:"${xrebelFile.absolutePath}" """
            } else {
                Messages.showInfoMessage("xrebel不存在插件当中, 无法使用, 请禁用 " + xrebelFile.absolutePath, "提示")
                val dialog = SettingDialog(project)
                dialog.pack()
                dialog.isVisible = true
            }
        }
        return null
    }
}
