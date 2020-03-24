package util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class TipsUtils {
    public static void catchBaseException(Runnable runnable) {
        try {
            runnable.run();
        } catch (BaseException e) {
            ApplicationManager.getApplication()
                              .invokeLater(() -> Messages.showErrorDialog(e.getMessage(), "提示"));

        } catch (Exception e) {
            ApplicationManager.getApplication()
                              .invokeLater(() -> Messages.showErrorDialog(e.toString(), "异常提示"));
        }
    }
}
