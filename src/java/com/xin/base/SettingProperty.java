package com.xin.base;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class SettingProperty {

    private static String DELIMITER = "@-@-@";
    private static String lastRequest = "lastRequest";
    private static String isProjectInit = "isProjectInit ";
    private static Map<Project, Integer> projectPortMap = new ConcurrentHashMap<>();

    public static String getLastRequest(Project project) {
        return PropertiesComponent.getInstance(project)
                                  .getValue(lastRequest, "");
    }

    public static void setLastRequest(Project project, String value) {
        PropertiesComponent.getInstance(project)
                           .setValue(lastRequest, value);
    }

    public static boolean isProjectInit(Project project) {
        return PropertiesComponent.getInstance(project)
                                  .getBoolean(isProjectInit, false);
    }

    public static void setProjectInit(Project project, Boolean value) {
        PropertiesComponent.getInstance(project)
                           .setValue(isProjectInit, value);
    }

    public static Boolean isAdvanceSet(Project project, AdvanceSetting advance) {
        if (!PropertiesComponent.getInstance(project)
                                .isValueSet(advance.name())) {
            PropertiesComponent.getInstance(project)
                               .setValue(advance.name(), true);
        }
        return PropertiesComponent.getInstance(project)
                                  .getBoolean(advance.name());
    }

    public static void setAdvanceSet(Project project, AdvanceSetting advance, Boolean value) {
        PropertiesComponent.getInstance(project)
                           .setValue(advance.name(), value.toString());
    }

    public static void setProjectPort(Project project, int port) {
        projectPortMap.put(project, port);
    }

    public static int getProjectPort(Project project) {
        return projectPortMap.getOrDefault(project, -1);
    }

    public static String getProjectLocation() {
        return PluginManagerCore.getPlugin(PluginId.getId("instant-invoke-plugin"))
                                .getPath()
                                .getAbsolutePath();
    }

    public static String getAdvanceLocation() {
        return getProjectLocation() + File.separator + "classes" + File.separator + "advance";
    }
}
