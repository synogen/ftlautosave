package org.synogen.ftlautosave;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

public class FileWatch extends Thread {

	private final Logger log;

	private String filename;

	public FileWatch(String filename) {
		this.filename = filename;
		log = Logger.getLogger("FileWatch (" + this.filename + ")");
	}

	@Override
	public void run() {
		log.info("Watching " + filename);

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
						log.info(filename + " has changed, saving new backup");
						Path backup = file.resolveSibling(file.getFileName() + "." + currentModification);
						Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
						previousModification = currentModification;
					}
				} catch (IOException e) {
					log.warning("Error while creating backup of " + filename + ":");
					e.printStackTrace();
				}
				
				sleep(Config.WATCH_INTERVAL);
			}
		} catch (InterruptedException e) {
			log.info("Exiting file watch for " + filename);
		}
	}
	
}
