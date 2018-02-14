package org.synogen.ftlautosave;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileWatch extends Thread {

	private String filename;

	public FileWatch(String filename) {
		this.filename = filename;
	}

	@Override
	public void run() {
		App.log.info("Watching " + filename);

		Path savePath = Paths.get(App.config.getSavePath());
		
		Long previousModification = 0l;
		Long currentModification = 0l;

		try {
			while (true) {
				Path file = savePath.resolve(filename);

				if (file.toFile().exists()) {
					currentModification = file.toFile().lastModified();
				}

				try {
					if (currentModification.compareTo(previousModification) != 0) {
						App.log.info(filename + " has changed, saving new backup");
						Path backup = file.resolveSibling(file.getFileName() + "." + currentModification);
						Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
						previousModification = currentModification;
					}
				} catch (IOException e) {
					App.log.warning("Error while creating backup of " + filename + ":");
					e.printStackTrace();
				}
				
				sleep(App.config.getWatchInterval());
			}
		} catch (InterruptedException e) {
			App.log.info("Exiting file watch for " + filename);
		}
	}
	
}
