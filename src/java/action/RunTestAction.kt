package action

import base.SettingProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.debugger.DebuggerManagerEx
import com.intellij.debugger.impl.DebuggerSession
import com.intellij.debugger.settings.DebuggerSettings
import com.intellij.debugger.ui.HotSwapStatusListener
import com.intellij.debugger.ui.HotSwapUI
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import com.intellij.psi.impl.source.PsiJavaFileImpl
import component.InitStartupActivity
import util.PluginFileHelper
import util.RequestInvokeUtil
import util.catchBaseException
import vo.BaseException
import vo.MethodVo


/**
 *
 * @author linxixin@cvte.com
 */


class RunTestAction : AnAction() {

    private val log: Logger = Logger.getInstance(this.javaClass)

    override fun actionPerformed(e: AnActionEvent) {
        log.info("触发方法执行事件, 请求开始")
        val fileHelper = PluginFileHelper(e!!)
        catchBaseException {
            val positionMethod = fileHelper.getPositionMethod() ?: throw BaseException("请在方法内执行")

            if (positionMethod.parameterList.parametersCount > 0) {
                throw BaseException("测试方法不能有参数")
            }
            if (InitStartupActivity.FileChange.isFileChange()) {
                FileDocumentManager.getInstance().saveAllDocuments()
                InitStartupActivity.FileChange.isChange = false
                val debuggerManager = DebuggerManagerEx.getInstanceEx(e.project)
                val session = debuggerManager.context.debuggerSession

                if (session != null && session.isAttached) {
//                session.isModifiedClassesScanRequired
                    HotSwapUI.getInstance(e.project).reloadChangedClasses(session, DebuggerSettings.getInstance().COMPILE_BEFORE_HOTSWAP, object : HotSwapStatusListener {
                        override fun onSuccess(sessions: MutableList<DebuggerSession>?) {
                            ApplicationManager.getApplication().runReadAction {
                                invoke(positionMethod, e.project!!)
                            }
                        }
                    })

                }

            } else {
                invoke(positionMethod, e.project!!)
            }

        }
    }

    fun invoke(positionMethod: PsiMethod, project: Project) {

        val packageName = (positionMethod.containingFile as PsiJavaFileImpl).packageName
        val paramNames = positionMethod.parameterList.parameters.mapNotNull { psiParameter: PsiParameter? -> psiParameter!!.name }
        val methodVo = MethodVo(
                packageName,
                positionMethod.containingClass!!.qualifiedName!!,
                positionMethod.name,
                paramNames
        )
        RequestInvokeUtil.requestInvoke(methodVo, project)
        SettingProperty.setLastRequest(project, ObjectMapper().writeValueAsString(methodVo))
    }

}
