package io.github.aggumati.frame;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateLoader {
	public static String loadJavaTemplate (String tempalteName, Map<String, Object> params) throws IOException, TemplateException {
		String result = "";
		Configuration cfg = new Configuration();
		String listTemplate = String.format("src/main/resources/template-gen/java/%s.ftl", tempalteName);
		Template template = cfg.getTemplate(listTemplate);
		Writer out = new StringWriter();
		template.process(params, out);
		result = out.toString();
		out.flush();
		return result;
	}
	
	public static String loadJavaTemplate (String tempalteName) throws IOException, TemplateException {
		String result = "";
		Configuration cfg = new Configuration();
		String listTemplate = String.format("src/main/resources/template-gen/java/%s.ftl", tempalteName);
		Template template = cfg.getTemplate(listTemplate);
		Writer out = new StringWriter();
		template.process(new HashMap<String, Object>(), out);
		result = out.toString();
		out.flush();
		return result;
	}
	
	public static String loadControllerTemplate (String tempalteName, Map<String, Object> params) throws IOException, TemplateException {
		tempalteName = "controller/"+tempalteName;
		return loadJavaTemplate(tempalteName, params);
	}
	
	public static String loadServiceTemplate (String tempalteName, Map<String, Object> params) throws IOException, TemplateException {
		tempalteName = "service/"+tempalteName;
		return loadJavaTemplate(tempalteName, params);
	}
}
