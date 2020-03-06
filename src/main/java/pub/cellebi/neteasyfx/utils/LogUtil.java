package pub.cellebi.neteasyfx.utils;

import pub.cellebi.neteasyfx.MusicApp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LogUtil {

    private static final Logger logger;

    static {
        logger = Logger.getGlobal();
        try {
            var path = logFilePattern();
            var logPath = Path.of(path);
            if (Files.notExists(logPath)) {
                Files.createFile(Path.of(path));
            }
            var handler = new FileHandler(path, true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String logFilePattern() {
        var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return MusicApp.HOME.resolve(LocalDate.now().format(dateTimeFormatter) + ".log").toString();
    }

    public static Logger getLogger() {
        return logger;
    }
}
