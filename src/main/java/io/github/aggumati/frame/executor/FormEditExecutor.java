package io.github.aggumati.frame.executor;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import id.co.vico.appframe.model.JsonResponse;
import id.co.vico.appframe.model.isys.WebScreen;
import id.co.vico.appframe.service.IService;
import id.co.vico.appframe.service.impl.AbstractService;
import id.co.vico.appframe.util.ClassUtil;
import id.co.vico.appframe.util.CommonUtil;
import id.co.vico.appframe.util.StringUtil;
import io.github.aggumati.frame.ClassLoaded;
import io.github.aggumati.frame.ScreenGenerator;
import io.github.aggumati.frame.TemplateLoader;
import io.github.aggumati.frame.model.AnnotationGenerator;
import io.github.aggumati.frame.model.FieldGenerator;
import io.github.aggumati.frame.model.FormGenerator;
import io.github.aggumati.frame.model.MethodGenerator;
import io.github.aggumati.frame.model.Scafollding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class FormEditExecutor  implements ScreenExecutor {
	
	private String serviceClassName;

	@Override
	public void execute(ScreenGenerator screenGenerator)
			throws Exception {
		WebScreen webScreen = screenGenerator.getWebScreen();
		JDefinedClass definedClass = screenGenerator.getDefinedClass();
		
		final String methodName = "form" + StringUtil.toCamelCase(webScreen.getUrl());
		final String actionMethodName = "update" + StringUtil.toCamelCase(webScreen.getUrl());
		final String pageUrl = "page" + StringUtil.toCamelCase(webScreen.getUrl());
		final String service = StringUtil.lowerFirst(screenGenerator.getScafollding().getServiceClassName());
		
		generateService(screenGenerator);
		
		FieldGenerator fieldGen = new FieldGenerator(JMod.PRIVATE,pageUrl, "String" , "pages/" + webScreen.getUrl());
		if (!screenGenerator.getClassLoaded().getListFields().contains(fieldGen))definedClass.field(fieldGen.getModifier(), fieldGen.getTypeClass(), pageUrl, JExpr.lit("pages/" + webScreen.getUrl()));
		
		fieldGen = new FieldGenerator(JMod.PRIVATE,service, serviceClassName , null);
		if (!screenGenerator.getClassLoaded().getListFields().contains(fieldGen))
			definedClass.field(fieldGen.getModifier(), fieldGen.getTypeClass(), service).annotate(Autowired.class);
		
		MethodGenerator tmp = new MethodGenerator();
		tmp.setMethodName(methodName);
		if (!screenGenerator.getClassLoaded().getListMethod().contains(tmp)) {
			JMethod methodForm = definedClass.method(JMod.PUBLIC,org.springframework.web.servlet.ModelAndView.class, methodName);
			methodForm.param(org.springframework.ui.ModelMap.class, "model");
			methodForm.param(javax.servlet.http.HttpServletRequest.class, "request");
			methodForm.param(java.security.Principal.class, "principal");
			methodForm.param(javax.servlet.http.HttpSession.class, "httpSession");
			methodForm.annotate(org.springframework.web.bind.annotation.RequestMapping.class).param("value", "/" + webScreen.getUrl().toLowerCase());
			
			final String name = "openpage.java";
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("pageUrl", pageUrl);
			methodForm.body().directStatement(TemplateLoader.loadJavaTemplate(name, params));
		} 
		
		tmp.setMethodName(actionMethodName);
		String urlTableData = "/" + actionMethodName + ".do";
		if (!screenGenerator.getClassLoaded().getListMethod().contains(tmp)) {
			JMethod methodListData = definedClass.method(JMod.PUBLIC,id.co.vico.appframe.model.JsonResponse.class, actionMethodName);
			methodListData.param(Class.forName(screenGenerator.getScafollding().getModelClass()), "dataForm").annotate(org.springframework.web.bind.annotation.ModelAttribute.class).param("value", "dataForm");
			methodListData.param(BindingResult.class, "bindingResult");
			methodListData.annotate(org.springframework.web.bind.annotation.RequestMapping.class).param("value", urlTableData);
			methodListData.annotate(org.springframework.web.bind.annotation.ResponseBody.class);
			
			final String name = "insertDataCtrlBody.java";
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("service", service);
			params.put("model", screenGenerator.getScafollding().getModelClassName());
			methodListData.body().directStatement(TemplateLoader.loadJavaTemplate(name, params));
		}
		
		Configuration cfg = new Configuration();
		String listTemplate = "src/main/resources/template-gen/form_template.ftl";
		String listOutput = Scafollding.pagesFolder + File.separator + webScreen.getUrl() + ".jsp";
			
		// Load the template
		Template template = cfg.getTemplate(listTemplate);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("url", urlTableData);
		data.put("form_name", webScreen.getScreenName());

		FormNewExecutor newExecutor = new FormNewExecutor();
		List<FormGenerator> columnHtml = newExecutor.getColumnHtml(null, screenGenerator.getScafollding().getModelClass());
		data.put("columnHtml", columnHtml);
		data.put("hasFileField", newExecutor.isHasFileField());
		data.put("hasCustomField", newExecutor.isHasCustomField());

		Writer out = new OutputStreamWriter(new FileOutputStream(listOutput));
		template.process(data, out);
		out.flush();
		
	}
	
	private void generateService(ScreenGenerator screenGenerator)  throws ClassNotFoundException, IOException, TemplateException {
		serviceClassName = Scafollding.packageGroup + "." + screenGenerator.getScafollding().getContext() + ".service." + screenGenerator.getScafollding().getServiceClassName();
		final String serviceFileName = String.format("%s\\%s.java", Scafollding.javaFolder, ClassUtil.classToFile(serviceClassName));
		
		// create service interface
		ClassLoaded classLoaded = new ClassLoaded(serviceFileName, serviceClassName, ClassType.INTERFACE);
		JDefinedClass serviceDefinedClass = classLoaded.openClass();
		serviceDefinedClass._extends(IService.class);
		MethodGenerator tmp = new MethodGenerator();
		
		final String methodInsert = "insert"+StringUtil.capitalizeFirst(screenGenerator.getScafollding().getModelClassName());
		tmp.setMethodName(methodInsert);
		if (!classLoaded.getListMethod().contains(tmp)) {
			JMethod methodList = serviceDefinedClass.method(JMod.PUBLIC,JsonResponse.class, methodInsert);
			methodList.param(screenGenerator.getScafollding().getModel(), "model");
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
		if (CommonUtil.isNotNullOrEmpty(mapperName)) {
			FieldGenerator fieldGen = new FieldGenerator(JMod.PRIVATE,StringUtil.lowerFirst(mapperName), null, null);
			if (!classLoadedImpl.getListFields().contains(fieldGen))serviceImplDefinedClass.field(JMod.PRIVATE, screenGenerator.getScafollding().getMapper(), StringUtil.lowerFirst(mapperName)).annotate(Autowired.class);
		}
		
		serviceImplDefinedClass._implements(Class.forName(screenGenerator.getScafollding().getServiceClass()));
		serviceImplDefinedClass._extends(AbstractService.class);
		
		AnnotationGenerator gen = new AnnotationGenerator();
		gen.setName("Service");
		if (!classLoadedImpl.getListAnnotatios().contains(gen))
			serviceImplDefinedClass.annotate(Service.class);
		
		tmp.setMethodName(methodInsert);
		if (!classLoaded.getListMethod().contains(tmp)) {
			JMethod methodList = serviceImplDefinedClass.method(JMod.PUBLIC,JsonResponse.class, methodInsert);
			methodList.param(screenGenerator.getScafollding().getModel(), "model");
			JAnnotationUse annotate = methodList.annotate(Transactional.class);
			annotate.param("rollbackFor", Exception.class);
			
			if (CommonUtil.isNotNullOrEmpty(mapperName)) {
				final String name = "insertData.java";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("mapperClass", StringUtil.lowerFirst(mapperName));
				methodList.body().directStatement(TemplateLoader.loadJavaTemplate(name, params));
			} else {
				final String name = "insertDataNoMapper.java";
				methodList.body().directStatement(TemplateLoader.loadJavaTemplate(name));
			}
		}
		
		serviceImplDefinedClass.owner().build(new File(Scafollding.javaFolder));
		ClassUtil.compileClass(serviceImplFileName);
	}

}
