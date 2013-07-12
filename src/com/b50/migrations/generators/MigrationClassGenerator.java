package com.b50.migrations.generators;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MigrationClassGenerator {
	
	private String templateLocation;
	private String templateName;
	
	public MigrationClassGenerator(String templateLocation, String templateName) {
		this.templateLocation = templateLocation;
		this.templateName = templateName;
	}

	public String generate(String packageName, int sequence) throws IOException{
		Configuration cfg = new Configuration();
		cfg.setDirectoryForTemplateLoading(new File(this.templateLocation));
		Template template = cfg.getTemplate(this.templateName);
		Map<String, String> input = new HashMap<String, String>();
		input.put("package", packageName);
		input.put("sequence_number", ""+sequence);
		StringWriter out = new StringWriter();
		try {
			template.process(input, out);
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.flush();
		return out.toString();
	}
}
