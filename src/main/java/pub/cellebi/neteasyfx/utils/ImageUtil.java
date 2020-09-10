package pub.cellebi.neteasyfx.utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class  ImageUtil {
    private final static Path IMAGE_CACHE_PATH;

    static {
        IMAGE_CACHE_PATH = Path.of(System.getProperty("user.home"))
                .resolve(".neteasy-music-fx")
                .resolve("images");
    }

    public final static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(3);
    private final static Map<String, Image> IMAGE_CACHE = new HashMap<>();

    public static Image loadImage(String url, double width) {
        return loadImage(url, width, null);
    }

    public static Image loadImage(String url, double width, Consumer<Image> onLoad) {
        var imageName = getImageName(url);
        if (IMAGE_CACHE.containsKey(imageName)) {
            var image = IMAGE_CACHE.get(imageName);
            executeOnLoad(onLoad, image);
            return image;
        }
        var imagePath = IMAGE_CACHE_PATH.resolve(imageName);
        if (Files.exists(imagePath)) {
            try {
                var image = new Image("file:///" + imagePath, true);
                IMAGE_CACHE.put(imageName, image);
                executeOnLoad(onLoad, image);
                return image;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        var image = new Image(url, width, -1, true, true, true);
        image.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.doubleValue() == 1.0) {
                IMAGE_CACHE.put(imageName, image);
                EXECUTOR_SERVICE.submit(() -> {
                    try {
                        if (Files.notExists(IMAGE_CACHE_PATH)) {
                            Files.createDirectories(IMAGE_CACHE_PATH);
                        }
                        if (Files.notExists(imagePath)) {
                            var bImage = SwingFXUtils.fromFXImage(image, null);
                            if (bImage == null) {
                                return;
                            }
                            var formatName = imageName.substring(imageName.indexOf('.') + 1);
                            var isSuccess = ImageIO.write(bImage, formatName, imagePath.toFile());
                            if (!isSuccess) {
                                ImageIO.write(bImage, "png", imagePath.toFile());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                executeOnLoad(onLoad, image);
            }
        });
        image.errorProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                LogUtil.getLogger().info(String.format("can't download image(%s)", url) +
                        image.getException());
            }
        });
        return image;
    }

    private static void executeOnLoad(Consumer<Image> consumer, Image image) {
        if (consumer != null) {
            consumer.accept(image);
        }
    }

    private static String getImageName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
