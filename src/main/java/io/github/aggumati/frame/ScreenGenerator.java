package io.github.aggumati.frame;

import id.co.vico.appframe.dao.isys.WebScreenMapper;
import id.co.vico.appframe.model.isys.WebScreen;
import id.co.vico.appframe.service.FrameworkService;
import id.co.vico.appframe.util.StringUtil;
import io.github.aggumati.frame.executor.FormNewExecutor;
import io.github.aggumati.frame.executor.ListEditExecutor;
import io.github.aggumati.frame.executor.ListExecutor;
import io.github.aggumati.frame.executor.ScreenExecutor;
import io.github.aggumati.frame.model.AnnotationGenerator;
import io.github.aggumati.frame.model.Scafollding;
import io.github.aggumati.frame.test.model.TestModelForm;
import io.github.aggumati.frame.test.model.TestModelTable;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.sun.codemodel.JDefinedClass;

@Component
public class ScreenGenerator {
	private final Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private FrameworkService frameworkService;
	@Autowired
	private WebScreenMapper screenDao;
	
	public static void main(String[] args) {
		final ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring-service.xml","spring-service-gen.xml"});
		final ScreenGenerator classGenerator = context.getBean(ScreenGenerator.class);
		System.setProperty("java.home", System.getenv("JAVA_HOME"));
		
		Scafollding scafollding = new Scafollding("-103", ScafolldingType.LISTEDIT, TestModelTable.class);
		classGenerator.execute(scafollding);
		
//		Scafollding scafollding = new Scafollding("-101", ScafolldingType.LISTSIMPLE, TestModelTable.class);
//		classGenerator.execute(scafollding);
//		
		Scafollding scafollding2 = new Scafollding("-102", ScafolldingType.FORM, TestModelForm.class);
		classGenerator.execute(scafollding2);
	}
	
	private Scafollding scafollding;
	private ClassLoaded classLoaded;
	private WebScreen webScreen;
	private JDefinedClass definedClass;
	
	public Scafollding getScafollding() {
		return scafollding;
	}
	public ClassLoaded getClassLoaded() {
		return classLoaded;
	}
	public WebScreen getWebScreen() {
		return webScreen;
	}
	public JDefinedClass getDefinedClass() {
		return definedClass;
	}
	public void setWebScreen(WebScreen webScreen) {
		this.webScreen = webScreen;
	}
	
	private String ctrlName;
	private String controller;
	private String controllerFullName;
	
	public void execute (Scafollding scf) {
		log.info("wait .. generating");
		
		this.scafollding = scf;
		this.webScreen = screenDao.selectById(scafollding.getIdScreen());

		File newFile = new File(Scafollding.pagesFolder);
		if (!newFile.exists()) newFile.mkdirs();
		
		ctrlName = String.format("%sController.java", StringUtil.toCamelCase(scafollding.getControllerName()));
		controller = String.format("%s\\%s\\controller\\%s", Scafollding.packageGropFolder, scafollding.getContext(), ctrlName);
		controllerFullName = Scafollding.packageGroup + "." + scafollding.getContext() + ".controller." + StringUtil.toCamelCase(scafollding.getControllerName()) + "Controller";
		
		classLoaded = new ClassLoaded(controller, controllerFullName);
		try {
			this.definedClass = classLoaded.openClass();
			
			AnnotationGenerator gen = new AnnotationGenerator();
			gen.setName("Controller");
			if (!classLoaded.getListAnnotatios().contains(gen))
				definedClass.annotate(org.springframework.stereotype.Controller.class);
			
			gen.setName("RequestMapping");
			if (!classLoaded.getListAnnotatios().contains(gen))
				definedClass.annotate(org.springframework.web.bind.annotation.RequestMapping.class).param("value", "/");
			
			definedClass._extends(id.co.vico.appframe.controller.AbstractController.class);
			
			ScreenExecutor executor = null;
			switch (scafollding.getScafolldingType()) {
			case LIST:
				executor = new ListExecutor();
				break;
			case FORM:
				executor = new FormNewExecutor();
				break;
			case LISTEDIT:
				executor = new ListEditExecutor();
				break;
			default:
				break;
			}
			executor.execute(this);
			definedClass.owner().build(new File(Scafollding.javaFolder));
		} catch (Exception e) {
			log.error("error", e);
		}

		log.info("done.");
	}
	
	public void reload() {
		classLoaded = new ClassLoaded(controller, controllerFullName);
		try {
			this.definedClass = classLoaded.openClass();
			
			AnnotationGenerator gen = new AnnotationGenerator();
			gen.setName("Controller");
			if (!classLoaded.getListAnnotatios().contains(gen))
				definedClass.annotate(org.springframework.stereotype.Controller.class);
			
			gen.setName("RequestMapping");
			if (!classLoaded.getListAnnotatios().contains(gen))
				definedClass.annotate(org.springframework.web.bind.annotation.RequestMapping.class).param("value", "/");
			
			definedClass._extends(id.co.vico.appframe.controller.AbstractController.class);
			definedClass.owner().build(new File(Scafollding.javaFolder));
		} catch (IOException e) {
			e.printStackTrace();
		}

		log.info("done.");
	}
}
