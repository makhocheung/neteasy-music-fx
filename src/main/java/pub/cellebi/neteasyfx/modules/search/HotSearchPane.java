package pub.cellebi.neteasyfx.modules.search;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import pub.cellebi.neteasyfx.state.Jux;
import pub.cellebi.neteasyfx.modules.Line;
import pub.cellebi.neteasyfx.modules.Spacer;

import java.util.stream.Collectors;

public final class HotSearchPane extends VBox {

    private final FlowPane content;

    public HotSearchPane() {
        content = new FlowPane();

        render();
        registerListener();
    }

    private void render() {
        content.setHgap(10);
        content.setVgap(10);

        getChildren().addAll(new Label("热门搜索"), Spacer.VSpacer(10), Line.HLine(), Spacer.VSpacer(10), content);
        setPadding(new Insets(0, 15, 0, 15));
        getStyleClass().add("hot-search");
    }

    private void registerListener() {
        Jux.SEARCH_STATE.hotSearch.addListener((ListChangeListener<String>) c -> {
            var list = c.getList().stream().map(Button::new)
                    .peek(b -> b.setOnAction(e -> {
                        Jux.SEARCH_STATE.searchWord.set(b.getText());
                        Jux.SEARCH_STATE.historySearch.add(b.getText());
                    })).collect(Collectors.toList());
            content.getChildren().addAll(list);
        });
    }
}
