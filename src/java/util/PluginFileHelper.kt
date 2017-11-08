package util

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.impl.file.PsiDirectoryFactory
import com.intellij.psi.impl.source.PsiJavaFileImpl
import com.intellij.psi.search.GlobalSearchScope
import vo.BaseException


/**
 *
 * @author linxixin@cvte.com
 */

class PluginFileHelper(val anActionEvent: AnActionEvent) {

    private val context: CurrentContext = CurrentContext(anActionEvent)

    val editor: Editor?
        get() {
            return context.editor
        }

    fun createDir(dir: VirtualFile, dirName: String) {
        ApplicationManager.getApplication().runWriteAction {
            dir.findChild(dirName)?.apply {
                PsiDirectoryFactory.getInstance(context.project).createDirectory(dir.createChildDirectory(null, dirName))
            }
        }
    }

    fun createJavaFile(dir: VirtualFile, className: String) {
        ApplicationManager.getApplication().runWriteAction {
            dir.findChild(className + ".java")?.apply {
                val classDir = PsiManager.getInstance(context.project).findDirectory(dir)
                classDir?.let { JavaDirectoryService.getInstance().createClass(it, className) }
            }
        }
    }

    /**
     * 根据package去创建目录
     */
    fun createByPackage(packageName: String) {
        ApplicationManager.getApplication().runWriteAction {
            var baseDir: VirtualFile? = context.project.baseDir
            var parent: VirtualFile

            val split = packageName.split(",")
            for (s in split) {
                parent = baseDir!!
                baseDir = parent.findChild(s)
                baseDir.let { PsiDirectoryFactory.getInstance(context.project).createDirectory(parent.createChildDirectory(null, s)) }
                baseDir = parent.findChild(s)

            }

        }
    }

    fun findFirstClass(className: String) {
        JavaPsiFacade.getInstance(context.project).findClass(className, GlobalSearchScope.allScope(context.project))
    }

    fun getPositionMethod(): PsiMethod? {
        if (context.psiFile !is PsiJavaFileImpl) {
            throw BaseException("必须是java文件才有方法")
        }
        val psiJavaFileImpl = context.psiFile as PsiJavaFileImpl
        psiJavaFileImpl.classes.forEach { clazz ->
            clazz.methods
                    .asSequence()
                    .filter { it.textRange.containsOffset(context.editor?.caretModel!!.offset) }
                    .forEach { return it }
        }
        throw BaseException("找不到该方法")
    }

}

data class CurrentContext(private var anActionEvent: AnActionEvent) {

    init {
        this.anActionEvent = anActionEvent
    }

    val project: Project
        get() = anActionEvent.project!!

    val editor: Editor?
        get() = anActionEvent.getData(EDITOR)

    val psiFile: PsiFile?
        get() = anActionEvent.getData(PSI_FILE)

    val psiElement: PsiElement?
        get() = anActionEvent.getData(PSI_ELEMENT)


}


