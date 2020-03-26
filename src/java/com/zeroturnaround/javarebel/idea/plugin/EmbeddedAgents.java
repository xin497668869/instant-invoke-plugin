//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zeroturnaround.javarebel.idea.plugin;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.xin.component.InitStartupActivity;
import com.zeroturnaround.javarebel.conf.RebelConf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeroturnaround.jrebel.client.logger.Log;

import java.io.File;

public class EmbeddedAgents {
    private static final Log log = Log.getInstance(EmbeddedAgents.class);
    public static final String PROPERTY_JREBEL_JAR_PATH = "griffin.jar.location";
    public static final String PROPERTY_XREBEL_JAR_PATH = "xrebel.jar.path";
    private static String cachedEmbeddedGriffinPath;

    public EmbeddedAgents() {
    }

    public static File getXRebelAgentPath() {
        String overridePath = RebelConf.getProperty("xrebel.jar.path");
        File xrebelJar;
        if (overridePath != null) {
            xrebelJar = new File(overridePath);
        } else {
            xrebelJar = new File(InitStartupActivity.getXrebelLocation());
        }

        if (xrebelJar.exists() && xrebelJar.isFile()) {
            return xrebelJar;
        } else {
            throw new IllegalArgumentException("xrebel.jar path incorrect or not a file: '" + xrebelJar + "'");
        }
    }

    @Nullable
    public static synchronized String getGriffinAgentPath() {
        log.debug("Entering getGriffinAgentPath with cached value {}", cachedEmbeddedGriffinPath);
        if (cachedEmbeddedGriffinPath == null) {
            log.debug("Nothing in cache for cachedEmbeddedGriffinPath...");

            try {
                String path = InitStartupActivity.getJrebelLocation();
                validatePath(path);
                cachedEmbeddedGriffinPath = path;
            } catch (Exception var1) {
                log.error("Griffin path not found", var1);
            }
        }

        log.debug("Returning Griffin path \"{}\"", cachedEmbeddedGriffinPath);
        return cachedEmbeddedGriffinPath;
    }

    private static void validatePath(@NotNull String path) {

        log.debug("Validating path: \"{}\"", path);
        File file = new File(path);
        if (file.exists() && !file.isDirectory()) {
            log.debug("Validation succeeded! ");
        } else {
            throw new IllegalArgumentException("Invalid path for jrebel.jar: " + path);
        }
    }

    static class PluginHomeHolder {
        static File pluginHome = getPluginHome();

        PluginHomeHolder() {
        }

        private static File getPluginHome() {
            IdeaPluginDescriptor pluginDescriptor = JRebelIdeaPlugin.pluginDescriptor();
            if (pluginDescriptor == null) {
                throw new NullPointerException("PluginManager.getPlugin() could not find plugin");
            } else {
                File path = pluginDescriptor.getPath();
                if (path == null) {
                    throw new NullPointerException("Plugin path is null");
                } else {
                    EmbeddedAgents.log.debug("Using plugin home path '{}'", path);
                    return path.getAbsoluteFile();
                }
            }
        }
    }
}
