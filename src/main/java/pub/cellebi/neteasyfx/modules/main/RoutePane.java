package pub.cellebi.neteasyfx.modules.main;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;
import pub.cellebi.neteasyfx.state.Jux;
import pub.cellebi.neteasyfx.modules.explore.ExplorePane;
import pub.cellebi.neteasyfx.modules.mv.MVPane;
import pub.cellebi.neteasyfx.modules.search.SearchPane;

import java.util.List;

import static pub.cellebi.neteasyfx.modules.main.NavigationPane.NAVIGATION_PANE;

public final class RoutePane extends VBox {

    private final Button menu;
    private final Button exploreRoute;
    private final Button searchRoute;
    private final Button mvRoute;
    private final List<Button> routes;

    private boolean isMenuExpand = true;

    public RoutePane() {
        menu = new Button();
        exploreRoute = new Button("发现音乐");
        searchRoute = new Button("搜索");
        mvRoute = new Button("MV");
        routes = List.of(exploreRoute, searchRoute, mvRoute);

        render();
        dataBind();
        registerListener();
        initState();
    }

    private void render() {
        styleNavigateButton(exploreRoute, "M12 1c-4.97 0-9 4.03-9 9v7c0 1.66 1.34 3 3 3h3v-8H5v-2c0-3.87 3.13-7 7-7s7 3.13 7 7v2h-4v8h3c1.66 0 3-1.34 3-3v-7c0-4.97-4.03-9-9-9z");
        styleNavigateButton(searchRoute, "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
        styleNavigateButton(mvRoute, "M21 3H3c-1.11 0-2 .89-2 2v12c0 1.1.89 2 2 2h5v2h8v-2h5c1.1 0 1.99-.9 1.99-2L23 5c0-1.11-.9-2-2-2zm0 14H3V5h18v12zm-5-6l-7 4V7z");

        var menuSVGPath = new SVGPath();
        menuSVGPath.setContent("M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z");
        menuSVGPath.setFill(Color.rgb(69, 69, 69));
        menu.setGraphic(menuSVGPath);
        menu.getStyleClass().add("menu");
        var hBox = new HBox(menu);
        hBox.setAlignment(Pos.CENTER_LEFT);

        getStyleClass().add("route-pane");
        getStylesheets().add(getClass().getResource("route-pane.css").toExternalForm());
        getChildren().addAll(hBox, exploreRoute, searchRoute, mvRoute);
    }

    private void registerListener() {
        menu.setOnAction(e -> {
            isMenuExpand = !isMenuExpand;
            routes.forEach(r -> r.setContentDisplay(isMenuExpand ? ContentDisplay.LEFT : ContentDisplay.GRAPHIC_ONLY));
            setPrefWidth(isMenuExpand ? 185 : 50);
        });
        exploreRoute.setOnAction(event -> {
            if (isNeedNavigate(ExplorePane.class)) {
                Jux.ROUTE_STATE.currentRouteNode.set(exploreRoute);
                var pane = new ExplorePane();
                Platform.runLater(() -> doNavigate(exploreRoute, pane));
            }
        });
        searchRoute.setOnAction(event -> {
            if (isNeedNavigate(SearchPane.class)) {
                Jux.ROUTE_STATE.currentRouteNode.set(searchRoute);
                doNavigate(searchRoute, new SearchPane());
            }
        });
        mvRoute.setOnAction(event -> {
            if (isNeedNavigate(MVPane.class)) {
                Jux.ROUTE_STATE.currentRouteNode.set(mvRoute);
                doNavigate(mvRoute, new MVPane());
            }
        });
    }

    private void dataBind() {
        Jux.ROUTE_STATE.currentRouteNode.addListener((v, o, n) -> {
            if (n != null) {
                selectedRouteButton((Button) n);
            } else {
                routes.forEach(route -> route.getStyleClass().remove("selected-button"));
            }
        });
    }

    private void initState() {
        Jux.ROUTE_STATE.currentRouteNode.set(exploreRoute);
        Platform.runLater(() -> doNavigate(exploreRoute, new ExplorePane()));
    }

    private void styleNavigateButton(Button button, String s) {
        button.setPrefWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setTextAlignment(TextAlignment.JUSTIFY);
        var svgPath = new SVGPath();
        svgPath.setContent(s);
        svgPath.setFill(Color.rgb(69, 69, 69));
        button.setGraphic(svgPath);
        button.setGraphicTextGap(20);
    }

    private void selectedRouteButton(final Button routeButton) {
        routes.forEach(route -> route.getStyleClass().remove("selected-button"));
        routeButton.getStyleClass().add("selected-button");
    }

    private boolean isNeedNavigate(final Class<? extends Node> clazz) {
        return !NAVIGATION_PANE.latestNode()
                .map(Node::getClass)
                .map(c -> c == clazz)
                .orElse(false);
    }

    private void doNavigate(final Button routeButton, final Node node) {
        node.setUserData(routeButton);
        NAVIGATION_PANE.navigate(node);
    }

}
