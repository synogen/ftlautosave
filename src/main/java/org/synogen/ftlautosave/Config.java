package org.synogen.ftlautosave;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Data
public class Config {

	public Config() {
		// defaults
		this.watchInterval = 1000;
		this.files.add("ae_prof.sav");
		this.files.add("continue.sav");
		if (System.getProperties().getProperty("os.name").contains("Windows")) {
			savePath = System.getProperties().getProperty("user.home") + "\\My Documents\\My Games\\FasterThanLight";
		} else {
			// TODO detect FTL save path for other operating systems
			savePath = "";
		}

	}

	private Integer watchInterval;
	private List<String> files = new ArrayList<>();
	private String savePath;

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
