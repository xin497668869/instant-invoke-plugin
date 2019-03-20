package action

import base.SettingProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiParameter
import com.intellij.psi.impl.source.PsiJavaFileImpl
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

            val packageName = (positionMethod.containingFile as PsiJavaFileImpl).packageName
            val paramNames = positionMethod.parameterList.parameters.mapNotNull { psiParameter: PsiParameter? -> psiParameter!!.name }
            val methodVo = MethodVo(
                    packageName,
                    positionMethod.containingClass!!.qualifiedName!!,
                    positionMethod.name,
                    paramNames
            )
            RequestInvokeUtil.requestInvoke(methodVo, e.project!!)
            SettingProperty.setLastRequest(e!!.project!!, ObjectMapper().writeValueAsString(methodVo))
        }
    }


}