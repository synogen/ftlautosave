package org.synogen.ftlautosave;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
public class Config {

	/**
	 * Credits: https://mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
	 */
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static String UHOME = System.getProperties().getProperty("user.home");
	public static boolean IS_WINDOWS = (OS.indexOf("win") >= 0);
	public static boolean IS_MAC = (OS.indexOf("mac") >= 0);
	public static boolean IS_UNIX = (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	public static boolean IS_SOLARIS = (OS.indexOf("sunos") >= 0);

	public Config() throws Exception {
		// defaults
		this.watchInterval = 1000;
		this.profile = "ae_prof.sav";
		this.savefile = "continue.sav";
		if (IS_WINDOWS) {
			ftlSavePath = UHOME + "\\My Documents\\My Games\\FasterThanLight";
		}
		else if(IS_MAC){
			ftlSavePath = UHOME + "/Library/Application Support/FasterThanLight";
		}
		else if(IS_UNIX){
			ftlSavePath = UHOME + "/.local/share/FasterThanLight";
		}
		else {
			throw new Exception("Operating systems other than Windows, Mac or Linux are not supported.");
		}

		Path ftl;
		if (IS_WINDOWS) {
			// default full path for Windows, if this fails, reverts to "FTLGame.exe" as per original implementation
			ftl = Paths.get("C:\\Program Files (x86)\\Steam\\steamapps\\common\\FTL Faster Than Light\\FTLGame.exe");
		}
		else if(IS_MAC){
			// on Mac this resolves but does result in "error=13, Permission denied"
			ftl = Paths.get(UHOME + "/Library/Application Support/Steam/steamapps/common/FTL Faster Than Light/FTL.app");
		}
		else if(IS_UNIX){
			// untested, best guess for now
			ftl = Paths.get(UHOME + "/.steam/steam/SteamApps/common/FTL Faster Than Light/FTL");
		}else {
			throw new Exception("Operating systems other than Windows, Mac or Linux are not supported.");
		}

		if (ftl.toFile().exists()) {
			this.ftlRunPath = ftl.toAbsolutePath().toString();
        } else {
			this.ftlRunPath = "FTLGame.exe";
		}
		this.autoStartFtl = false;
		this.autoUpdateSnapshots = true;
		this.limitBackupSaves = true;
		this.strictSaveParsing = false;
		this.maxNrOfBackupSaves = 500;
	}

	private Integer watchInterval;
	private String savefile;
	private String profile;
	private String ftlSavePath;
	private String ftlRunPath;
	private Boolean autoStartFtl;
	private Boolean autoUpdateSnapshots;
	private Boolean limitBackupSaves;
	private Boolean strictSaveParsing;
	private Integer maxNrOfBackupSaves;

	public static Config fromFile(String filename) throws Exception {
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
