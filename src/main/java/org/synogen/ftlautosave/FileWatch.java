package org.synogen.ftlautosave;

import lombok.Getter;

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

	@Getter
	private boolean watching = false;

	@Override
	public void run() {
		Path savePath = Paths.get(App.config.getFtlSavePath());

		App.log.info("Watching " + filename);

		Path file = savePath.resolve(filename);

		Long previousModification = 0l;
		Long currentModification = 0l;

		if (!file.toFile().exists()) {
			App.log.info(filename + " not found, it will be watched as soon as it is created");
		}

		try {
			while (true) {
				if (file.toFile().exists()) {
					currentModification = file.toFile().lastModified();
					watching = true;
				} else {
					watching = false;
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
