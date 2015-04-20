package io.github.aggumati.frame.model;

import id.co.vico.appframe.util.ClassUtil;
import id.co.vico.appframe.util.CommonUtil;
import id.co.vico.appframe.util.StringUtil;
import io.github.aggumati.frame.ScafolldingType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class Scafollding {
	
	public static final String packageGroup = "id.co.vico";
	
	public static final String pomFile = String.format("%s%spom.xml", System.getProperty("user.dir"),File.separator);
	public static final String srcFolder = String.format("%s%ssrc", System.getProperty("user.dir"),File.separator);
	public static final String binFolder = String.format("%s%s%starget%sclasses", System.getProperty("user.dir"),File.separator,File.separator,File.separator);
	public static final String mainFolder = String.format("%s%smain", srcFolder, File.separator);
	public static final String webAppFolder = String.format("%s%swebapp", mainFolder, File.separator);
	public static final String pagesFolder = String.format("%s%spages", webAppFolder, File.separator);
	public static final String javaFolder = String.format("%s%sjava", mainFolder, File.separator);
	public static final String packageGropFolder = String.format("%s%s%s", javaFolder, File.separator, StringUtil.packageToFolder(packageGroup));
	
	private String controllerName;
	private String context;
	private String idScreen;
	private ScafolldingType scafolldingType;
	private String modelClass;
	private Class<?> model;
	private String serviceClass;
	private Class<?> service;
	private String serviceImplClass;
	private Class<?> serviceImpl;
	private String mapperClass;
	private Class<?> mapper;
	
	public Scafollding (String idScreen, ScafolldingType scafolldingType, Class<?> modelClass) {
		this.idScreen = idScreen;
		this.modelClass = modelClass.getName();
		this.scafolldingType = scafolldingType;
		this.model = modelClass;
		try {
			FileReader reader = new FileReader(pomFile);
			MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
		    Model model = xpp3Reader.read(reader);
		    this.context = StringUtil.capitalizeFirst(model.getProperties().getProperty("app.context"));
		    this.controllerName = StringUtil.capitalizeFirst(this.context);
		    this.context = this.context.toLowerCase();
		    this.serviceClass =  Scafollding.packageGroup + "."+this.context +".service." +StringUtil.capitalizeFirst(this.context) + "Service";
		    this.serviceImplClass =  Scafollding.packageGroup + "."+this.context +".service.impl." +StringUtil.capitalizeFirst(this.context) + "ServiceImpl";
		    this.mapperClass = ClassUtil.getFullClassName(Scafollding.packageGroup, getModelClassName()+"Mapper");
		    
		    try {
				this.service = Class.forName(serviceClass).getClass();
			} catch (ClassNotFoundException e) {}
		    try {
				this.serviceImpl = Class.forName(serviceImplClass).getClass();
			} catch (ClassNotFoundException e) {}
		    if (CommonUtil.isNotNullOrEmpty(this.mapperClass)) {
		    	try {
					this.mapper = Class.forName(this.mapperClass);
				} catch (ClassNotFoundException e) {}
		    }
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (XmlPullParserException e) {}
	}
	
	public Scafollding (String idScreen, ScafolldingType scafolldingType, Class<?> modelClass, Class<?> controllerClass) {
		this(idScreen, scafolldingType, modelClass);
		this.controllerName = controllerClass.getName();
		String tmp = controllerName.split(".")[controllerName.split(".").length-1].replace("Controller", "Service");
		this.serviceClass = packageGroup + "."+this.context+".service." + tmp;
		this.serviceImplClass = packageGroup + "."+this.context+".service.impl." + tmp + "Impl";
		try {
			this.service = Class.forName(serviceClass).getClass();
		} catch (ClassNotFoundException e) {}
		try {
			this.serviceImpl = Class.forName(serviceImplClass).getClass();
		} catch (ClassNotFoundException e) {}
	}
	
	public Scafollding (String idScreen, ScafolldingType scafolldingType, Class<?> modelClass, String controllerClass) {
		this(idScreen, scafolldingType, modelClass);
		this.controllerName = packageGroup + "."+this.context+".controller." + StringUtil.capitalizeFirst(StringUtil.toCamelCase(controllerClass)) + "Controller";
		this.serviceClass = packageGroup + "."+this.context+".service." + StringUtil.capitalizeFirst(StringUtil.toCamelCase(controllerClass)) + "Service";
		this.serviceImplClass = packageGroup + "."+this.context+".service.impl." + StringUtil.capitalizeFirst(StringUtil.toCamelCase(controllerClass)) + "ServiceImpl";
		try {
			this.service = Class.forName(serviceClass).getClass();
			this.serviceImpl = Class.forName(serviceImplClass).getClass();
		} catch (ClassNotFoundException e) {
		} 
	}
	
	public Scafollding (String idScreen, ScafolldingType scafolldingType, Class<?> modelClass, Class<?> controllerClass, Class<?> serviceClass) {
		this(idScreen, scafolldingType, modelClass, controllerClass);
		this.service = serviceClass;
		this.serviceClass = serviceClass.getName();
	}

	public String getControllerName() {
		return controllerName;
	}

	public String getContext() {
		return context;
	}

	public String getIdScreen() {
		return idScreen;
	}

	public ScafolldingType getScafolldingType() {
		return scafolldingType;
	}

	public String getModelClass() {
		return modelClass;
	}

	public Class<?> getModel() {
		return model;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public Class<?> getService() {
		return service;
	}

	public String getServiceImplClass() {
		return serviceImplClass;
	}

	public Class<?> getServiceImpl() {
		return serviceImpl;
	}

	public String getMapperClass() {
		return mapperClass;
	}

	public Class<?> getMapper() {
		return mapper;
	}

	public String getModelClassName() {
		if (CommonUtil.isNotNullOrEmpty(getModelClass())) {
			if (getModelClass().indexOf(".") >0) {
				return getModelClass().split("\\.")[getModelClass().split("\\.").length-1];
			} else {
				return getModelClass();
			}
		}
		return null;
	}
	
	public String getServiceClassName() {
		if (CommonUtil.isNotNullOrEmpty(getServiceClass())) {
			if (getServiceClass().indexOf(".") >0) {
				return getServiceClass().split("\\.")[getServiceClass().split("\\.").length-1];
			} else {
				return getServiceClass();
			}
		}
		return null;
	}
	
	public String getServiceImplClassName() {
		if (CommonUtil.isNotNullOrEmpty(getServiceImplClass())) {
			if (getServiceImplClass().indexOf(".") >0) {
				return getServiceImplClass().split("\\.")[getServiceImplClass().split("\\.").length-1];
			} else {
				return getServiceImplClass();
			}
		}
		return null;
	}
	
	public String getMapperClassName() {
		if (CommonUtil.isNotNullOrEmpty(getMapperClass())) {
			if (getMapperClass().indexOf(".") >0) {
				return getMapperClass().split("\\.")[getMapperClass().split("\\.").length-1];
			} else {
				return getMapperClass();
			}
		}
		return null;
	}
}
