package action

import base.SettingProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiParameter
import com.intellij.psi.impl.source.PsiJavaFileImpl
import com.intellij.util.messages.Topic
import util.PluginFileHelper
import util.catchBaseException
import vo.BaseException
import vo.MethodVo
import vo.ResponseData
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException


/**
 *
 * @author linxixin@cvte.com
 */


class RunTestAction : AnAction() {

    private val log: Logger = Logger.getInstance(this.javaClass)

    override fun actionPerformed(e: AnActionEvent?) {
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
            requestInvoke(methodVo, e.project!!)
        }
    }

    companion object {
        private val log: Logger = Logger.getInstance(RunTestAction::class.java)
        /**
         * 用于和显示组件进行交互
         */
        var ResponseDataTopic = Topic.create("ResponseDataTopicListener", ResponseDataTopicListener::class.java)

        fun requestInvoke(positionMethod: MethodVo, project: Project) {

            val port = SettingProperty.getProjectPort(project)
            if (port < 0) {
                throw BaseException("项目端口没开,拿不到项目的端口!")
            }

            log.info("准备开始请求 $port, 内容为 $positionMethod")
            ApplicationManager.getApplication().invokeLater {
                val log: Logger = Logger.getInstance("执行线程")
                Thread {
                    catchBaseException {
                        try {
                            val socket = Socket()
                            val socketAddress = InetSocketAddress("localhost", port)
                            socket.connect(socketAddress, 500)
                            socket.soTimeout = 1 * 60 * 1000
                            socket.use {
                                val bufferedWriter = BufferedWriter(OutputStreamWriter(it.getOutputStream()))
                                val requestContext = ObjectMapper().writeValueAsString(positionMethod)
                                bufferedWriter.write(requestContext + "\r\n")
                                SettingProperty.setLastRequest(project, requestContext)
                                bufferedWriter.flush()
                                val bufferedReader = BufferedReader(InputStreamReader(it.getInputStream()))
                                val readLine = bufferedReader.readLine()
                                log.info("读取请求内容 $readLine")
                                if (readLine.isNotBlank()) {
                                    val responseData = ObjectMapper().readValue(readLine, ResponseData::class.java)
                                    println("收到的协议 $responseData")
                                    var responseDataListener = project.messageBus.syncPublisher(ResponseDataTopic)
                                }
                            }
                        } catch (e: SocketTimeoutException) {
                            log.warn(e)
                            if (e.message?.contains("Read timed out") == true) {
                                throw BaseException("debug时间太长了吧?")
                            } else {
                                throw BaseException("端口尚未打开, 程序还没启动?")
                            }
                        } catch (e: ConnectException) {
                            log.warn(e)
                            throw BaseException("debug时间太长了吧?")
                        } catch (e: Exception) {
                            log.error(e)
                            throw BaseException("请求异常, 请看日志 ${e.message}")
                        }
                    }
                }.start()
            }
        }
    }


    interface ResponseDataTopicListener {
        fun come(responseData: ResponseData)
    }

}