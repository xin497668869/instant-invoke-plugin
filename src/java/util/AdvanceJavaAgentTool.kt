package util

import base.SettingProperty
import base.SettingProperty.getAdvanceLocation
import com.google.common.io.Files
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectOutputStream
import java.util.*
import kotlin.collections.HashMap

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
                SettingProperty.setHotDeploy(project, false)
                ApplicationManager.getApplication().invokeLater {
                    Messages.showInfoMessage("hot_deploy不存在插件当中,无法使用,  已禁用 " + hotDeployFile.absolutePath, "提示")
                }
            }
        }
        return null
    }

    fun createXrebelLic() {

        val userHome = System.getProperty("user.home")+"\\.xrebel"
        if (File(userHome, "xrebel.lic").exists()) {
            return
        }

        val crackMsg = HashMap<String, Any>()
        crackMsg.put("Comment", "xxin")
        crackMsg.put("commercial", "false")
        crackMsg.put("Organization", "anonymous-user")
        crackMsg.put("limited", "false")
        crackMsg.put("enterprise", "false")
        crackMsg.put("Product", "XREBEL")
        crackMsg.put("validFrom", Date())
        crackMsg.put("version", "1.x")
        crackMsg.put("Name", "xxin")
        crackMsg.put("Email", "497668869@qq.com")
        crackMsg.put("OrderId", "1234567")
        crackMsg.put("uid", "uidddddddddd")
        crackMsg.put("Seats", "1111")
        crackMsg.put("ZeroId", "ZeroIdZeroIdZeroId")
        crackMsg.put("validDays", -1)
        crackMsg.put("Type", "evaluation")
        crackMsg.put("noBanner", "true")
        crackMsg.put("validUntil", Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000))
        crackMsg.put("override", "true")

        val licenseMap = HashMap<String, Any?>()
        licenseMap.put("license", objectToBytes(crackMsg))
        licenseMap.put("signature", "".toByteArray())
        File(userHome).mkdir()
        Files.write(objectToBytes(licenseMap), File(userHome, "xrebel.lic"))
    }


    private fun objectToBytes(any: Any): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteArrayOutputStream).use {
            it.writeObject(any)
            it.flush()
        }
        return byteArrayOutputStream.toByteArray()
    }


    fun getXrebelAgent(project: Project): String? {

        if (SettingProperty.isXrebel(project)) {
            val xrebelFile = File(getAdvanceLocation() + File.separator + "xrebel" + File.separator + "xrebel.jar")
            if (xrebelFile.exists()) {
                createXrebelLic()
                return """-javaagent:"${xrebelFile.absolutePath}" """
            } else {
                SettingProperty.setXrebel(project, false)
                ApplicationManager.getApplication().invokeLater {
                    Messages.showInfoMessage("xrebel不存在插件当中, 无法使用, 已禁用 " + xrebelFile.absolutePath, "提示")
                }
            }
        }
        return null
    }
}
