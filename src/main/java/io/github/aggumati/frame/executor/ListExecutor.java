package io.github.aggumati.frame.executor;

import freemarker.template.Configuration;
import freemarker.template.Template;
import id.co.vico.appframe.model.Page;
import id.co.vico.appframe.model.isys.WebScreen;
import id.co.vico.appframe.service.IService;
import id.co.vico.appframe.service.impl.AbstractService;
import id.co.vico.appframe.util.ClassUtil;
import id.co.vico.appframe.util.CommonUtil;
import id.co.vico.appframe.util.StringUtil;
import io.github.aggumati.frame.ClassLoaded;
import io.github.aggumati.frame.ScreenGenerator;
import io.github.aggumati.frame.TemplateLoader;
import io.github.aggumati.frame.list.FrameListColumn;
import io.github.aggumati.frame.model.AnnotationGenerator;
import io.github.aggumati.frame.model.FieldGenerator;
import io.github.aggumati.frame.model.MethodGenerator;
import io.github.aggumati.frame.model.Scafollding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class ListExecutor implements ScreenExecutor {

	private String serviceClassName;
	private String methodListPage;
	
	@Override
	public void execute(ScreenGenerator screenGenerator) throws Exception {
		WebScreen webScreen = screenGenerator.getWebScreen();
		JDefinedClass definedClass = screenGenerator.getDefinedClass();
		
		MethodGenerator tmp = new MethodGenerator();

		generateService(screenGenerator);
		
		// define class variable
		final String methodNameList = "list" + StringUtil.toCamelCase(webScreen.getUrl());
		final String methodNameData = "data" + StringUtil.toCamelCase(webScreen.getUrl());
		final String pageUrl = "page" + StringUtil.toCamelCase(webScreen.getUrl());
		final String serviceField = StringUtil.lowerFirst(screenGenerator.getScafollding().getServiceClassName());
		
		FieldGenerator fieldGen = new FieldGenerator(JMod.PRIVATE, pageUrl, "String" , "pages/" + webScreen.getUrl());
		if (!screenGenerator.getClassLoaded().getListFields().contains(fieldGen))definedClass.field(fieldGen.getModifier(), fieldGen.getTypeClass(), pageUrl, JExpr.lit("pages/" + webScreen.getUrl()));
		
		fieldGen = new FieldGenerator(JMod.PRIVATE, serviceField, serviceClassName , null);
		if (!screenGenerator.getClassLoaded().getListFields().contains(fieldGen))
			definedClass.field(fieldGen.getModifier(), fieldGen.getTypeClass(), serviceField).annotate(Autowired.class);

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
			params.put("methodListPage", methodListPage);
			methodListData.body().directStatement(TemplateLoader.loadJavaTemplate(name, params));
		}
		
		Configuration cfg = new Configuration();
		String listTemplate = "src/main/resources/template-gen/list_template.ftl";
		String listOutput = Scafollding.pagesFolder + File.separator + webScreen.getUrl() + ".jsp";
			
		// Load the template
		Template template = cfg.getTemplate(listTemplate);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("url", urlTableData);
		data.put("tablename", webScreen.getScreenName());

		List<String> columnScript = new ArrayList<String>();
		List<String> columnHtml = new ArrayList<String>();
		List<String> columnStyle = new ArrayList<String>();
		Class<?> cls = Class.forName(screenGenerator.getScafollding().getModelClass());
		List<Field> privateFields = new ArrayList<Field>();
		Field[] allFields = cls.getDeclaredFields();
		for (int idx = 0; idx < allFields.length; idx++) {
			Field fieldTmp = allFields[idx];
			
			if (fieldTmp.isAnnotationPresent(FrameListColumn.class)){
				columnScript.add(fieldTmp.getName());
				columnHtml.add("<th class=\""
						+ fieldTmp.getName().toLowerCase() + "\">"
						+ StringUtil.splitCamelCase(fieldTmp.getName()).toUpperCase() + "</th>");
				columnStyle.add("." + fieldTmp.getName().toLowerCase()
						+ "{}");
				privateFields.add(fieldTmp);
			}
		}

		data.put("columnScript", columnScript);
		data.put("columnHtml", columnHtml);
		data.put("columnStyle", columnStyle);

		Writer out = new OutputStreamWriter(new FileOutputStream(listOutput));
		template.process(data, out);
		out.flush();
	}
	
	public void generateService (ScreenGenerator screenGenerator) throws Exception {
		serviceClassName = Scafollding.packageGroup + "." + screenGenerator.getScafollding().getContext() + ".service." + screenGenerator.getScafollding().getServiceClassName();
		final String serviceFileName = String.format("%s\\%s.java", Scafollding.javaFolder, ClassUtil.classToFile(serviceClassName));
		
		// create service interface
		ClassLoaded classLoaded = new ClassLoaded(serviceFileName, serviceClassName, ClassType.INTERFACE);
		JDefinedClass serviceDefinedClass = classLoaded.openClass();
		serviceDefinedClass._extends(IService.class);
		MethodGenerator tmp = new MethodGenerator();
		
		final String methodCountListService = "count"+StringUtil.capitalizeFirst(screenGenerator.getScafollding().getModelClassName());
		tmp.setMethodName(methodCountListService);
		if (!classLoaded.getListMethod().contains(tmp)) {
			JMethod methodList = serviceDefinedClass.method(JMod.PUBLIC,Integer.class, methodCountListService);
			methodList.param(String.class, "searchVal");
		}
		
		final String methodNameListService = "findList"+StringUtil.capitalizeFirst(screenGenerator.getScafollding().getModelClassName())+"Page";
		this.methodListPage = methodNameListService;
		tmp.setMethodName(methodNameListService);
		if (!classLoaded.getListMethod().contains(tmp)) {
			JMethod methodList = serviceDefinedClass.method(JMod.PUBLIC,Page.class, methodNameListService);
			methodList.param(Integer.class, "offset");
			methodList.param(Integer.class, "limit");
			methodList.param(String.class, "searchVal");
		}
		
		final String methodFindListService = "findList"+StringUtil.capitalizeFirst(screenGenerator.getScafollding().getModelClassName());
		tmp.setMethodName(methodFindListService);
		if (!classLoaded.getListMethod().contains(tmp)) {
			JMethod methodList = serviceDefinedClass.method(JMod.PUBLIC,List.class, methodFindListService);
			methodList.param(Integer.class, "offset");
			methodList.param(Integer.class, "limit");
			methodList.param(String.class, "searchVal");
		}
		
		serviceDefinedClass.owner().build(new File(Scafollding.javaFolder));
		
		// Compile source file.
		ClassUtil.compileClass(serviceFileName);
		
		// create service impl
		final String serviceImplClassName = Scafollding.packageGroup + "." + screenGenerator.getScafollding().getContext() + ".service.impl." + screenGenerator.getScafollding().getServiceImplClassName();
		final String serviceImplFileName = String.format("%s\\%s.java", Scafollding.javaFolder, ClassUtil.classToFile(serviceImplClassName));
		
		ClassLoaded classLoadedImpl = new ClassLoaded(serviceImplFileName, serviceImplClassName);
		JDefinedClass serviceImplDefinedClass = classLoadedImpl.openClass();
		
		final String mapperName = screenGenerator.getScafollding().getMapperClassName();
		final String dummyList = "dummyList"+StringUtil.capitalizeFirst(screenGenerator.getScafollding().getModelClassName());
		
		if (CommonUtil.isNotNullOrEmpty(mapperName)) {
			FieldGenerator fieldGen = new FieldGenerator(JMod.PRIVATE, StringUtil.lowerFirst(mapperName), null, null);
			if (!classLoadedImpl.getListFields().contains(fieldGen))serviceImplDefinedClass.field(fieldGen.getModifier(), screenGenerator.getScafollding().getMapper(), StringUtil.lowerFirst(mapperName)).annotate(Autowired.class);
		} else {
			FieldGenerator fieldGen = new FieldGenerator(JMod.PRIVATE | JMod.STATIC, dummyList, null, null);
			if (!classLoadedImpl.getListFields().contains(fieldGen)){
				serviceImplDefinedClass.field(fieldGen.getModifier(), List.class, dummyList);
			}
		}

		serviceImplDefinedClass._implements(Class.forName(screenGenerator.getScafollding().getServiceClass()));
		serviceImplDefinedClass._extends(AbstractService.class);
		
		AnnotationGenerator gen = new AnnotationGenerator();
		gen.setName("Service");
		if (!classLoadedImpl.getListAnnotatios().contains(gen))
			serviceImplDefinedClass.annotate(Service.class);
		
		tmp.setMethodName(methodFindListService);
		if (!classLoadedImpl.getListMethod().contains(tmp)) {
			JMethod methodList = serviceImplDefinedClass.method(JMod.PUBLIC,List.class, methodFindListService);
			methodList.param(Integer.class, "offset");
			methodList.param(Integer.class, "limit");
			methodList.param(String.class, "searchVal");
			JAnnotationUse annotate = methodList.annotate(Transactional.class);
			annotate.param("readOnly", true);
			
			if (CommonUtil.isNotNullOrEmpty(mapperName)) {
				// TODO : provide another alternative if the class doesn't have any mapper class
				final String name = "findListBody.java";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("mapperClass", StringUtil.lowerFirst(mapperName));
				methodList.body().directStatement(TemplateLoader.loadJavaTemplate(name, params));
			} else {
				final String name = "dummyFindListBody.java";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("dummyListName", dummyList);
				params.put("modelClass", screenGenerator.getScafollding().getModelClass()+".class");
				methodList.body().directStatement(TemplateLoader.loadServiceTemplate(name, params));
			}
		}
		
		tmp.setMethodName(methodCountListService);
		if (!classLoadedImpl.getListMethod().contains(tmp)) {
			JMethod methodList = serviceImplDefinedClass.method(JMod.PUBLIC,Integer.class, methodCountListService);
			methodList.param(String.class, "searchVal");
			JAnnotationUse annotate = methodList.annotate(Transactional.class);
			annotate.param("readOnly", true);
			
			final String name = "findCountBody.java";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("listMethod", methodFindListService);
			methodList.body().directStatement(TemplateLoader.loadJavaTemplate(name, params));
		}
		
		tmp.setMethodName(methodNameListService);
		if (!classLoadedImpl.getListMethod().contains(tmp)) {
			JMethod methodList = serviceImplDefinedClass.method(JMod.PUBLIC,Page.class, methodNameListService);
			methodList.param(Integer.class, "offset");
			methodList.param(Integer.class, "limit");
			methodList.param(String.class, "searchVal");
			JAnnotationUse annotate = methodList.annotate(Transactional.class);
			annotate.param("readOnly", true);
			
			final String name = "findListPageBody.java";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("countMethod", methodCountListService);
			params.put("findMethod", methodFindListService);
			methodList.body().directStatement(TemplateLoader.loadJavaTemplate(name, params));
		}
		
		serviceImplDefinedClass.owner().build(new File(Scafollding.javaFolder));
		ClassUtil.compileClass(serviceImplFileName);
	}

	public String getServiceClassName() {
		return serviceClassName;
	}

	public String getMethodListPage() {
		return methodListPage;
	}
}
