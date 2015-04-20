package io.github.aggumati.frame.model;

import id.co.vico.appframe.util.StringUtil;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public class AnnotationGenerator {
	private String name;
	private List<AnnotationParamGenerator> params;

	public AnnotationGenerator() {
		this.params = new ArrayList<AnnotationParamGenerator>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AnnotationParamGenerator> getParams() {
		return params;
	}

	public void setParams(List<AnnotationParamGenerator> params) {
		this.params = params;
	}
	
	public static final String TYPE_STRING = "string";
	public static final String TYPE_BOOLEAN = "boolean";
	public static final String TYPE_CLASS = "class";
	
	public void addParam(String name, String value) {
		value = value.replace("\"", "");
		String type = TYPE_STRING;
		if (StringUtil.isBoolean(value)) type = TYPE_BOOLEAN;
		else if (StringUtil.isJavaClass(value)) type = TYPE_CLASS;
		this.params.add(new AnnotationParamGenerator(name, type, value));
	}
	
	public void addParam(String value) {
		String type = TYPE_STRING;
		if (StringUtil.isBoolean(value)) type = TYPE_BOOLEAN;
		else if (StringUtil.isJavaClass(value)) type = TYPE_CLASS;
		this.params.add(new AnnotationParamGenerator("value", type, value));
	}

	public Class<? extends Annotation> getNameClass() {
		if ("RequestMapping".equalsIgnoreCase(this.name)) {
			return RequestMapping.class;
		} else if ("ResponseBody".equalsIgnoreCase(this.name)) {
			return ResponseBody.class;
		} else if ("ModelAttribute".equalsIgnoreCase(this.name)) {
			return ModelAttribute.class;
		} else if ("RequestParam".equalsIgnoreCase(this.name)) {
			return RequestParam.class;
		} else if ("Controller".equalsIgnoreCase(this.name)) {
			return Controller.class;
		} else if ("Service".equalsIgnoreCase(this.name)) {
			return Service.class;
		} else if ("Component".equalsIgnoreCase(this.name)) {
			return Component.class;
		} else if ("Override".equalsIgnoreCase(this.name)) {
			return Override.class;
		} else if ("Autowired".equalsIgnoreCase(this.name)) {
			return Autowired.class;
		} else if ("Transactional".equalsIgnoreCase(this.name)) {
			return Transactional.class;
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AnnotationGenerator) {
			AnnotationGenerator objTmp = (AnnotationGenerator) obj;
			if (objTmp.getName().equals(this.name)) {
				return true;
			}
		} 
		return false;
	}
}
