package log;

import com.intellij.util.messages.Topic;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class RequestTreeBuild {

    Topic<RequestTopic> requestTopic = Topic.create("RequestTopic", RequestTopic.class);

    public static Component build() {
        DefaultMutableTreeNode aaaa = new DefaultMutableTreeNode("aaaa");
        aaaa.add(new DefaultMutableTreeNode("alksdjflajsdf"));
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(aaaa);
        RequestTree requestTree = new RequestTree(defaultTreeModel);
        requestTree.setRootVisible(false);
        return requestTree;
    }
}
