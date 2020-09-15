package com.xin.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.search.GlobalSearchScope;
import lombok.Getter;
import org.jetbrains.kotlin.psi.KtFile;

import java.io.IOException;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
@Getter
public class PluginFileHelper {

    private AnActionEvent anActionEvent;
    private CurrentContextWrap context;

    public PluginFileHelper(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
        this.context = new CurrentContextWrap(anActionEvent);
    }

    public void createDir(VirtualFile dir, String dirName) {
        ApplicationManager.getApplication()
                          .runWriteAction(() -> {
                              try {
                                  PsiDirectoryFactory.getInstance(context.getProject())
                                                     .createDirectory(dir.createChildDirectory(null, dirName));
                              } catch (IOException e) {
                                  e.printStackTrace();
                              }
                          });
    }

    /**
     * 根据package去创建目录
     */
    public void createByPackage(String packageName) {
        ApplicationManager.getApplication()
                          .runWriteAction(() -> {
                              VirtualFile baseDir = context.getProject()
                                                           .getBaseDir();

                              String[] split = packageName.split(",");
                              for (String s : split) {
                                  try {
                                      PsiDirectoryFactory.getInstance(context.getProject())
                                                         .createDirectory(baseDir.createChildDirectory(null, s));
                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  }
                                  baseDir = baseDir.findChild(s);
                              }

                          });
    }

    public void findFirstClass(String className) {
        JavaPsiFacade.getInstance(context.getProject())
                     .findClass(className, GlobalSearchScope.allScope(context.getProject()));
    }

    public PsiMethod getPositionMethod() {
        PsiFile psiFile = PsiDocumentManager.getInstance(context.getProject())
                                            .getPsiFile(context.getEditor()
                                                               .getDocument());

        PsiClass[] classes;
        if (psiFile instanceof PsiJavaFile) {
            classes = ((PsiJavaFile) psiFile).getClasses();
        } else if (psiFile instanceof KtFile) {
            classes = ((KtFile) psiFile).getClasses();
        } else {
            throw new BaseException("必须是java文件才有方法\r\n" + psiFile + " " + psiFile.getFileType());
        }
        for (PsiClass psiClass : classes) {
            for (PsiMethod method : psiClass.getMethods()) {
                if (method.getTextRange()
                          .containsOffset(context.getEditor()
                                                 .getCaretModel()
                                                 .getOffset())) {
                    return method;
                }
            }
        }
        throw new BaseException("找不到该方法");
    }

}
