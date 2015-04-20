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
import io.github.aggumati.frame.formtype.FrameFieldCheck;
import io.github.aggumati.frame.formtype.FrameFieldDate;
import io.github.aggumati.frame.formtype.FrameFieldDateTime;
import io.github.aggumati.frame.formtype.FrameFieldDropdown;
import io.github.aggumati.frame.formtype.FrameFieldEmail;
import io.github.aggumati.frame.formtype.FrameFieldFile;
import io.github.aggumati.frame.formtype.FrameFieldList;
import io.github.aggumati.frame.formtype.FrameFieldObject;
import io.github.aggumati.frame.formtype.FrameFieldPassword;
import io.github.aggumati.frame.formtype.FrameFieldRadio;
import io.github.aggumati.frame.formtype.FrameFieldTable;
import io.github.aggumati.frame.formtype.FrameFieldText;
import io.github.aggumati.frame.formtype.ItemCheck;
import io.github.aggumati.frame.formtype.ItemDropdown;
import io.github.aggumati.frame.formtype.ItemList;
import io.github.aggumati.frame.formtype.ItemRadio;
import io.github.aggumati.frame.list.FrameListColumnCheck;
import io.github.aggumati.frame.list.FrameListColumnDate;
import io.github.aggumati.frame.list.FrameListColumnDatetime;
import io.github.aggumati.frame.list.FrameListColumnDropdown;
import io.github.aggumati.frame.list.FrameListColumnFile;
import io.github.aggumati.frame.list.FrameListColumnText;
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
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class FormNewExecutor implements ScreenExecutor {
	
	private String serviceClassName;
	private boolean hasFileField;
	
	/**
	 * need custom.js; date field, date time field;
	 */
	private boolean hasCustomField;

	@Override
	public void execute(ScreenGenerator screenGenerator) throws Exception {
		WebScreen webScreen = screenGenerator.getWebScreen();
		JDefinedClass definedClass = screenGenerator.getDefinedClass();
		
		final String methodName = "form" + StringUtil.toCamelCase(webScreen.getUrl());
		final String actionMethodName = "save" + StringUtil.toCamelCase(webScreen.getUrl());
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

		List<FormGenerator> columnHtml = getColumnHtml(null, screenGenerator.getScafollding().getModelClass());
		data.put("columnHtml", columnHtml);
		data.put("hasFileField", hasFileField);
		data.put("hasCustomField", hasCustomField);

		Writer out = new OutputStreamWriter(new FileOutputStream(listOutput));
		template.process(data, out);
		out.flush();
	}
	
	public List<FormGenerator> getColumnHtml(String prefix, String fullClassName) throws Exception {
		List<FormGenerator> columnHtml = new ArrayList<FormGenerator>();
		Class<?> cls = Class.forName(fullClassName);
		List<Field> privateFields = new ArrayList<Field>();
		Field[] allFields = cls.getDeclaredFields();
		
		for (int idx = 0; idx < allFields.length; idx++) {
			Field fieldTmp = allFields[idx];
			
			String type = null;
			FormGenerator formGen = new FormGenerator();
			if (fieldTmp.isAnnotationPresent(FrameFieldText.class)) {
				type = "text";
			} else if (fieldTmp.isAnnotationPresent(FrameFieldPassword.class)) {
				type = "password";
			} else if (fieldTmp.isAnnotationPresent(FrameFieldEmail.class)) {
				type = "email";
				// check data type
			} else if (fieldTmp.isAnnotationPresent(FrameFieldDate.class)) {
				type = "date";
				// check data type 
				if (!fieldTmp.getType().equals(Date.class)) {
					throw new Exception(fieldTmp.getName() + " must be a java.util.Date type");
				}
				hasCustomField = true;
			} else if (fieldTmp.isAnnotationPresent(FrameFieldDateTime.class)) {
				type = "datetime";
				if (!fieldTmp.getType().equals(Date.class)) {
					throw new Exception(fieldTmp.getName() + " must be a java.util.Date type");
				}
				hasCustomField = true;
			} else if (fieldTmp.isAnnotationPresent(FrameFieldFile.class)) {
				type = "file";
				if (!fieldTmp.getType().equals(MultipartFile.class)) {
					throw new Exception(fieldTmp.getName() + " must be a org.springframework.web.multipart.MultipartFile type");
				}
				hasFileField = true;
			} else if (fieldTmp.isAnnotationPresent(FrameFieldRadio.class)) {
				type = "radio";
				if (!fieldTmp.getType().equals(String.class)) {
					throw new Exception(fieldTmp.getName() + " must be a java.lang.String type");
				}
				FrameFieldRadio radioField = fieldTmp.getAnnotation(FrameFieldRadio.class);
				for (int i = 0; i < radioField.value().length; i++) {
					ItemRadio item = radioField.value()[i];
					formGen.addKeyVal(item.key(), item.val());
				}
			} else if (fieldTmp.isAnnotationPresent(FrameFieldCheck.class)) {
				type = "check";
				FrameFieldCheck checkField = fieldTmp.getAnnotation(FrameFieldCheck.class);
				for (int i = 0; i < checkField.value().length; i++) {
					ItemCheck item = checkField.value()[i];
					formGen.addKeyVal(item.key(), item.val());
				}
			} else if (fieldTmp.isAnnotationPresent(FrameFieldDropdown.class)) {
				type = "dropdown";
				if (!fieldTmp.getType().equals(String.class)) {
					throw new Exception(fieldTmp.getName() + " must be a java.lang.String type");
				}
				FrameFieldDropdown dropdownField = fieldTmp.getAnnotation(FrameFieldDropdown.class);
				for (int i = 0; i < dropdownField.value().length; i++) {
					ItemDropdown item = dropdownField.value()[i];
					formGen.addKeyVal(item.key(), item.val());
				}
			} else if (fieldTmp.isAnnotationPresent(FrameFieldList.class)) {
				type = "list";
				if (!fieldTmp.getType().equals(List.class)) {
					throw new Exception(fieldTmp.getName() + " must be a java.util.List<String> type");
				}
				FrameFieldList listField = fieldTmp.getAnnotation(FrameFieldList.class);
				for (int i = 0; i < listField.value().length; i++) {
					ItemList item = listField.value()[i];
					formGen.addKeyVal(item.key(), item.val());
				}
			} else if (fieldTmp.isAnnotationPresent(FrameFieldObject.class)) {
				columnHtml.addAll(getColumnHtml(fieldTmp.getName(), fieldTmp.getType().getName()));
			} else if (fieldTmp.isAnnotationPresent(FrameFieldTable.class)) {
				type = "table";
				if (!fieldTmp.getType().equals(List.class)) {
					throw new Exception(fieldTmp.getName() + " must be a java.util.List type");
				}
				FrameFieldTable listField = fieldTmp.getAnnotation(FrameFieldTable.class);
				formGen.setSizeList(listField.page());
				Type genericFieldType = fieldTmp.getGenericType();
				Class<?> fieldArgClass = null;
				if(genericFieldType instanceof ParameterizedType){
					ParameterizedType aType = (ParameterizedType) genericFieldType;
					Type[] fieldArgTypes = aType.getActualTypeArguments();
					for(Type fieldArgType : fieldArgTypes){
						fieldArgClass = (Class<?>) fieldArgType;
					}
				}
				
				if (fieldArgClass == null) {
					throw new Exception("Plese use correct class field type");
				}
				
				formGen.setTable(getInnerTableForm(fieldTmp.getName(), fieldArgClass.getName()));
			}
			
			if (CommonUtil.isNotNullOrEmpty(type)) {
				if (CommonUtil.isNotNullOrEmpty(prefix)) {
					formGen.setName(
							StringUtil.splitCamelCase(StringUtil.lowerFirst( prefix)) + "." +
							StringUtil.splitCamelCase(StringUtil.lowerFirst( fieldTmp.getName())));
					
					if (CommonUtil.isNullOrEmpty(formGen.getLabel()))
						formGen.setLabel((prefix + " - " + StringUtil.splitCamelCaseWithSpace(fieldTmp.getName())).toUpperCase());
				} else {
					formGen.setName(StringUtil.splitCamelCase(StringUtil.lowerFirst( fieldTmp.getName())));
					
					if (CommonUtil.isNullOrEmpty(formGen.getLabel()))
						formGen.setLabel(StringUtil.splitCamelCaseWithSpace(fieldTmp.getName()).toUpperCase());
				}
				
				formGen.setType(type);
				columnHtml.add(formGen);
				privateFields.add(fieldTmp);
			}
		}
		
		return columnHtml;
	}
	
	private List<FormGenerator> getInnerTableForm(String prefix, String fullClassName) throws Exception {
		List<FormGenerator> columnHtml = new ArrayList<FormGenerator>();
		Class<?> cls = Class.forName(fullClassName);
		List<Field> privateFields = new ArrayList<Field>();
		Field[] allFields = cls.getDeclaredFields();
		
		for (int idx = 0; idx < allFields.length; idx++) {
			Field fieldTmp = allFields[idx];
			
			String type = null;
			FormGenerator formGen = new FormGenerator();
			if (fieldTmp.isAnnotationPresent(FrameListColumnText.class)) {
				type = "tabletext";
			} else if (fieldTmp.isAnnotationPresent(FrameListColumnDate.class)) {
				type = "tabledate";
				if (!fieldTmp.getType().equals(Date.class)) {
					throw new Exception(fieldTmp.getName() + " must be a java.util.Date type");
				}
				hasCustomField = true;
			} else if (fieldTmp.isAnnotationPresent(FrameListColumnDatetime.class)) {
				type = "tabledatetime";
				if (!fieldTmp.getType().equals(Date.class)) {
					throw new Exception(fieldTmp.getName() + " must be a java.util.Date type");
				}
				hasCustomField = true;
			} else if (fieldTmp.isAnnotationPresent(FrameListColumnFile.class)) {
				type = "tablefile";
				if (!fieldTmp.getType().equals(MultipartFile.class)) {
					throw new Exception(fieldTmp.getName() + " must be a org.springframework.web.multipart.MultipartFile type");
				}
				hasFileField = true;
			} else if (fieldTmp.isAnnotationPresent(FrameListColumnCheck.class)) {
				type = "tablecheck";
				FrameListColumnCheck tablecheck = fieldTmp.getAnnotation(FrameListColumnCheck.class);
				formGen.setValue(tablecheck.value());
			} else if (fieldTmp.isAnnotationPresent(FrameListColumnDropdown.class)) {
				type = "tabledropdown";
				if (!fieldTmp.getType().equals(String.class)) {
					throw new Exception(fieldTmp.getName() + " must be a java.lang.String type");
				}
				FrameListColumnDropdown dropdownField = fieldTmp.getAnnotation(FrameListColumnDropdown.class);
				for (int i = 0; i < dropdownField.value().length; i++) {
					ItemDropdown item = dropdownField.value()[i];
					formGen.addKeyVal(item.key(), item.val());
				}
			}
			
			if (CommonUtil.isNotNullOrEmpty(type)) {
				if (CommonUtil.isNotNullOrEmpty(prefix)) {
					formGen.setName(
							StringUtil.splitCamelCase(StringUtil.lowerFirst( prefix)) + "." +
							StringUtil.splitCamelCase(StringUtil.lowerFirst( fieldTmp.getName())));
				} else {
					formGen.setName(StringUtil.splitCamelCase(StringUtil.lowerFirst( fieldTmp.getName())));
				}
				
				if (CommonUtil.isNullOrEmpty(formGen.getLabel()))
					formGen.setLabel(StringUtil.splitCamelCaseWithSpace(fieldTmp.getName()).toUpperCase());
				
				formGen.setType(type);
				columnHtml.add(formGen);
				privateFields.add(fieldTmp);
			}
		}
		
		return columnHtml;
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

	public String getServiceClassName() {
		return serviceClassName;
	}

	public boolean isHasFileField() {
		return hasFileField;
	}

	public boolean isHasCustomField() {
		return hasCustomField;
	}
	
	

}
