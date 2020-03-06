package pub.cellebi.neteasyfx.modules.explore;

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
import pub.cellebi.neteasyfx.utils.Util;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class RecommendRadioPane extends BorderPane {

    private final Label title;
    private final GridPane contentPane;

    public RecommendRadioPane() {
        title = new Label("主播电台");
        contentPane = new GridPane();

        render();
        initState();
    }

    private void render() {
        var svgPath = new SVGPath();
        svgPath.setContent("M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zM7.76 16.24l-1.41 1.41C4.78 16.1 4 14.05 4 12c0-2.05.78-4.1 2.34-5.66l1.41 1.41C6.59 8.93 6 10.46 6 12s.59 3.07 1.76 4.24zM12 16c-2.21 0-4-1.79-4-4s1.79-4 4-4 4 1.79 4 4-1.79 4-4 4zm5.66 1.66l-1.41-1.41C17.41 15.07 18 13.54 18 12s-.59-3.07-1.76-4.24l1.41-1.41C19.22 7.9 20 9.95 20 12c0 2.05-.78 4.1-2.34 5.66zM12 10c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z");
        svgPath.getStyleClass().add("label-icon");
        title.setGraphicTextGap(5);
        title.setGraphic(svgPath);
        var line1 = Line.HLine();
        var line2 = Line.HLine();
        contentPane.addRow(1, line1);
        contentPane.addRow(3, line2);
        contentPane.setVgap(10);
        GridPane.setColumnSpan(line1, 2);
        GridPane.setColumnSpan(line2, 2);
        var vBox = new VBox();
        vBox.getChildren().addAll(title, Line.HLine());
        vBox.setSpacing(10);
        BorderPane.setMargin(vBox, new Insets(0, 0, 10, 0));

        setTop(vBox);
        setCenter(contentPane);
        setMaxWidth(1080);
    }

    private void initState() {
        NetService.get("http://cellebi.pub:3000/personalized/djprogram", json -> {
            var mapper = Util.MAPPER;
            var list = ((List<Map<String, Object>>) mapper.readValue(json, Map.class).get("result"))
                    .subList(0, 6);
            list.forEach(m -> {
                var p = (Map<String, Object>) m.remove("program");
                m.put("radio", p.get("radio"));
            });
            Platform.runLater(() -> initContent(list));
        });
    }

    private void initContent(List<Map<String, Object>> items) {
        for (int i = 0; i < items.size(); i++) {
            var cell = new RecommendRadioCell(items.get(i));
            var columnIndex = i % 2;
            var rowIndex = columnIndex == 0 ? i : i - 1;
            contentPane.add(cell, columnIndex, rowIndex);
            GridPane.setHgrow(cell, Priority.ALWAYS);
        }
    }
}
