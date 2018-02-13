package org.synogen.ftlautosave;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Config {

	public Config() {
		// defaults
		this.watchInterval = 1000;
		this.files.add("ae_prof.sav");
		this.files.add("continue.sav");
	}

	private Integer watchInterval;
	private List<String> files = new ArrayList<>();
}
