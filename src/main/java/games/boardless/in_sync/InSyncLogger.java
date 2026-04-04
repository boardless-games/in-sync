package games.boardless.in_sync;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class InSyncLogger {
  public static Logger getLogger(String name) throws IOException {
    final Logger logger = Logger.getLogger(name);

    final FileHandler fileHandler = new FileHandler("InSync.log", true);
    fileHandler.setLevel(Level.INFO);
    fileHandler.setFormatter(new SimpleFormatter());
    logger.addHandler(fileHandler);

    final ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.ALL);
    consoleHandler.setFormatter(new SimpleFormatter());
    logger.addHandler(consoleHandler);

    logger.setLevel(Level.ALL);

    return logger;
  }
}
