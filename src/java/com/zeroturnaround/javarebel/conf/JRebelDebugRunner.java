//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.zeroturnaround.javarebel.conf;

import com.intellij.debugger.impl.GenericDebuggerParametersRunnerConfigurable;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.debugger.impl.GenericDebuggerRunnerSettings;
import com.intellij.debugger.settings.DebuggerSettings;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.RunnerRegistry;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationWithRunnerSettings;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemTaskDebugRunner;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.util.text.VersionComparatorUtil;
import com.xin.util.AdvanceJavaAgentUtils;
import com.zeroturnaround.javarebel.idea.plugin.debugger.AsyncDebuggerWarning;
import com.zeroturnaround.javarebel.idea.plugin.deprecation.NeededUntil;
import com.zeroturnaround.javarebel.idea.plugin.deprecation.TestedIntelliJVersions;
import com.zeroturnaround.javarebel.idea.plugin.runner.AgentSelection;
import com.zeroturnaround.javarebel.idea.plugin.runner.JRebelDebugExecutor;
import com.zeroturnaround.javarebel.idea.plugin.runner.JRebelRunUtils;
import com.zeroturnaround.javarebel.idea.plugin.runner.JRebelRunnerCommon;
import com.zeroturnaround.javarebel.idea.plugin.runner.RebelAgentSelectionManager;
import com.zeroturnaround.javarebel.idea.plugin.runner.RebelXmlCheck;
import com.zeroturnaround.javarebel.idea.plugin.util.IntellijVersionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeroturnaround.jrebel.client.logger.Log;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JRebelDebugRunner extends GenericDebuggerRunner implements JRebelRunnerCommon {
    private static final Log log = Log.getInstance(JRebelDebugRunner.class);
    private static final String INSTRUMENTING_AGENT_FIELD = "INSTRUMENTING_AGENT";
    private static final String LIFERAY_CONFIG_CLASS = "com.liferay.ide.idea.server.LiferayServerConfiguration";
    private static final List<String> GRAIL_APP_RUNNERS = Arrays.asList("org.jetbrains.plugins.grails.runner.impl.GrailsCommandLineState",
                                                                        "org.jetbrains.plugins.grails.runner.impl.GrailsTestCommandLineState",
                                                                        "org.jetbrains.plugins.grails.runner.impl.GrailsRunAppCommandLineState");
    private static final AtomicBoolean shouldTryGrails = new AtomicBoolean(true);
    private static final AtomicBoolean shouldTryGradle = new AtomicBoolean(true);

    public JRebelDebugRunner() {
    }

    public void patch(JavaParameters javaParameters, RunnerSettings settings, RunProfile runProfile, boolean beforeExecution) throws
                                                                                                                              ExecutionException {
        super.patch(javaParameters, settings, runProfile, beforeExecution);
        this.patchCommon(JRebelDebugExecutor.getInstance(), runProfile, javaParameters);
    }

    public SettingsEditor<GenericDebuggerRunnerSettings> getSettingsEditor(Executor executor, RunConfiguration runConfiguration) {
        SettingsEditor<GenericDebuggerRunnerSettings> result = null;
        if (runConfiguration instanceof RunConfigurationWithRunnerSettings && ((RunConfigurationWithRunnerSettings) runConfiguration).isSettingsNeeded()) {
            result = new GenericDebuggerParametersRunnerConfigurable(runConfiguration.getProject());
        }

        return result;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {

        JRebelRunUtils.fixWebSphereServerXml(env.getRunProfile());

        RunContentDescriptor var3;
        try {
            var3 = isLiferay(env) ? this.doExecuteLiferay(state, env) : super.doExecute(state, env);
        } finally {
            RebelXmlCheck.checkRebelXmlAndDisplayNotification(env);
        }

        return var3;
    }

    @NotNull
    @Override
    public String getRunnerId() {
        if (fakeRunnerId()) {
            return super.getRunnerId();
        } else {
            return "JRebel Debug";
        }
    }

    private static boolean fakeRunnerId() {
        StackTraceElement[] stack = Thread.currentThread()
                                          .getStackTrace();

        for (int i = 0; i < stack.length && i < 8; ++i) {
            if ("isDebug".equals(stack[i].getMethodName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile runProfile) {

        return this.canRunCommon(executorId, runProfile);
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment env) throws ExecutionException {

        AgentSelection jxr = RebelAgentSelectionManager.getSelection(env.getProject());
        if (!jxr.isJRebelEnabled() || AsyncDebuggerWarning.checkAndWarn(env.getProject())) {
            boolean execute = this.executeCommon(env);
            if (execute) {
                super.execute(env);
            }

        }
    }

    @Override
    public RunContentDescriptor createContentDescriptor(RunProfileState state, ExecutionEnvironment environment) throws ExecutionException {
        RunContentDescriptor descriptor;
        if (shouldTryGrails.get() && isGrailsApp(state)) {
            descriptor = createContentDescriptorForGrails(state, environment);
            if (descriptor != null) {
                return descriptor;
            }
        }

        if (shouldTryGradle.get() && isRunnableState(state)) {
            descriptor = createContentDescriptorForGradle(state, environment);
            if (descriptor != null) {
                return descriptor;
            }
        }
        if (state instanceof JavaCommandLine) {
            JavaParameters var7 = ((JavaCommandLine) state).getJavaParameters();
            patchInstantInvokePlugin(var7, environment.getProject());
        }
        return super.createContentDescriptor(state, environment);
    }

    @Nullable
    private static RunContentDescriptor createContentDescriptorForGradle(RunProfileState state, ExecutionEnvironment environment) {
        ExternalSystemTaskDebugRunner runner = new ExternalSystemTaskDebugRunner();

        try {
            RunContentDescriptor descriptor = createContentDescriptor(runner, state, environment, true);
            if (descriptor != null) {
                log.debug("Using externalSystemRunConf to create contentDescriptor for {}", state);
                return descriptor;
            }
        } catch (Exception var4) {
            shouldTryGradle.set(false);
            log.error("Failed to run gradle debugger", var4);
        }

        return null;
    }

    @Nullable
    private static RunContentDescriptor createContentDescriptorForGrails(RunProfileState state, ExecutionEnvironment environment) {
        if (isGrailsApp(state)) {
            log.info("{} is Grails RunProfileState, calling Grails*DebuggerRunner to attach debugger", state);

            try {
                RunProfile profile = environment.getRunProfile();
                RunnerRegistry runnerRegistry = RunnerRegistry.getInstance();
                String[] grailsDebuggerRunners = new String[]{"org.jetbrains.plugins.grails.runner.Grails3DebuggerRunner", "org.jetbrains.plugins.grails.runner.GrailsDebuggerRunner", "GrailsDebuggerRunner"};
                String[] var5 = grailsDebuggerRunners;
                int var6 = grailsDebuggerRunners.length;

                for (int var7 = 0; var7 < var6; ++var7) {
                    String runnerId = var5[var7];
                    ProgramRunner grailsRunner = runnerRegistry.findRunnerById(runnerId);
                    if (grailsRunner != null && grailsRunner.canRun(DefaultDebugExecutor.EXECUTOR_ID, profile)) {
                        log.debug("{} can run with {}", profile, runnerId);
                        addGrailsDebugArgumentIfNeeded(state);
                        return createContentDescriptor(grailsRunner, state, environment, false);
                    }
                }

                log.error("Seems to have been Grails' RunProfile, but didn't find handler");
                return null;
            } catch (Exception var10) {
                log.error("Failed to run grails debugger", var10);
                shouldTryGrails.set(false);
                return null;
            }
        } else {
            log.error("RunProfileState should be instance of GrailsCommandLineState, but was {}", state);
            return null;
        }
    }

    private static RunContentDescriptor createContentDescriptor(ProgramRunner runner, RunProfileState state,
                                                                ExecutionEnvironment environment, boolean allowNull) throws Exception {
        Method createContentDescriptorMethod = runner.getClass()
                                                     .getDeclaredMethod("createContentDescriptor", RunProfileState.class,
                                                                        ExecutionEnvironment.class);
        createContentDescriptorMethod.setAccessible(true);
        RunContentDescriptor result = (RunContentDescriptor) createContentDescriptorMethod.invoke(runner, state, environment);
        if (!allowNull && result == null) {
            throw new IllegalStateException("RunContentDescriptor is null");
        } else {
            return result;
        }
    }

    private static boolean isRunnableState(RunProfileState state) {
        String runnableName = "com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunnableState";
        String oldRunnableName = "com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration$MyRunnableState";
        String clazzName = IntellijVersionUtil.isIntellij193_4099_13OrNewer() ? runnableName : oldRunnableName;
        boolean isInstance = false;

        try {
            Class<?> clazz = ExternalSystemRunConfiguration.class.getClassLoader()
                                                                 .loadClass(clazzName);
            isInstance = clazz.isInstance(state);
        } catch (ClassNotFoundException var6) {
            log.warn("Could not load class " + clazzName, var6);
        }

        return isInstance;
    }

    private static boolean isGrailsApp(RunProfileState state) {
        return GRAIL_APP_RUNNERS.contains(state.getClass()
                                               .getName());
    }

    private static void addGrailsDebugArgumentIfNeeded(RunProfileState state) throws ExecutionException {
        if (needsGrailsDebugArgument(state)) {
            if (state instanceof JavaCommandLine) {
                ParametersList programParametersList = ((JavaCommandLine) state).getJavaParameters()
                                                                                .getProgramParametersList();
                String parameter = "--debug-jvm";
                if (!programParametersList.hasParameter(parameter)) {
                    programParametersList.add(parameter);
                    log.debug("Added '{}' to command parameters", parameter);
                }
            } else {
                log.warn("'{}' is not an instance of JavaCommandLine. Grails debugger argument modification not possible.",
                         state.getClass());
            }
        }

    }

    private static boolean needsGrailsDebugArgument(RunProfileState state) {
        try {
            if (!Registry.is("grails.simple.debug") && isAssignableFrom(state,
                                                                        "org.jetbrains.plugins.grails.runner.impl.GrailsCommandLineState")) {
                Object application = state.getClass()
                                          .getMethod("getApplication")
                                          .invoke(state);
                if (isAssignableFrom(application, "org.jetbrains.plugins.grails.structure.Grails3Application")) {
                    Object version = application.getClass()
                                                .getMethod("getGrailsVersion")
                                                .invoke(application);
                    if (VersionComparatorUtil.compare(version.toString(), "3.1.5") < 0) {
                        return true;
                    }
                }
            }
        } catch (Exception var3) {
            log.info("Failed to execute method 'needsGrailsDebugArgument': ", var3);
        }

        return false;
    }

    private static boolean isAssignableFrom(Object object, String className) throws ClassNotFoundException {
        return object.getClass()
                     .getClassLoader()
                     .loadClass(className)
                     .isAssignableFrom(object.getClass());
    }

    private static boolean isLiferay(@NotNull ExecutionEnvironment env) {

        return "com.liferay.ide.idea.server.LiferayServerConfiguration".equals(env.getRunProfile()
                                                                                  .getClass()
                                                                                  .getName());
    }

    private RunContentDescriptor doExecuteLiferay(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws
                                                                                                                     ExecutionException {

        DebuggerSettings debuggerSettings = DebuggerSettings.getInstance();
        Boolean asyncEnabledState = this.get(debuggerSettings);

        RunContentDescriptor var5;
        try {
            if (asyncEnabledState != null) {
                this.set(debuggerSettings, false);
            }

            var5 = super.doExecute(state, env);
        } finally {
            this.set(debuggerSettings, asyncEnabledState);
        }

        return var5;
    }

    @NeededUntil(
            version = TestedIntelliJVersions.IC_2017_1,
            comment = "Field added in 2017.1"
    )
    private void set(DebuggerSettings debuggerSettings, Boolean value) {
        if (value != null) {
            try {
                debuggerSettings.getClass()
                                .getField("INSTRUMENTING_AGENT")
                                .set(debuggerSettings, value);
            } catch (IllegalAccessException var4) {
                log.error("Failed to set INSTRUMENTING_AGENT field", var4);
            } catch (NoSuchFieldException var5) {
                log.error("Failed to set INSTRUMENTING_AGENT field", var5);
            }
        }

    }

    public void patchInstantInvokePlugin(JavaParameters var1, Project var3) {

        String hotDeployParam = AdvanceJavaAgentUtils.getInstantInvokeAgent(var3);
        if (hotDeployParam != null) {
            var1.getVMParametersList()
                .addParametersString(hotDeployParam);
        }

        var1.getVMParametersList()
            .addParametersString("-Dmyproject.root=\"" + var3.getBasePath() + "\"");
    }

    @NeededUntil(
            version = TestedIntelliJVersions.IC_2017_1
    )
    private Boolean get(DebuggerSettings debuggerSettings) {
        try {
            return (Boolean) debuggerSettings.getClass()
                                             .getField("INSTRUMENTING_AGENT")
                                             .get(debuggerSettings);
        } catch (IllegalAccessException var3) {
            log.error("Failed to get INSTRUMENTING_AGENT field", var3);
        } catch (NoSuchFieldException var4) {
            log.error("Failed to get INSTRUMENTING_AGENT field", var4);
        }

        return null;
    }
}
