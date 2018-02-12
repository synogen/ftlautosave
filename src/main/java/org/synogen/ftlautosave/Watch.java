package org.synogen.ftlautosave;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

public class Watch extends Thread {

	private Logger log = Logger.getLogger(this.getName());

	@Override
	public void run() {
		Path root = Paths.get(".");
		
		Long lastModifiedProfile = 0l;
		Long lastModifiedSave = 0l;
		
		Long currentModifiedProfile = 0l;
		Long currentModifiedSave = 0l;
		try {
			while (true) {
				Path profile = root.resolve(Config.PROFILE_FILE);
				Path savefile = root.resolve(Config.SAVE_FILE);

				if (profile.toFile().exists()) {
					currentModifiedProfile = profile.toFile().lastModified();
				}
				if (savefile.toFile().exists()) {
					currentModifiedSave = savefile.toFile().lastModified();
				}

				try {
					if (currentModifiedProfile.compareTo(lastModifiedProfile) != 0) {
						log.info("Profile file has changed, saving new backup");
						Path profileBackup = profile.resolveSibling(profile.getFileName() + "." + currentModifiedProfile);
						Files.copy(profile, profileBackup, StandardCopyOption.REPLACE_EXISTING);
						lastModifiedProfile = currentModifiedProfile;
					}
					if (currentModifiedSave.compareTo(lastModifiedSave) != 0) {
						log.info("Save file has changed, saving new backup");
						Path savefileBackup = savefile.resolveSibling(savefile.getFileName() + "." + currentModifiedSave);
						Files.copy(savefile, savefileBackup, StandardCopyOption.REPLACE_EXISTING);
						lastModifiedSave = currentModifiedSave;
					}
				} catch (IOException e) {
					log.warning("Error while creating backup:");
					e.printStackTrace();
				}
				
				sleep(Config.WATCH_INTERVAL);
			}
		} catch (InterruptedException e) {
			log.info("Exiting save file watch");
		}
	}
	
}
