package pub.cellebi.neteasyfx.modules.explore;

import com.fasterxml.jackson.databind.json.JsonMapper;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import pub.cellebi.neteasyfx.service.NetService;
import pub.cellebi.neteasyfx.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class Carousel extends AnchorPane {

    private static final int BANNER_WIDTH = 378;
    private static final int BANNER_HEIGHT = 140;
    private static final int OFFSET = 30;

    private final List<Banner> banners;
    private Banner leftBanner;
    private Banner middleBanner;
    private Banner rightBanner;
    private final Button leftButton;
    private final Button rightButton;
    private final AnchorPane bannerPane;

    private final double x0;
    private final double x1;
    private final double x2;
    private int leftIndex = 0;
    private int middleIndex = 1;
    private int rightIndex = 2;
    private final int width;
    private final int height;
    private final Duration duration = Duration.seconds(0.3);

    //private ScheduledService service;

    //private class CarouselScheduledService extends ScheduledService<Void> {

//        @Override
//        protected Task<Void> createTask() {
//            return new Task<>() {
//                @Override
//                protected Void call() {
//                    return null;
//                }
//
//                @Override
//                protected void scheduled() {
//                    super.scheduled();
//                    middleBanner = banners.get(rightIndex);
//                    leftBanner = banners.get(middleIndex);
//                    rightIndex = newIndex(++rightIndex);
//                    rightBanner = banners.get(rightIndex);
//                    middleIndex = newIndex(++middleIndex);
//                    leftIndex = newIndex(++leftIndex);
//                    imageRoot.getChildren().clear();
//                    imageRoot.getChildren().addAll(rightBanner, leftBanner, middleBanner);
//                    imageRoot.getChildren().forEach(c -> StackPane.setAlignment(c, Pos.CENTER_LEFT));
//                    new ParallelTransition(middle2Left(leftBanner), left2Right(rightBanner), right2Middle(middleBanner)).play();
//                }
//            };
//        }
//    }

    //    public void auto() {
//        hoverProperty().addListener((a, b, n) -> {
//            if (n) {
//                service.cancel();
//            } else {
//                service.restart();
//            }
//        });
//        service = new CarouselScheduledService();
//        service.setDelay(Duration.seconds(5));
//        service.setPeriod(Duration.seconds(5));
//        service.start();
//    }

//    private void nextIndex() {
//        var size = banners.size();
//        leftIndex = (leftIndex + 1) % size;
//        middleIndex = (middleIndex + 1) % size;
//        rightIndex = (rightIndex + 1) % size;
//    }

    public Carousel(int width) {
        this(width, BANNER_HEIGHT);
    }

    public Carousel(int width, int height) {
        this.width = width;
        this.height = height;
        banners = new ArrayList<>();
        x0 = OFFSET;
        x1 = (width - BANNER_WIDTH) >> 1;
        x2 = width - BANNER_WIDTH - OFFSET;
        IntStream.range(0, 3).mapToObj(i -> new Banner()).forEach(banner -> {
            shrink(banner);
            banners.add(banner);
        });
        leftBanner = banners.get(leftIndex);
        middleBanner = banners.get(middleIndex);
        rightBanner = banners.get(rightIndex);
        leftButton = createControlButton("M30.83 14.83L28 12 16 24l12 12 2.83-2.83L21.66 24z", x0 + 17);
        rightButton = createControlButton("M20 12l-2.83 2.83L26.34 24l-9.17 9.17L20 36l12-12z", x2 + BANNER_WIDTH - 48);
        bannerPane = new AnchorPane();

        render();
        dataBind();
        registerListener();
        initState();
    }

    private void render() {
        leftBanner.setTranslateX(x0);
        middleBanner.setTranslateX(x1);
        rightBanner.setTranslateX(x2);
        expand(middleBanner);
        bannerPane.getChildren().addAll(leftBanner, rightBanner, middleBanner);

        getChildren().addAll(bannerPane, leftButton, rightButton);
        getStyleClass().add("carousel");
        setPrefSize(width, height);
        setMaxSize(width, height);
    }

    private void dataBind() {
        leftButton.visibleProperty().bind(this.hoverProperty());
        rightButton.visibleProperty().bind(this.hoverProperty());
    }

    private void registerListener() {
        leftButton.setOnAction(e -> {
            middleBanner = banners.get(leftIndex);
            rightBanner = banners.get(middleIndex);
            leftIndex = newIndex(--leftIndex);
            leftBanner = banners.get(leftIndex);
            middleIndex = newIndex(--middleIndex);
            rightIndex = newIndex(--rightIndex);
            bannerPane.getChildren().clear();
            bannerPane.getChildren().addAll(leftBanner, rightBanner, middleBanner);
            new ParallelTransition(middle2Right(rightBanner),
                    right2Left(leftBanner), left2Middle(middleBanner)).play();
        });
        rightButton.setOnAction(e -> {
            middleBanner = banners.get(this.rightIndex);
            leftBanner = banners.get(middleIndex);
            rightIndex = newIndex(++rightIndex);
            rightBanner = banners.get(rightIndex);
            middleIndex = newIndex(++middleIndex);
            leftIndex = newIndex(++leftIndex);
            bannerPane.getChildren().clear();
            bannerPane.getChildren().addAll(rightBanner, leftBanner, middleBanner);
            new ParallelTransition(middle2Left(leftBanner),
                    left2Right(rightBanner), right2Middle(middleBanner)).play();
        });
    }

    @SuppressWarnings("unchecked")
    private void initState() {
        NetService.get("http://cellebi.pub:3000/banner", json -> {
            try {
                var map = new JsonMapper().readValue(json, Map.class);
                var banners = (List<Map<String, Object>>) map.get("banners");
                var urls = banners.stream().map(m -> (String) m.get("imageUrl"))
                        .toArray(String[]::new);
                Platform.runLater(() -> updateBanners(urls));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateBanners(String[] urls) {
        if (banners.size() < urls.length) {
            var size = urls.length - banners.size();
            while (size > 0) {
                var banner = new Banner();
                shrink(banner);
                banners.add(banner);
                size--;
            }
        }
        IntStream.range(0, urls.length).forEach(i -> banners.get(i).updateImage(urls[i]));
    }

    private Button createControlButton(String content, double xOffset) {
        var button = new Button();
        var svg = new SVGPath();
        svg.setContent(content);
        svg.setFill(Color.WHITE);
        button.setGraphic(svg);
        button.setTranslateX(xOffset);
        button.setPrefHeight(Carousel.BANNER_HEIGHT);
        shrink(button);
        return button;
    }

    private int newIndex(int index) {
        index %= banners.size();
        if (index < 0) {
            index = index + banners.size();
        }
        return index;
    }

    private void shrink(Node node) {
        node.setScaleX(0.9);
        node.setScaleY(0.9);
    }

    private void expand(Node node) {
        node.setScaleX(1);
        node.setScaleY(1);
    }

    private ScaleTransition shrinkTransition(Banner banner) {
        var transition = new ScaleTransition(duration, banner);
        transition.setFromX(1);
        transition.setFromY(1);
        transition.setToX(0.9);
        transition.setToY(0.9);
        return transition;
    }

    private ScaleTransition expandTransition(Banner banner) {
        var transition = new ScaleTransition(duration, banner);
        transition.setFromX(0.9);
        transition.setFromY(0.9);
        transition.setToX(1);
        transition.setToY(1);
        return transition;
    }

    private Animation middle2Right(Banner middleBanner) {
        var scaleTransition = shrinkTransition(middleBanner);
        var translateTransition = new TranslateTransition(duration, middleBanner);
        translateTransition.setFromX(x1);
        translateTransition.setToX(x2);
        return new ParallelTransition(scaleTransition, translateTransition);
    }

    private Animation right2Left(Banner rightBanner) {
        var transition = new TranslateTransition(duration, rightBanner);
        transition.setFromX(x2);
        transition.setToX(x0);
        return transition;
    }

    private Animation left2Middle(Banner leftBanner) {
        var scaleTransition = expandTransition(leftBanner);
        var translateTransition = new TranslateTransition(duration, leftBanner);
        translateTransition.setFromX(x0);
        translateTransition.setToX(x1);
        return new ParallelTransition(scaleTransition, translateTransition);
    }

    private Animation middle2Left(Banner middleBanner) {
        var scaleTransition = shrinkTransition(middleBanner);
        var translateTransition = new TranslateTransition(duration, middleBanner);
        translateTransition.setFromX(x1);
        translateTransition.setToX(x0);
        return new ParallelTransition(scaleTransition, translateTransition);
    }

    private Animation left2Right(Banner leftBanner) {
        var transition = new TranslateTransition(duration, leftBanner);
        transition.setFromX(x0);
        transition.setToX(x2);
        return transition;
    }

    private Animation right2Middle(Banner rightBanner) {
        var scaleTransition = expandTransition(rightBanner);
        var translateTransition = new TranslateTransition(duration, rightBanner);
        translateTransition.setFromX(x2);
        translateTransition.setToX(x1);
        return new ParallelTransition(scaleTransition, translateTransition);
    }

    public static final class Banner extends Pane {

        public static final Image DEFAULT_IMAGE;

        static {
            var rect = new Rectangle(BANNER_WIDTH, BANNER_HEIGHT, Color.DARKGRAY);
            DEFAULT_IMAGE = rect.snapshot(new SnapshotParameters(), null);
        }

        private final ImageView imageView;

        public Banner() {
            imageView = new ImageView();
            imageView.setImage(DEFAULT_IMAGE);
            imageView.setFitWidth(BANNER_WIDTH);
            imageView.setFitHeight(BANNER_HEIGHT);
            setMaxSize(BANNER_WIDTH, BANNER_HEIGHT);
            getChildren().add(imageView);
            getStyleClass().add("banner");
        }

        public void updateImage(String url) {
            ImageUtil.loadImage(url, 720, imageView::setImage);
        }
    }

}
