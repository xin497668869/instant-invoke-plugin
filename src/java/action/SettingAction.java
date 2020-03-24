package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import gui.setting.SettingDialog;
import org.jetbrains.annotations.NotNull;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class SettingAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        SettingDialog settingDialog = new SettingDialog(anActionEvent.getProject());
        settingDialog.pack();
        settingDialog.setVisible(true);
    }
}
