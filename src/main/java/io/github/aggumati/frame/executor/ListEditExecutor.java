package io.github.aggumati.frame.executor;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import id.co.vico.appframe.model.isys.WebScreen;
import id.co.vico.appframe.util.StringUtil;
import io.github.aggumati.frame.ScreenGenerator;
import io.github.aggumati.frame.TemplateLoader;
import io.github.aggumati.frame.list.FrameListColumn;
import io.github.aggumati.frame.list.FrameListId;
import io.github.aggumati.frame.model.FieldGenerator;
import io.github.aggumati.frame.model.MethodGenerator;
import io.github.aggumati.frame.model.Scafollding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class ListEditExecutor implements ScreenExecutor {
	
	private String urlNewForm;
	private String urlEditForm;
	private String urlDeleteRow;
	private ListExecutor listExecutor;

	@Override
	public void execute(ScreenGenerator screenGenerator) throws Exception {
		
		WebScreen webScreen = screenGenerator.getWebScreen();
		JDefinedClass definedClass = screenGenerator.getDefinedClass();
		
		final String tmpUrl = webScreen.getUrl();
		final String tmpName = webScreen.getScreenName();
		urlNewForm = String.format("%s_new", webScreen.getUrl());
		urlEditForm = String.format("%s_edit", webScreen.getUrl());
		
		ScreenExecutor execute = new FormNewExecutor();
		webScreen.setUrl(urlNewForm);
		webScreen.setScreenName("New " + tmpName);
		screenGenerator.setWebScreen(webScreen);
		execute.execute(screenGenerator);

		execute = new FormEditExecutor();
		webScreen.setUrl(urlEditForm);
		webScreen.setScreenName("Update " + tmpName);
		screenGenerator.setWebScreen(webScreen);
		execute.execute(screenGenerator);
		
		webScreen.setUrl(tmpUrl);
		webScreen.setScreenName(tmpName);
		
		final String actionMethodName = "delete" + StringUtil.toCamelCase(webScreen.getUrl());
		MethodGenerator tmp = new MethodGenerator();
		tmp.setMethodName(actionMethodName);
		urlDeleteRow = "/" + actionMethodName + ".do";
		if (!screenGenerator.getClassLoaded().getListMethod().contains(tmp)) {
			JMethod methodListData = definedClass.method(JMod.PUBLIC,id.co.vico.appframe.model.JsonResponse.class, actionMethodName);
			methodListData.param(String.class, "id").annotate(RequestParam.class).param("value", "id");
			methodListData.param(HttpServletRequest.class, "servletRequest");
			methodListData.annotate(org.springframework.web.bind.annotation.RequestMapping.class).param("value", urlDeleteRow);
			methodListData.annotate(org.springframework.web.bind.annotation.ResponseBody.class);
			
			final String name = "jsonresponse.java";
			final Map<String, Object> params = new HashMap<String, Object>();
			methodListData.body().directStatement(TemplateLoader.loadJavaTemplate(name, params));
		}
		
		generateService(screenGenerator);
		generateListView(screenGenerator);
		

	}
	
	private void generateService (ScreenGenerator screenGenerator) throws ClassNotFoundException, IOException, TemplateException {
		try {
			listExecutor = new ListExecutor();
			listExecutor.generateService(screenGenerator);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}
	
	private void generateListView (ScreenGenerator screenGenerator) throws ClassNotFoundException, IOException, TemplateException, NoSuchMethodException, SecurityException {
		
		WebScreen webScreen = screenGenerator.getWebScreen();
		JDefinedClass definedClass = screenGenerator.getDefinedClass();
		final String methodNameList = "list" + StringUtil.toCamelCase(webScreen.getUrl());
		final String methodNameData = "data" + StringUtil.toCamelCase(webScreen.getUrl());
		final String pageUrl = "page" + StringUtil.toCamelCase(webScreen.getUrl());
		final String serviceField = StringUtil.lowerFirst(screenGenerator.getScafollding().getServiceClassName());
		
		FieldGenerator fieldGen = new FieldGenerator(JMod.PRIVATE, pageUrl, "String" , "pages/" + webScreen.getUrl());
		if (!screenGenerator.getClassLoaded().getListFields().contains(fieldGen))definedClass.field(fieldGen.getModifier(), fieldGen.getTypeClass(), pageUrl, JExpr.lit("pages/" + webScreen.getUrl()));

		fieldGen = new FieldGenerator(JMod.PRIVATE, serviceField, listExecutor.getServiceClassName() , null);
		if (!screenGenerator.getClassLoaded().getListFields().contains(fieldGen))
			definedClass.field(fieldGen.getModifier(), fieldGen.getTypeClass(), serviceField).annotate(Autowired.class);
		
		MethodGenerator tmp = new MethodGenerator();
		tmp.setMethodName(methodNameList);
		if (!screenGenerator.getClassLoaded().getListMethod().contains(tmp)) {
			JMethod methodList = definedClass.method(JMod.PUBLIC,org.springframework.web.servlet.ModelAndView.class, methodNameList);
			methodList.param(org.springframework.ui.ModelMap.class, "model");
			methodList.param(javax.servlet.http.HttpServletRequest.class, "request");
			methodList.param(java.security.Principal.class, "principal");
			methodList.param(javax.servlet.http.HttpSession.class, "httpSession");
			methodList.annotate(org.springframework.web.bind.annotation.RequestMapping.class).param("value", "/" + webScreen.getUrl().toLowerCase());
			
			final String name = "openpage.java";
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("pageUrl", pageUrl);
			methodList.body().directStatement(TemplateLoader.loadJavaTemplate(name, params));
		}
		
		tmp.setMethodName(methodNameData);
		String urlTableData = "/table-" + webScreen.getUrl().toLowerCase()+ ".json";
		if (!screenGenerator.getClassLoaded().getListMethod().contains(tmp)) {
			JMethod methodListData = definedClass.method(JMod.PUBLIC,id.co.vico.appframe.model.TableData.class, methodNameData);
			methodListData.param(String.class, "echo").annotate(org.springframework.web.bind.annotation.RequestParam.class).param("value", "sEcho");
			methodListData.param(Integer.class, "offset").annotate(org.springframework.web.bind.annotation.RequestParam.class).param("value", "iDisplayStart");
			methodListData.param(Integer.class, "limit").annotate(org.springframework.web.bind.annotation.RequestParam.class).param("value", "iDisplayLength");
			methodListData.param(String.class, "search").annotate(org.springframework.web.bind.annotation.RequestParam.class).param("value", "sSearch").param("required", false);
			methodListData.annotate(org.springframework.web.bind.annotation.RequestMapping.class).param("value", urlTableData);
			methodListData.annotate(org.springframework.web.bind.annotation.ResponseBody.class);
			
			final String name = "table-data.json.java";
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("serviceName", serviceField);
			params.put("methodListPage", listExecutor.getMethodListPage());
			methodListData.body().directStatement(TemplateLoader.loadJavaTemplate(name, params));
		}

		Configuration cfg = new Configuration();
		String listTemplate = "src/main/resources/template-gen/list_edit_template.ftl";
		String listOutput = Scafollding.pagesFolder + File.separator + webScreen.getUrl() + ".jsp";
			
		// Load the template
		Template template = cfg.getTemplate(listTemplate);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("url", urlTableData);
		data.put("tablename", webScreen.getScreenName());
		data.put("url_new", urlNewForm.toLowerCase());
		data.put("url_update", urlEditForm.toLowerCase());
		data.put("url_list", webScreen.getUrl());
		data.put("url_delete", urlDeleteRow );

		List<String> columnScript = new ArrayList<String>();
		List<String> columnHtml = new ArrayList<String>();
		List<String> columnStyle = new ArrayList<String>();
		Class<?> cls = Class.forName(screenGenerator.getScafollding().getModelClass());
		List<Field> privateFields = new ArrayList<Field>();
		Field[] allFields = cls.getDeclaredFields();
		for (int idx = 0; idx < allFields.length; idx++) {
			Field fieldTmp = allFields[idx];
			if (fieldTmp.isAnnotationPresent(FrameListColumn.class)) {
				columnScript.add(fieldTmp.getName());
				columnHtml.add("<th class=\""
						+ fieldTmp.getName().toLowerCase() + "\">"
						+ StringUtil.splitCamelCase(fieldTmp.getName()).toUpperCase() + "</th>");
				columnStyle.add("." + fieldTmp.getName().toLowerCase()
						+ "{}");
				privateFields.add(fieldTmp);
				
				if (!fieldTmp.isAnnotationPresent(FrameListId.class)) {
					data.put("idScript", fieldTmp.getName());
					data.put("idfield", fieldTmp.getName());
				}
			}
		}

		data.put("columnScript", columnScript);
		data.put("columnHtml", columnHtml);
		data.put("columnStyle", columnStyle);

		Writer out = new OutputStreamWriter(new FileOutputStream(listOutput));
		template.process(data, out);
		out.flush();
	}
}
