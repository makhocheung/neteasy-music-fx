package pub.cellebi.neteasyfx.modules.explore;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import pub.cellebi.neteasyfx.service.NetService;
import pub.cellebi.neteasyfx.modules.Line;
import pub.cellebi.neteasyfx.modules.Spacer;
import pub.cellebi.neteasyfx.utils.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecommendMVPane extends BorderPane {

    private final Label title;
    private final HBox contentPane;

    public RecommendMVPane() {
        title = new Label();
        contentPane = new HBox();
        render();
        initState();
    }

    public void render() {
        var svgPath = new SVGPath();
        svgPath.setContent("M21 3H3c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H3V5h18v14zM8 15c0-1.66 1.34-3 3-3 .35 0 .69.07 1 .18V6h5v2h-3v7.03c-.02 1.64-1.35 2.97-3 2.97-1.66 0-3-1.34-3-3z");
        svgPath.getStyleClass().add("label-icon");
        title.setGraphic(svgPath);
        title.setText("推荐MV");
        title.setGraphicTextGap(5);
        var vBox = new VBox();
        vBox.getChildren().addAll(title, Line.HLine());
        vBox.setSpacing(10);
        BorderPane.setMargin(vBox, new Insets(0, 0, 10, 0));

        setTop(vBox);
        setCenter(contentPane);
        setMaxWidth(1080);
    }

    @SuppressWarnings("unchecked")
    public void initState() {
        NetService.get("http://cellebi.pub:3000/personalized/mv", json -> {

            var map = Util.MAPPER.readValue(json, Map.class);
            var items = (List<Map<String, Object>>) map.get("result");
            Platform.runLater(() -> {
                var list = items.stream().map(RecommendMVCell::new).collect(Collectors.toList());
                var spacer1 = Spacer.HSpacer(10);
                var spacer2 = Spacer.HSpacer(10);
                var cell0 = list.get(0);
                var cell1 = list.get(1);
                var cell2 = list.get(2);

                contentPane.getChildren().addAll(cell0, spacer1, cell1, spacer2, cell2);
                HBox.setHgrow(cell0, Priority.ALWAYS);
                HBox.setHgrow(cell1, Priority.ALWAYS);
                HBox.setHgrow(cell2, Priority.ALWAYS);
            });
        });
    }

}
