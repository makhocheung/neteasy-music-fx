package pub.cellebi.neteasyfx.modules.main;

import javafx.scene.layout.BorderPane;

import static pub.cellebi.neteasyfx.modules.main.NavigationPane.NAVIGATION_PANE;

public final class MainPane extends BorderPane {

    public static BorderPane MAIN;

    private final RoutePane routePane;
    private final PlayerPane playerPane;

    public MainPane() {
        MAIN = this;
        routePane = new RoutePane();
        playerPane = new PlayerPane();
        render();
    }

    public void render() {
        setLeft(routePane);
        setCenter(NAVIGATION_PANE);
        setBottom(playerPane);
        getStyleClass().add("main-pane");
    }
}
