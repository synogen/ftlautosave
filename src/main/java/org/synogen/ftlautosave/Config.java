package org.synogen.ftlautosave;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
public class Config {

	public Config() {
		// defaults
		this.watchInterval = 1000;
		this.profile = "ae_prof.sav";
		this.savefile = "continue.sav";
		Path path = Paths.get("FTLGame.exe");
		if (System.getProperties().getProperty("os.name").contains("Windows")) {
			String detectedSavePath = RegistryUtil.detectFtlSavePath();
			if (detectedSavePath != null) {
				ftlSavePath = detectedSavePath;
			} else {
				ftlSavePath = System.getProperties().getProperty("user.home") + "\\My Documents\\My Games\\FasterThanLight";
			}

			String detectedRunPath = RegistryUtil.detectFtlRunPath();
			if (detectedRunPath != null) {
				ftlRunPath = detectedRunPath;
			} else {
				if (path.toFile().exists()) {
					ftlRunPath = path.toAbsolutePath().toString();
				} else {
					ftlRunPath = "FTLGame.exe";
				}
			}
		} else {
			ftlSavePath = "";
			if (path.toFile().exists()) {
				ftlRunPath = path.toAbsolutePath().toString();
			} else {
				ftlRunPath = "FTLGame.exe";
			}
		}
		this.autoStartFtl = false;
		this.autoUpdateSnapshots = true;
		this.limitBackupSaves = true;

		this.saveFileType = SaveFileType.CUSTOM;
	}

	private Integer watchInterval;
	private String savefile;
	private String profile;
	private String ftlSavePath;
	private String ftlRunPath;
	private Boolean autoStartFtl;
	private Boolean autoUpdateSnapshots;
	private Boolean limitBackupSaves;
	private String saveFileType;

	public static Config fromFile(String filename) {
		try {
			App.log.info("Loading configuration");
			ObjectMapper jackson = new ObjectMapper();
			File configfile = Paths.get(filename).toFile();
			if (!configfile.exists()) {
				jackson.writeValue(configfile, new Config());
			}

			return jackson.readValue(configfile, Config.class);
		} catch (IOException e) {
			App.log.info("Could not load configuration, using defaults");
			return new Config();
		}
	}

	public void toFile(String filename) {
		try {
			App.log.info("Saving configuration");
			ObjectMapper jackson = new ObjectMapper();
			File configfile = Paths.get(filename).toFile();

			jackson.writeValue(configfile, this);
		} catch (IOException e) {
			App.log.info("Could not write configuration");
		}
	}
}
