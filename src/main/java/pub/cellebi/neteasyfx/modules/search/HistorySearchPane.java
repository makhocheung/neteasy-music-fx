package pub.cellebi.neteasyfx.modules.search;

import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pub.cellebi.neteasyfx.state.Jux;
import pub.cellebi.neteasyfx.modules.Line;
import pub.cellebi.neteasyfx.modules.Spacer;

public class HistorySearchPane extends VBox {

    public HistorySearchPane() {

        render();
        registerListener();
        initState();
    }

    public void render() {
        setPadding(new Insets(0, 15, 0, 15));
        getChildren().addAll(new Label("搜索历史"), Spacer.VSpacer(10), Line.HLine(), Spacer.VSpacer(10));
    }

    private void initState() {
        Jux.SEARCH_STATE.historySearch.forEach(s -> HistorySearchPane.this.getChildren().add(new HistorySearchCell(s)));
    }

    private void registerListener() {
        Jux.SEARCH_STATE.historySearch.addListener((SetChangeListener<String>) c -> {
            if (c.wasAdded()) {
                HistorySearchPane.this.getChildren().add(new HistorySearchCell(c.getElementAdded()));
            } else {
                var cells = HistorySearchPane.this.getChildren();
                cells.stream().filter(cell -> cell instanceof HistorySearchCell
                        && ((HistorySearchCell) cell).getText().equals(c.getElementRemoved()))
                        .findFirst().ifPresent(cells::remove);
            }
        });
    }
}
