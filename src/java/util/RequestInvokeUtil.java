package util;

import base.AdvanceSetting;
import base.SettingProperty;
import com.alibaba.fastjson.JSON;
import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.debugger.settings.DebuggerSettings;
import com.intellij.debugger.ui.HotSwapStatusListener;
import com.intellij.debugger.ui.HotSwapUI;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import component.InitStartupActivity;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import vo.MethodVo;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class RequestInvokeUtil {

    private static Logger log = Logger.getInstance(RequestInvokeUtil.class);

    public static void requestInvoke(MethodVo methodVo, Project project) {

        if (project == null) {
            throw new BaseException("project 没找到");
        }

        int port = SettingProperty.getProjectPort(project);
        if (port < 0) {
            throw new BaseException("项目端口没开,拿不到项目的端口!");
        }

        if (!SettingProperty.isAdvanceSet(project, AdvanceSetting.INSTANT_INVOKE_AGENT)) {
            startThreadToInvoke(methodVo, port, project);
            return;
        }

        if (InitStartupActivity.isFileChange()) {
            FileDocumentManager.getInstance()
                               .saveAllDocuments();
            InitStartupActivity.setFileChange(false);
            DebuggerManagerEx debuggerManager = DebuggerManagerEx.getInstanceEx(project);
            DebuggerSession session = debuggerManager.getContext()
                                                     .getDebuggerSession();

            if (session != null && session.isAttached()) {
                HotSwapUI.getInstance(project)
                         .reloadChangedClasses(session, DebuggerSettings.getInstance().COMPILE_BEFORE_HOTSWAP,
                                               new HotSwapStatusListener() {
                                                   @Override
                                                   public void onSuccess(List<DebuggerSession> sessions) {
                                                       ApplicationManager.getApplication()
                                                                         .runReadAction(
                                                                                 () -> startThreadToInvoke(methodVo, port, project));
                                                   }
                                               });
            }
        } else {
            startThreadToInvoke(methodVo, port, project);
        }

    }

    private static void startThreadToInvoke(@NotNull MethodVo methodVo, int port, Project project) {
        try {
            SettingProperty.setLastRequest(project, JSON.toJSONString(methodVo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                httpPost(methodVo, port);
            } catch (Exception e) {
                log.error("出现异常");
                ApplicationManager.getApplication()
                                  .invokeLater(() -> Messages.showErrorDialog(e.getMessage(), "异常提示"));
            }
        }).start();
    }

    private static void httpPost(@NotNull MethodVo methodVo, int port) throws IOException {
        try {
            URL url = new URL("http://localhost:" + port + "/instant-invoke/invoke");
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            String content = "{\"className\":\"" + methodVo.getClassName() + "\",\"methodName\":\"" + methodVo.getMethodName() + "\"}";
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(60 * 60 * 1000);
            connection.setConnectTimeout(60 * 60 * 1000);
            connection.setRequestProperty("Connection", "close");
            connection.setRequestProperty("Content-Length", String.valueOf(content.length()));
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.connect();
            IOUtils.write(content, connection.getOutputStream());
            IOUtils.toString(connection.getInputStream());
        } catch (ConnectException e) {
            log.error(e);
            throw new RuntimeException("连接失败, 是不是项目还没启动完或者没开热部署配置?");
        } catch (SocketTimeoutException e) {
            log.error(e);
            throw new RuntimeException("socket超时, 是不是debug超过60分钟了?");
        }
    }
}
