package io.github.aggumati.frame.model;

import id.co.vico.appframe.util.ClassUtil;
import id.co.vico.appframe.util.CommonUtil;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

public class FieldGenerator {
	private int modifier;
	private String name;
	private String type;
	private String initVal;
	private List<AnnotationGenerator> annotations;

	public FieldGenerator(int modifier, String name, String type, String initVal) {
		this.modifier = modifier;
		this.name = name;
		this.type = type;
		this.initVal = initVal;
		this.annotations = new ArrayList<AnnotationGenerator>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInitVal() {
		return initVal;
	}
	public void setInitVal(String initVal) {
		this.initVal = initVal;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FieldGenerator) {
			FieldGenerator objTmp = (FieldGenerator) obj;
			if (objTmp.getName().equals(this.name)) {
				return true;
			}
		} 
		return false;
	}
	
	public Class<?> getTypeClass () {
		if ("String".equalsIgnoreCase(this.type)) {
			return String.class;
		} else if ("List".equalsIgnoreCase(this.type)) {
			return List.class;
		} else if ("ArrayList".equalsIgnoreCase(this.type)) {
			return ArrayList.class;
		} else if ("Integer".equalsIgnoreCase(this.type)) {
			return Integer.class;
		} else if ("Double".equalsIgnoreCase(this.type)) {
			return Double.class;
		} else if ("Float".equalsIgnoreCase(this.type)) {
			return Float.class;
		} else if ("int".equalsIgnoreCase(this.type)) {
			return int.class;
		} else if ("BindingResult".equalsIgnoreCase(this.type)) {
			return BindingResult.class;
		} else if ("HttpSession".equalsIgnoreCase(this.type)) {
			return HttpSession.class;
		} else if ("Principal".equalsIgnoreCase(this.type)) {
			return Principal.class;
		} else if ("HttpServletRequest".equalsIgnoreCase(this.type)) {
			return HttpServletRequest.class;
		} else if ("ModelMap".equalsIgnoreCase(this.type)) {
			return ModelMap.class;
		} else {
			Class<?> classTmp =  null;
			try {
				String tmp = ClassUtil.getFullClassName(Scafollding.packageGroup, this.type);
				
				if (CommonUtil.isNotNullOrEmpty(tmp))
					classTmp = Class.forName(tmp);
				else 
					classTmp = Class.forName(this.type);
			} catch (ClassNotFoundException e) {
				try {
					classTmp =  Class.forName(this.type);
				} catch (ClassNotFoundException e1) {}
			}
			return classTmp;
		}
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

	public int getModifier() {
		return modifier;
	}

	public void setModifier(int modifier) {
		this.modifier = modifier;
	}
}
