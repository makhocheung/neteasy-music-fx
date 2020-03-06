package pub.cellebi.neteasyfx.modules.main;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

public final class NavigationPane extends StackPane {

    public static final NavigationPane NAVIGATION_PANE = new NavigationPane();

    public final SimpleBooleanProperty canBack = new SimpleBooleanProperty(false);
    private final Deque<Node> stack;

    public NavigationPane() {
        stack = new LinkedList<>();
        render();
    }

    public void render() {
        getStyleClass().add("navigation-pane");
    }

    public void navigate(Node node) {
        if (stack.size() == 10) {
            return;
        }
        stack.push(node);
        getChildren().clear();
        getChildren().add(node);
        canBack.set(stack.size() > 1);
    }

    public void navigateBack(int i) {
        for (int j = 0; j < i; j++) {
            if (stack.size() == 1) {
                break;
            }
            try {
                stack.poll();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        getChildren().clear();
        getChildren().add(stack.peek());
        canBack.set(stack.size() > 1);
    }

    public void navigateBack() {
        navigateBack(1);
    }

    public Optional<Node> latestNode() {
        return Optional.ofNullable(stack.peek());
    }
}
