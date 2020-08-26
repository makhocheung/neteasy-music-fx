package pub.cellebi.neteasyfx.modules.explore;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import pub.cellebi.neteasyfx.service.NetService;
import pub.cellebi.neteasyfx.modules.Line;
import pub.cellebi.neteasyfx.modules.Spacer;
import pub.cellebi.neteasyfx.utils.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExclusiveBroadcastPane extends BorderPane {

    private final Label title;
    private final HBox contentPane;

    public ExclusiveBroadcastPane() {
        title = new Label();
        contentPane = new HBox();
        render();
        initState();
    }

    public void render() {
        var svgPath = new SVGPath();
        svgPath.setContent("M20 2H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 5h-3v5.5c0 1.38-1.12 2.5-2.5 2.5S10 13.88 10 12.5s1.12-2.5 2.5-2.5c.57 0 1.08.19 1.5.51V5h4v2zM4 6H2v14c0 1.1.9 2 2 2h14v-2H4V6z");
        svgPath.getStyleClass().add("label-icon");
        title.setGraphic(svgPath);
        title.setText("独家放送");
        title.setGraphicTextGap(5);
        title.setFont(Font.font("Noto Sans Mono CJK SC"));
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
        NetService.get("http://cellebi.pub:3000/personalized/privatecontent", json -> {
            var map = Util.MAPPER.readValue(json, Map.class);
            var items = (List<Map<String, Object>>) map.get("result");
            Platform.runLater(() -> {
                var list = items.stream().map(ExclusiveBroadcastCell::new).collect(Collectors.toList());
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
