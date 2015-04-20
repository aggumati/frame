package io.github.aggumati.frame.model;

import id.co.vico.appframe.model.JsonResponse;
import id.co.vico.appframe.model.Page;
import id.co.vico.appframe.model.TableData;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;

public class MethodGenerator {

	private String modifier;
	private String returnType;
	private String methodName;
	private List<String> statements;
	private List<FieldGenerator> params;
	private List<AnnotationGenerator> annotations;

	public MethodGenerator() {
		this.statements = new ArrayList<String>();
		this.params = new ArrayList<FieldGenerator>();
		this.annotations = new ArrayList<AnnotationGenerator>();
	}

	public List<String> getStatements() {
		return statements;
	}

	public void setStatements(List<String> listStatement) {
		this.statements = listStatement;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void addStatement(String lineStatement) {
		this.statements.add(lineStatement);
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public List<FieldGenerator> getParams() {
		return params;
	}

	public void setParams(List<FieldGenerator> params) {
		this.params = params;
	}
	
	public void addParam(FieldGenerator field) {
		this.params.add(field);
	}

	public List<AnnotationGenerator> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<AnnotationGenerator> annotations) {
		this.annotations = annotations;
	}
	
	public void addAnnotation(AnnotationGenerator field) {
		this.annotations.add(field);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MethodGenerator) {
			MethodGenerator objTmp = (MethodGenerator) obj;
			if (objTmp.getMethodName().equals(this.methodName)) {
				return true;
			}
		} 
		return false;
	}
	
	public Class<?> getTypeClass () {
		if ("String".equalsIgnoreCase(this.returnType)) {
			return String.class;
		} else if ("TableData".equalsIgnoreCase(this.returnType)) {
			return TableData.class;
		} else if ("ModelAndView".equalsIgnoreCase(returnType)) {
			return ModelAndView.class;
		} else if ("JsonResponse".equalsIgnoreCase(returnType)) {
			return JsonResponse.class;
		} else if ("Page".equalsIgnoreCase(returnType)) {
			return Page.class;
		} else if ("void".equalsIgnoreCase(returnType)) {
			return void.class;
		} else if ("ArrayList".equalsIgnoreCase(returnType)) {
			return ArrayList.class;
		} else if ("List".equalsIgnoreCase(returnType)) {
			return List.class;
		} else if ("Integer".equalsIgnoreCase(returnType)) {
			return Integer.class;
		}
		return String.class;
	}
}
