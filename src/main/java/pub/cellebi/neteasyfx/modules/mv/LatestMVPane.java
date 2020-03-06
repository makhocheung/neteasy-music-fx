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

public class LatestMVPane extends BorderPane {

    private final Label title;
    private final GridPane contentPane;

    public LatestMVPane() {
        title = new Label();
        contentPane = new GridPane();

        render();
        initState();
    }

    public void render() {
        var svgPath = new SVGPath();
        svgPath.setContent("M18 4l2 4h-3l-2-4h-2l2 4h-3l-2-4H8l2 4H7L5 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V4h-4z");
        svgPath.getStyleClass().add("label-icon");
        title.setText("最新MV");
        title.setGraphic(svgPath);
        title.setGraphicTextGap(5);
        var vBox = new VBox();
        vBox.getChildren().addAll(title, Line.HLine());
        vBox.setSpacing(10);
        BorderPane.setMargin(vBox, new Insets(0, 0, 10, 0));
        contentPane.setHgap(10);
        contentPane.setVgap(20);

        setTop(vBox);
        setCenter(contentPane);
    }

    @SuppressWarnings("unchecked")
    public void initState() {
        NetService.get("http://cellebi.pub:3000/mv/first?limit=13", json -> {
            var map = MAPPER.readValue(json, Map.class);
            var list = (List<Map<String, Object>>) map.get("data");
            Platform.runLater(() -> initContent(list));
        });
    }

    private void initContent(List<Map<String, Object>> items) {
        for (var i = 1; i < items.size(); i++) {
            var cell = new MVCell(items.get(i));
            contentPane.addRow(i % 4, cell);
            GridPane.setHgrow(cell, Priority.ALWAYS);
        }
    }
}
