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
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.zeroturnaround.javarebel.idea.plugin.runner.api.JRebelDebugRunnerCommon;
import com.zeroturnaround.javarebel.idea.plugin.runner.api.RunnerCommonInstances;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.AdvanceJavaAgentUtils;

public class JRebelDebugRunner extends GenericDebuggerRunner {
    public JRebelDebugRunner() {
    }

    @Override
    public SettingsEditor<GenericDebuggerRunnerSettings> getSettingsEditor(Executor executor, RunConfiguration runConfiguration) {
        return this.getDebug().getSettingsEditor(executor, runConfiguration);
    }

    @Override
    public RunContentDescriptor doExecute(@NotNull Project project, @NotNull RunProfileState state, @Nullable RunContentDescriptor runContentDescriptor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        this.getDebug().preDoExecute(state, env);
        RunContentDescriptor rd = super.doExecute(project, state, runContentDescriptor, env);
        this.getDebug().doExecute(rd, project, env);
        return rd;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        this.getDebug().preDoExecute(state, env);
        RunContentDescriptor rd = super.doExecute(state, env);
        this.getDebug().doExecute(rd, env.getProject(), env);
        return rd;
    }

    private JRebelDebugRunnerCommon getDebug() {
        return RunnerCommonInstances.debug();
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return this.getDebug().getRunnerId(super.getRunnerId());
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile runProfile) {
        return executorId.equals("JRebel Debug") || executorId.equals("JRebel Executor");
    }

    @Override
    public void checkConfiguration(RunnerSettings settings, ConfigurationPerRunnerSettings configurationPerRunnerSettings) {
        this.getDebug().checkConfiguration(settings, configurationPerRunnerSettings);
    }

    @Override
    public void patch(JavaParameters javaParameters, RunnerSettings runnerSettings, RunProfile profile, boolean beforeExecution) throws ExecutionException {
        super.patch(javaParameters, runnerSettings, profile, beforeExecution);

        this.getDebug().patch(javaParameters, profile);
    }

    public void patchInstantInvokePlugin(JavaParameters var1, Project var3) {

        String hotDeployParam = AdvanceJavaAgentUtils.getInstantInvokeAgent(var3);
        if (hotDeployParam != null) {
            var1.getVMParametersList().addParametersString(hotDeployParam);
        }

        var1.getVMParametersList()
            .addParametersString("-Dmyproject.root=\"" + var3.getBasePath() + "\"");
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment env, @Nullable Callback callback) throws ExecutionException {
        boolean execute = this.getDebug()
                              .execute(env);
        if (execute) {
            super.execute(env, callback);
        }
    }

    @Override
    protected RunContentDescriptor createContentDescriptor(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        RunContentDescriptor descriptor = this.getDebug().createContentDescriptor(state, environment);
        if (state instanceof JavaCommandLine) {
            JavaParameters var7 = ((JavaCommandLine) state).getJavaParameters();
            patchInstantInvokePlugin(var7, environment.getProject());
        }
        return descriptor != null ? descriptor : super.createContentDescriptor(state, environment);
    }
}
