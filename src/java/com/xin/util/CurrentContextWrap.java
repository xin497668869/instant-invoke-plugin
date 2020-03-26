package com.xin.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
@Data
@AllArgsConstructor
public class CurrentContextWrap {
    private AnActionEvent anActionEvent;

    public Project getProject() {
        return anActionEvent.getProject();
    }

    public Editor getEditor() {
        return anActionEvent.getData(EDITOR);
    }

    public PsiElement getPsiElement() {
        return anActionEvent.getData(PSI_ELEMENT);
    }
}
