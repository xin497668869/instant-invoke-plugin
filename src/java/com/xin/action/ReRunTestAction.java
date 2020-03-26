package com.xin.action;

import com.alibaba.fastjson.JSON;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.xin.base.SettingProperty;
import com.xin.util.RequestInvokeUtil;
import com.xin.vo.MethodVo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class ReRunTestAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        String lastRequest = SettingProperty.getLastRequest(project);

        if (StringUtils.isEmpty(lastRequest)) {
            Messages.showInfoMessage("第一次还没有发送", "提示");
        } else {

            RequestInvokeUtil.requestInvoke(JSON.parseObject(lastRequest, MethodVo.class), project);
        }
    }

}
