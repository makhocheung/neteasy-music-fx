package pub.cellebi.neteasyfx.modules;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pub.cellebi.neteasyfx.state.Jux;


import static pub.cellebi.neteasyfx.modules.main.MainPane.MAIN;
import static pub.cellebi.neteasyfx.modules.main.NavigationPane.NAVIGATION_PANE;

public class TitleBar extends HBox {

    private final Button back;
    private final Label title;

    public Runnable action;

    public TitleBar(String text) {
        back = new Button();
        title = new Label(text);
        render();
        dataBind();
        registerListener();
    }

    public void render() {
        var svgPath = new SVGPath();
        svgPath.setContent("M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z");
        svgPath.setFill(Color.rgb(167, 167, 167));
        back.setGraphic(svgPath);
        back.setStyle("-fx-background-color: transparent");
        title.setFont(Font.font("Noto Sans Mono CJK SC", FontWeight.BOLD, 13));

        setSpacing(5);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(back, title);
    }

    public void dataBind() {
        back.visibleProperty().bind(NAVIGATION_PANE.canBack);
        back.managedProperty().bind(NAVIGATION_PANE.canBack);
    }

    public void registerListener() {
        back.setOnAction(e -> {
            if (back.isVisible()) {
                if(action != null){
                    action.run();
                }
                NAVIGATION_PANE.navigateBack();

                NAVIGATION_PANE.latestNode().map(Node::getUserData)
                        .filter(d -> d instanceof Button)
                        .ifPresentOrElse(o -> Jux.ROUTE_STATE.currentRouteNode.set((Button) o),
                                () -> Jux.ROUTE_STATE.currentRouteNode.set(null));
                if (!MAIN.getLeft().isVisible()) {
                    MAIN.getLeft().setVisible(true);
                    MAIN.getLeft().setManaged(true);
                }
            }
        });
    }
}
