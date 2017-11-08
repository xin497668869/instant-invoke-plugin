package component

import base.SettingProperty
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import com.zeroturnaround.javarebel.conf.RebelPropertyResolver
import com.zeroturnaround.javarebel.idea.plugin.EmbeddedJRebelPath
import com.zeroturnaround.javarebel.idea.plugin.runner.JRebelDebugRunner
import org.zeroturnaround.bundled.org.bouncycastle.crypto.signers.RSADigestSigner
import java.io.File
import java.io.IOException

/**
 *
 * @author linxixin@cvte.com
 */

class InitStartupActivity : StartupActivity {

    companion object {
        private val log = Logger.getInstance(InitStartupActivity::class.java)
        val jrebelLocation = SettingProperty.getAdvanceLocation() + File.separator + "jrebel6" + File.separator + "jrebel.jar"
        init {
            //设置jrebel的javaagent包
            System.setProperty("griffin.jar.location", jrebelLocation)
            log.info("jrebel路径是在:"+SettingProperty.getAdvanceLocation() + File.separator + "jrebel6" + File.separator + "jrebel.jar")
            try {
                RebelPropertyResolver.add("rebel.license", SettingProperty.getAdvanceLocation() + File.separator + "jrebel6" + File.separator + "jrebel_test.lic")
            } catch (e: IOException) {
                log.error("rebel.license设置失败", e)
            }

            //提前加载 RSADigestSigner
            RSADigestSigner::class.java.name
            JRebelDebugRunner::class.java.name
            EmbeddedJRebelPath::class.java.name
        }
    }

    override fun runActivity(project: Project) {

        if (!SettingProperty.isProjectInit(project)) {
            val findClass = JavaPsiFacade.getInstance(project).findClass("org.springframework.context.annotation.ComponentScanAnnotationParser", GlobalSearchScope.allScope(project))
            if (findClass != null) {
                SettingProperty.setHotDeploy(project, true)
                SettingProperty.setXrebel(project, false)
            } else {
                SettingProperty.setXrebel(project, false)
                SettingProperty.setHotDeploy(project, false)
            }
            SettingProperty.setProjectInit(project, true)
        }
    }

}