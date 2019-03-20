package base

import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import java.io.File

/**
 *
 * @author linxixin@cvte.com
 */

object SettingProperty {



    private val hotDeploy = "is_hotdeploy"
    private val xrebel = "is_xrebel"
    private val PROJECT_PORT = "project_port"
    val INSTANT_INVOKE_AGENT = "instant-invoke-agent"
    val DELIMITER = "@-@-@"
    private val lastRequest = "lastRequest"
    private val isProjectInit = "isProjectInit "

    fun getLastRequest(project: Project): String {
        return PropertiesComponent.getInstance(project).getValue(lastRequest, "")
    }

    fun setLastRequest(project: Project, value: String) {
        return PropertiesComponent.getInstance(project).setValue(lastRequest, value)
    }

    fun isProjectInit(project: Project): Boolean {
        return PropertiesComponent.getInstance(project).getBoolean(isProjectInit, false)
    }

    fun setProjectInit(project: Project, value: Boolean) {
        return PropertiesComponent.getInstance(project).setValue(isProjectInit, value)
    }

    fun isHotDeploy(project: Project): Boolean {
        return PropertiesComponent.getInstance(project).getBoolean(hotDeploy)
    }

    fun setHotDeploy(project: Project, value: Boolean) {
        PropertiesComponent.getInstance(project).setValue(hotDeploy, value)
    }

    fun isXrebel(project: Project): Boolean {
        return PropertiesComponent.getInstance(project).getBoolean(xrebel)
    }

    fun setXrebel(project: Project, value: Boolean) {
        PropertiesComponent.getInstance(project).setValue(xrebel, value)
    }

    fun setProjectPort(project: Project, port: Int) {
        PropertiesComponent.getInstance(project).setValue(PROJECT_PORT, port, -1)
    }

    fun getProjectPort(project: Project): Int {
        return PropertiesComponent.getInstance(project).getInt(PROJECT_PORT, -1)
    }

    fun getProjectLocation(): String {
        return PluginManager.getPlugin(PluginId.getId("instant-invoke-plugin"))!!.path.absolutePath
    }

    fun getAdvanceLocation(): String {
        return getProjectLocation() + File.separator + "classes" + File.separator + "advance"
    }
}