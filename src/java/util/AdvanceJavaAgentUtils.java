package util;

import base.AdvanceSetting;
import base.SettingProperty;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.zeroturnaround.xrebel.sdk.UserLicense;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class AdvanceJavaAgentUtils {

    /**
     * 获取hotdeploy 的javaagent参数
     */
    public static String getJrebelAgent() {
        return SettingProperty.getAdvanceLocation() + File.separator + "jrebel6" + File.separator + "jrebel.jar";
    }

    /**
     * 获取hotdeploy 的javaagent参数
     */
    public static String getInstantInvokeAgent(Project project) {
        String warning = "";
        String paramaters = "";
        for (AdvanceSetting advanceSetting : AdvanceSetting.values()) {

            if (SettingProperty.isAdvanceSet(project, advanceSetting)) {
                if (advanceSetting == AdvanceSetting.INSTANT_INVOKE_AGENT) {
                    File hotDeployFile = new File(SettingProperty.getAdvanceLocation() + File.separator + advanceSetting.getPath());
                    if (hotDeployFile.exists()) {
                        int availablePort = NetUtils.getAvailablePort();
                        SettingProperty.setProjectPort(project, availablePort);
                        paramaters = paramaters + " -javaagent:\"" + hotDeployFile.getAbsolutePath() + "=" + availablePort + "\"";
                    } else {
                        SettingProperty.setAdvanceSet(project, advanceSetting, false);
                        warning = warning + advanceSetting.getDesc() + "插件不存在, 无法使用, 已禁用\n";
                    }
                } else {
                    File advanceFile = new File(SettingProperty.getAdvanceLocation() + File.separator + advanceSetting.getPath());
                    if (advanceFile.exists()) {
                        paramaters = paramaters + " -javaagent:\"" + advanceFile.getAbsolutePath() + "\"";
                    } else {
                        SettingProperty.setAdvanceSet(project, advanceSetting, false);
                        warning = warning + advanceSetting.getDesc() + "插件不存在, 无法使用, 已禁用\n";
                    }
                }
            }
        }
        if (!warning.isEmpty()) {
            String finalWarning = warning;
            ApplicationManager.getApplication()
                              .invokeLater(() -> {
                                  Messages.showInfoMessage(finalWarning, "提示");
                              });
        }

        return paramaters;
    }

    public void createXrebelLic() throws IOException {

        String userHome = System.getProperty("user.home") + "\\.xrebel";
        if (new File(userHome, "xrebel.lic").exists()) {
            return;
        }

        Map<String, Object> crackMsg = new HashMap<String, Object>();
        crackMsg.put("Comment", "xxin");
        crackMsg.put("commercial", "false");
        crackMsg.put("Organization", "anonymous-user");
        crackMsg.put("limited", "false");
        crackMsg.put("enterprise", "false");
        crackMsg.put("Product", "XREBEL");
        crackMsg.put("validFrom", new Date());
        crackMsg.put("version", "1.x");
        crackMsg.put("Name", "xxin");
        crackMsg.put("Email", "497668869@qq.com");
        crackMsg.put("OrderId", "1234567");
        crackMsg.put("uid", "uidddddddddd");
        crackMsg.put("Seats", "1111");
        crackMsg.put("ZeroId", "ZeroIdZeroIdZeroId");
        crackMsg.put("validDays", -1);
        crackMsg.put("Type", "evaluation");
        crackMsg.put("noBanner", "true");
        crackMsg.put("validUntil", new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000));
        crackMsg.put("override", "true");

        UserLicense userLiscense = new UserLicense();
        userLiscense.setLicense(objectToBytes(crackMsg));
        userLiscense.setSignature("".getBytes(StandardCharsets.UTF_8));

        new File(userHome).mkdir();
        FileUtils.writeByteArrayToFile(new File(userHome, "xrebel.lic"), objectToBytes(userLiscense));

    }

    private byte[] objectToBytes(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}
