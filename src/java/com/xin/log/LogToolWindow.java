package com.xin.log;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class LogToolWindow implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

//        Splitter splitter = new Splitter(false);
//        splitter.setFirstComponent(ScrollPaneFactory.createScrollPane(RequestTreeBuild.build()));
//        BuildTextConsoleView component = new BuildTextConsoleView(project);
//
//        splitter.setSecondComponent(component.getComponent());
//        Content issuesContent = toolWindow.getContentManager()
//                                          .getFactory()
//                                          .createContent(
//                                                  splitter,
//                                                  "日志",
//                                                  false);
//        toolWindow.getContentManager()
//                  .addContent(issuesContent);
    }

    @Override
    public void init(ToolWindow window) {

    }
}
