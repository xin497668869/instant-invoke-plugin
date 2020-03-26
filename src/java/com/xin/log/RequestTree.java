package com.xin.log;

import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreeModel;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class RequestTree extends Tree implements DataProvider {
    public RequestTree(TreeModel treemodel) {
        super(treemodel);
    }

    @Nullable
    @Override
    public Object getData(@NotNull String s) {
        return null;
    }
}
