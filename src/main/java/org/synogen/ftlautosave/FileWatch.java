package org.synogen.ftlautosave;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

public class FileWatch extends Thread {

	private final Logger LOG;

	private String filename;

	public FileWatch(String filename) {
		this.filename = filename;
		LOG = Logger.getLogger("FileWatch (" + this.filename + ")");
	}

	@Override
	public void run() {
		LOG.info("Watching " + filename);

		Path root = Paths.get(".");
		
		Long previousModification = 0l;
		Long currentModification = 0l;

		try {
			while (true) {
				Path file = root.resolve(filename);

				if (file.toFile().exists()) {
					currentModification = file.toFile().lastModified();
				}

				try {
					if (currentModification.compareTo(previousModification) != 0) {
						LOG.info(filename + " has changed, saving new backup");
						Path backup = file.resolveSibling(file.getFileName() + "." + currentModification);
						Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
						previousModification = currentModification;
					}
				} catch (IOException e) {
					LOG.warning("Error while creating backup of " + filename + ":");
					e.printStackTrace();
				}
				
				sleep(App.config.getWatchInterval());
			}
		} catch (InterruptedException e) {
			LOG.info("Exiting file watch for " + filename);
		}
	}
	
}
