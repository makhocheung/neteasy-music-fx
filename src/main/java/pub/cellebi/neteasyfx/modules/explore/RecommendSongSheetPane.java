package pub.cellebi.neteasyfx.modules.explore;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import pub.cellebi.neteasyfx.service.NetService;
import pub.cellebi.neteasyfx.modules.Line;
import pub.cellebi.neteasyfx.utils.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecommendSongSheetPane extends BorderPane {

    private final Label title;
    private final FlowPane contentPane;

    public RecommendSongSheetPane() {
        title = new Label();
        contentPane = new FlowPane();

        render();
        initState();
    }

    private void render() {
        var svgPath = new SVGPath();
        svgPath.setContent("M20 8H4V6h16v2zm-2-6H6v2h12V2zm4 10v8c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2v-8c0-1.1.9-2 2-2h16c1.1 0 2 .9 2 2zm-6 4l-6-3.27v6.53L16 16z");
        svgPath.getStyleClass().add("label-icon");
        title.setGraphic(svgPath);
        title.setText("推荐歌单");
        title.setGraphicTextGap(5);
        contentPane.setVgap(15);
        contentPane.setHgap(20);
        var vBox = new VBox();
        vBox.getChildren().addAll(title, Line.HLine());
        vBox.setSpacing(10);
        BorderPane.setMargin(vBox, new Insets(0, 0, 10, 0));

        setTop(vBox);
        setCenter(contentPane);
        setMaxWidth(1080);
    }

    @SuppressWarnings("unchecked")
    private void initState() {
        NetService.get("http://cellebi.pub:3000/personalized?limit=10", json -> {
            var map = Util.MAPPER.readValue(json, Map.class);
            var items = (List<Map<String, Object>>) map.get("result");
            Platform.runLater(() -> initContent(items));
        });
    }

    private void initContent(List<Map<String, Object>> items) {
        var cells = items.stream().map(RecommendSongSheetCell::new)
                .collect(Collectors.toList());
        contentPane.getChildren().addAll(cells);
    }
}
