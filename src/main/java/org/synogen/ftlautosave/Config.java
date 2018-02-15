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
		this.savefile = "ae_prof.sav";
		this.profile = "continue.sav";
		if (System.getProperties().getProperty("os.name").contains("Windows")) {
			ftlSavePath = System.getProperties().getProperty("user.home") + "\\My Documents\\My Games\\FasterThanLight";
		} else {
			// TODO detect FTL save path for other operating systems
			ftlSavePath = "";
		}
		Path ftl = Paths.get("FTLGame.exe");
		if (ftl.toFile().exists()) {
            this.ftlRunPath = ftl.toAbsolutePath().toString();
        } else {
			this.ftlRunPath = "FTLGame.exe";
		}

	}

	private Integer watchInterval;
	private String savefile;
	private String profile;
	private String ftlSavePath;
	private String ftlRunPath;

	public static Config fromFile(String filename) {
		try {
			ObjectMapper jackson = new ObjectMapper();
			File configfile = Paths.get(filename).toFile();
			if (!configfile.exists()) {
				jackson.writeValue(configfile, new Config());
			}

			return jackson.readValue(configfile, Config.class);
		} catch (IOException e) {
			return new Config();
		}
	}
}
