package pub.cellebi.neteasyfx.modules.search;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import pub.cellebi.neteasyfx.state.Jux;
import pub.cellebi.neteasyfx.modules.Spacer;

public class HistorySearchCell extends HBox {

    private Label keywordLabel;
    private Button close;

    public HistorySearchCell(String keyword) {
        keywordLabel = new Label(keyword);
        close = new Button();

        render();
        registerListener();
    }

    private void render() {
        var closeSVG = new SVGPath();
        closeSVG.setContent("M14.53 4.53l-1.06-1.06L9 7.94 4.53 3.47 3.47 4.53 7.94 9l-4.47 4.47 1.06 1.06L9 10.06l4.47 4.47 1.06-1.06L10.06 9z");
        closeSVG.setFill(Color.GRAY);
        close.setGraphic(closeSVG);
        close.setStyle("-fx-background-color: transparent");
        close.setCursor(Cursor.HAND);
        keywordLabel.setCursor(Cursor.HAND);
        var spacer = Spacer.HSpacer(0);

        getChildren().addAll(keywordLabel, spacer, close);
        setPadding(new Insets(0, 10, 10, 10));
        setAlignment(Pos.CENTER);
        setHgrow(spacer, Priority.ALWAYS);
    }

    private void registerListener() {
        keywordLabel.setOnMouseClicked(e -> Jux.SEARCH_STATE.searchWord.set(keywordLabel.getText()));
        close.setOnAction(e -> Jux.SEARCH_STATE.historySearch.remove(keywordLabel.getText()));
    }

    public String getText() {
        return keywordLabel.getText();
    }
}
