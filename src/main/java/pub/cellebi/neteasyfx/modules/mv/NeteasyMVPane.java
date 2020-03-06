package pub.cellebi.neteasyfx.modules.mv;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import pub.cellebi.neteasyfx.service.NetService;
import pub.cellebi.neteasyfx.modules.Line;

import java.util.List;
import java.util.Map;

import static pub.cellebi.neteasyfx.utils.Util.MAPPER;

public final class NeteasyMVPane extends BorderPane {

    private final Label title;
    private final GridPane contentPane;


    public NeteasyMVPane() {
        title = new Label();
        contentPane = new GridPane();

        render();
        initState();
    }

    public void render() {
        var svgPath = new SVGPath();
        svgPath.setContent("M17 10.5V7c0-.55-.45-1-1-1H4c-.55 0-1 .45-1 1v10c0 .55.45 1 1 1h12c.55 0 1-.45 1-1v-3.5l4 4v-11l-4 4z");
        svgPath.getStyleClass().add("label-icon");
        title.setText("网易出品");
        title.setGraphic(svgPath);
        title.setGraphicTextGap(5);
        var vBox = new VBox();
        vBox.getChildren().addAll(title, Line.HLine());
        vBox.setSpacing(10);
        BorderPane.setMargin(vBox, new Insets(0, 0, 10, 0));

        setTop(vBox);
        setCenter(contentPane);
    }

    @SuppressWarnings("unchecked")
    private void initState() {
        NetService.get("http://cellebi.pub:3000/mv/exclusive/rcmd?limit=4", json -> {
            var map = MAPPER.readValue(json, Map.class);
            var list = (List<Map<String, Object>>) map.get("data");
            Platform.runLater(() -> initContent(list));
        });
    }

    private void initContent(List<Map<String, Object>> items) {
        for (var i = 1; i < items.size(); i++) {
            var cell = new MVCell(items.get(i));
            contentPane.addColumn(i, cell);
            GridPane.setHgrow(cell, Priority.ALWAYS);
        }
    }
}
