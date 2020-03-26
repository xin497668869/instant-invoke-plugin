package com.xin.component;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.xin.base.SettingProperty;
import com.zeroturnaround.javarebel.idea.plugin.EmbeddedAgents;
import com.zeroturnaround.javarebel.idea.plugin.runner.JRebelDebugRunner;
import org.jetbrains.annotations.NotNull;
import org.zeroturnaround.jrebel.client.config.JRebelConfiguration;
import org.zeroturnaround.jrebel.client.config.LogLevel;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class InitStartupActivity implements StartupActivity {
    public volatile static boolean isChange = false;
    private static Logger log = Logger.getInstance(InitStartupActivity.class);
    private static String jrebelLocation;
    private static String xrebelLocation;

    static {

        jrebelLocation = SettingProperty.getAdvanceLocation() + File.separator + "jrebel6" + File.separator + "jrebel.jar";
        xrebelLocation = SettingProperty.getAdvanceLocation() + File.separator + "xrebel" + File.separator + "xrebel.jar";

        JRebelDebugRunner.class.getName();
        EmbeddedAgents.class.getName();
    }

    public static void initProperty(Properties properties) {
        //设置jrebel的javaagent包
        System.setProperty("griffin.jar.location", jrebelLocation);
        log.info("jrebel路径是在:" + SettingProperty.getAdvanceLocation() + File.separator + "jrebel6" + File.separator + "jrebel.jar");
        properties.setProperty("rebel.license",
                               SettingProperty.getAdvanceLocation() + File.separator + "jrebel6" + File.separator + "jrebel_test.lic");

        properties.setProperty("rebel.usage_reporting",
                               "false");
        properties.setProperty("rebel.license",
                               SettingProperty.getAdvanceLocation() + File.separator + "jrebel6" + File.separator + "jrebel_test.lic");

        if (properties.getProperty("rebel.com.xin.log") == null) {
            properties.setProperty("rebel.com.xin.log", "error");
        }
    }

    public static String getXrebelLocation() {
        return xrebelLocation;
    }

    public static String getJrebelLocation() {
        return jrebelLocation;
    }

    public static boolean isFileChange() {
        return FileDocumentManager.getInstance()
                                  .getUnsavedDocuments().length > 0 || isChange;
    }

    public static void setFileChange(boolean setFileChange) {
        isChange = setFileChange;
    }

    @Override
    public void runActivity(@NotNull Project project) {
        JRebelConfiguration.getDefault()
                           .logLevel()
                           .setValue(LogLevel.OFF);
        project.getMessageBus()
               .connect()
               .subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
                   @Override
                   public void before(@NotNull List<? extends VFileEvent> events) {
                       isChange = true;
                   }

               });
    }

    /**
     * 自动插入rebel.xml文件以支持tomcat启动的时候能进行热部署
     */
//        project.messageBus.connect().subscribe(BuildManagerListener.TOPIC, object : BuildManagerListener {
//
//            override fun buildFinished(project: Project, sessionId: UUID, isAutomake: Boolean) {
//                super.buildFinished(project, sessionId, isAutomake)
//                val modules = ModuleManager.getInstance(project).modules
//                for (module in modules) {
//                    val moduleOutputDirectory = CompilerPaths.getModuleOutputPath(module, false)
//                    if (moduleOutputDirectory != null) {
//                        val rebelXmlUtils = RebelXmlUtils(module)
//                        val generateRebelXmlContents = rebelXmlUtils.javaClass.getDeclaredMethod("generateRebelXmlContentsIde")
//                        generateRebelXmlContents.isAccessible = true
//                        val rebelContent = generateRebelXmlContents.invoke(rebelXmlUtils)
//                        val content = (rebelContent as String).replace(project.basePath!!, "\${myproject.root}")
//                        val path = File(moduleOutputDirectory)
//
//                        val file = File(path, "rebel.xml")
//                        if (path.exists() && !file.exists()) {
//                            BufferedWriter(OutputStreamWriter(FileOutputStream(file, false), StandardCharsets.UTF_8)).use {
//                                it.write(content)
//                                it.flush()
//                            }
//                        }
//                    }
//                }
//            }
//
//        })
}
