//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zeroturnaround.javarebel.idea.plugin;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import component.InitStartupActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class EmbeddedJRebelPath {
    private static final Logger log = Logger.getInstance("#com.zeroturnaround.javarebel.idea.plugin.EmbeddedJRebelPath");
    private static String cachedEmbeddedLegacyPath;
    private static String cachedEmbeddedGriffinPath;

    public EmbeddedJRebelPath() {
    }

    @Nullable
    public static synchronized String getLegacyAgentPath() {
        log.debug("Entering getLegacyAgentPath with cached value {}", new Object[]{cachedEmbeddedLegacyPath});
        if (cachedEmbeddedLegacyPath == null) {
            log.debug("Nothing in cache for cachedEmbeddedLegacyPath...");

            try {
                String path = System.getProperty("jrebel.jar.location", getJRebelPluginPath() + File.separator + "lib" + File.separator + "jrebel" + File.separator + "jrebel.jar");
                validatePath(path);
                cachedEmbeddedLegacyPath = path;
            } catch (Exception var1) {
                log.error(var1);
            }
        }

        log.debug("Returning embedded path \"{}\"", new Object[]{cachedEmbeddedLegacyPath});
        return cachedEmbeddedLegacyPath;
    }

    @Nullable
    public static synchronized String getGriffinAgentPath() {
        log.debug("Entering getGriffinAgentPath with cached value {}", new Object[]{cachedEmbeddedGriffinPath});
        if (cachedEmbeddedGriffinPath == null) {
            log.debug("Nothing in cache for cachedEmbeddedGriffinPath...");

            try {
                String path = InitStartupActivity.Companion.getJrebelLocation();
                cachedEmbeddedGriffinPath = path;
            } catch (Exception var1) {
                log.error(var1);
                cachedEmbeddedGriffinPath = getLegacyAgentPath();
            }
        }

        log.debug("Returning Griffin path \"{}\"", new Object[]{cachedEmbeddedGriffinPath});
        return cachedEmbeddedGriffinPath;
    }

    private static void validatePath(@NotNull String path) {
        log.debug("Validating path: \"{}\"", new Object[]{path});
        File file = new File(path);
        if (file.exists() && !file.isDirectory()) {
            log.debug("Validation succeeded! ");
        } else {
            throw new IllegalArgumentException("Invalid path for jrebel.jar: " + path);
        }
    }

    @NotNull
    private static String getJRebelPluginPath() {
        IdeaPluginDescriptor pluginDescriptor = JRebelIdeaPlugin.pluginDescriptor();
        if (pluginDescriptor == null) {
            log.error("getJRebelPluginPath() - PluginManager.getPlugin() could not find plugin");
            throw new RuntimeException("PluginManager.getPlugin() could not find plugin");
        } else {
            File jrebelPath = pluginDescriptor.getPath();
            log.debug("getJRebelPluginPath found: \"{}\"", new Object[]{jrebelPath});
            return jrebelPath.getAbsolutePath();
        }
    }
}
