//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zeroturnaround.javarebel.idea.plugin.runner;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.debugger.impl.GenericDebuggerRunnerSettings;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.AdvanceJavaAgentTool;


public class JRebelDebugRunner extends GenericDebuggerRunner {
    public JRebelDebugRunner() {
    }
    @Override
    public SettingsEditor<GenericDebuggerRunnerSettings> getSettingsEditor(Executor var1, RunConfiguration var2) {
        return JRebelDebugRunnerCommon.getSettingsEditor(var1, var2);
    }

    public RunContentDescriptor doExecute(@NotNull Project var1, @NotNull RunProfileState var2, @Nullable RunContentDescriptor var3, @NotNull ExecutionEnvironment var4) throws ExecutionException {
        JRebelDebugRunnerCommon.preDoExecute(var2, var4);
        RunContentDescriptor var5 = super.doExecute(var1, var2, var3, var4);
        JRebelDebugRunnerCommon.doExecute(var5, var1, var4);
        return var5;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState var1, @NotNull ExecutionEnvironment env) throws ExecutionException {
        JRebelDebugRunnerCommon.preDoExecute(var1, env);
        if (var1 instanceof JavaCommandLine) {
            ParametersList javaParameters = ((JavaCommandLine) var1).getJavaParameters().getVMParametersList();
            String hotDeployParam = AdvanceJavaAgentTool.INSTANCE.getHotDeployParam(env.getProject());
            if (hotDeployParam != null) {
                javaParameters.addParametersString(hotDeployParam);
            }
            String xrebelParam = AdvanceJavaAgentTool.INSTANCE.getXrebelParam(env.getProject());
            if (xrebelParam != null) {
                javaParameters.addParametersString(xrebelParam);
            }
        }

        RunContentDescriptor var3 = super.doExecute(var1, env);
        JRebelDebugRunnerCommon.doExecute(var3, env.getProject(), env);
        return var3;
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return JRebelDebugRunnerCommon.getRunnerId(super.getRunnerId());
    }

    @Override
    public boolean canRun(@NotNull String var1, @NotNull RunProfile var2) {
        return JRebelDebugRunnerCommon.canRun(var1, super.getRunnerId(), var2);
    }

    @Override
    public void checkConfiguration(RunnerSettings var1, ConfigurationPerRunnerSettings var2) throws RuntimeConfigurationException {
        JRebelDebugRunnerCommon.checkConfiguration(var1, var2);
    }

    @Override
    public void patch(JavaParameters var1, RunnerSettings var2, RunProfile var3, boolean var4) throws ExecutionException {
        super.patch(var1, var2, var3, var4);
        JRebelDebugRunnerCommon.patch(var1, var3);
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment var1, @Nullable Callback var2) throws ExecutionException {
        boolean var3 = JRebelDebugRunnerCommon.execute(var1, var2);
        if (var3) {
            super.execute(var1, var2);
        }
    }

    @Override
    protected RunContentDescriptor createContentDescriptor(@NotNull RunProfileState var1, @NotNull ExecutionEnvironment var2) throws ExecutionException {
        RunContentDescriptor var3 = JRebelDebugRunnerCommon.createContentDescriptor(var1, var2);
        return var3 != null ? var3 : super.createContentDescriptor(var1, var2);
    }
}