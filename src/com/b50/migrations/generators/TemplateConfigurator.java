package com.b50.migrations.generators;

import java.io.File;
import java.io.IOException;

import freemarker.template.Configuration;

public class TemplateConfigurator {

	protected String templateLocation;
	protected String templateName;

	public TemplateConfigurator(String templateLocation, String templateName) {
		this.templateLocation = templateLocation;
		this.templateName = templateName;
	}

	public Configuration getConfiguration() throws IOException {
		Configuration cfg = new Configuration();
		if (new File(this.templateLocation).exists()) {
			cfg.setDirectoryForTemplateLoading(new File(this.templateLocation));
			return cfg;
		} else {
			cfg.setClassForTemplateLoading(this.getClass(), "");
			return cfg;
		}
	}

}
