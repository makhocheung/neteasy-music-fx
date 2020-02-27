package pub.cellebi.neteasyfx.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public final class Banner extends AnchorPane {
    public Banner() {
        var imageView = new ImageView();
        var image = new Image("http://p1.music.126.net/onU0CLk3WjB2Kbk-9GLG0w==/109951164746730039.jpg", 360, 133, true, true);
        imageView.setImage(image);
        var clip = new Rectangle(image.getWidth(), image.getHeight());
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        imageView.setClip(clip);
        var label = new Label("news");
        var pane = new StackPane(label);
        pane.setStyle("-fx-background-color: red;-fx-padding: 5px;-fx-background-radius: 7 0 7 0");
        getChildren().addAll(imageView, pane);
        AnchorPane.setBottomAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);

    }
}
