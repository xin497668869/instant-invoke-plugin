package util;

import base.SettingProperty;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import vo.BaseException;
import vo.MethodVo;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class RequestInvokeUtil {

    private static CloseableHttpClient httpClient = HttpClientBuilder.create()
                                                                     .build();
    private static Logger log = Logger.getInstance(RequestInvokeUtil.class);

    public static void requestInvoke(@NotNull MethodVo methodVo, @NotNull Project project) {

        int port = SettingProperty.INSTANCE.getProjectPort(project);
        if (port < 0) {
            throw new BaseException("项目端口没开,拿不到项目的端口!");
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
            String response = IOUtils.toString(connection.getInputStream(), "UTF-8");
        } catch (ConnectException e) {
            log.error(e);
            throw new RuntimeException("连接失败, 是不是项目还没启动完或者没开热部署配置?");
        } catch (SocketTimeoutException e) {
            log.error(e);
            throw new RuntimeException("socket超时, 是不是debug超过60分钟了?");
        }
    }
}
