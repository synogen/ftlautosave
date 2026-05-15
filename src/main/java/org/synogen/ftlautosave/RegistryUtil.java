package org.synogen.ftlautosave;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistryUtil {

	public static String detectFtlRunPath() {
		String[][] keys = {
			{"HKLM\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 212680", "InstallLocation"},
			{"HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 212680", "InstallLocation"},
			{"HKLM\\SOFTWARE\\WOW6432Node\\GOG.com\\Games\\1207659102", "path"},
		};

		for (String[] key : keys) {
			String installDir = readRegistry(key[0], key[1]);
			if (installDir != null && !installDir.isEmpty()) {
				Path gameExe = Paths.get(installDir, "FTLGame.exe");
				if (gameExe.toFile().exists()) {
					App.log.fine("Detected FTL run path from registry: " + gameExe);
					return gameExe.toAbsolutePath().toString();
				}
			}
		}
		return null;
	}

	public static String detectFtlSavePath() {
		String personal = readRegistry(
			"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders",
			"Personal"
		);
		if (personal != null && !personal.isEmpty()) {
			if (personal.contains("%USERPROFILE%")) {
				String userProfile = System.getenv("USERPROFILE");
				if (userProfile != null) {
					personal = personal.replace("%USERPROFILE%", userProfile);
				}
			}
			Path savePath = Paths.get(personal, "My Games", "FasterThanLight");
			if (savePath.toFile().exists()) {
				App.log.fine("Detected FTL save path from registry: " + savePath);
				return savePath.toAbsolutePath().toString();
			}
		}
		return null;
	}

	private static String readRegistry(String keyPath, String valueName) {
		try {
			Process process = new ProcessBuilder("reg", "query", keyPath, "/v", valueName)
					.redirectErrorStream(true)
					.start();
			String output = new String(process.getInputStream().readAllBytes(), Charset.defaultCharset());
			if (output.contains("ERROR:")) return null;

			Pattern p = Pattern.compile("\\s*" + Pattern.quote(valueName) + "\\s+REG_\\w+\\s+(.*)", Pattern.CASE_INSENSITIVE);
			for (String line : output.split("\\r?\\n")) {
				Matcher m = p.matcher(line);
				if (m.matches()) {
					String value = m.group(1).trim();
					if (!value.isEmpty()) return value;
				}
			}
		} catch (IOException e) {
			App.log.log(Level.FINE, "Failed to read registry: " + keyPath, e);
		}
		return null;
	}
}
