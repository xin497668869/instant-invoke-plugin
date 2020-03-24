package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;
import util.BaseException;
import util.PluginFileHelper;
import util.RequestInvokeUtil;
import util.TipsUtils;
import vo.MethodVo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class RunTestAction extends AnAction {
    private Logger log = Logger.getInstance(RunTestAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        log.info("触发方法执行事件, 请求开始");
        PluginFileHelper fileHelper = new PluginFileHelper(anActionEvent);
        Project project = anActionEvent.getProject();

        TipsUtils.catchBaseException(() -> {
            PsiMethod positionMethod = fileHelper.getPositionMethod();
            if (positionMethod.getParameterList()
                              .getParametersCount() > 0) {
                throw new BaseException("测试方法不能有参数");
            }
            ;
            String packageName = ((PsiJavaFile) positionMethod.getContainingFile()).getPackageName();
            List<String> paramNames = Arrays.stream(positionMethod.getParameterList()
                                                                  .getParameters())
                                            .map(PsiParameter::getName)
                                            .collect(Collectors.toList());

            String qualifiedName = positionMethod.getContainingClass()
                                                 .getQualifiedName();
            MethodVo methodVo = new MethodVo(
                    packageName,
                    qualifiedName,
                    positionMethod.getName(),
                    paramNames
            );
            RequestInvokeUtil.requestInvoke(methodVo, project);
        });
    }

}
