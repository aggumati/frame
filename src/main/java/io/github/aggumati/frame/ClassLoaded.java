package io.github.aggumati.frame;

import id.co.vico.appframe.util.CommonUtil;
import io.github.aggumati.frame.model.AnnotationGenerator;
import io.github.aggumati.frame.model.AnnotationParamGenerator;
import io.github.aggumati.frame.model.FieldGenerator;
import io.github.aggumati.frame.model.MethodGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;


/**
 * open class for manipulating usign jcodemodel
 * @author AnggunG
 *
 */
public class ClassLoaded {
	
	private boolean isExist = false;
	private ArrayList<FieldGenerator> listFields = new ArrayList<FieldGenerator>();
	private ArrayList<MethodGenerator> listMethod = new ArrayList<MethodGenerator>();
	private ArrayList<AnnotationGenerator> listAnnotatios = new ArrayList<AnnotationGenerator>();
	
	public boolean isExist() {
		return isExist;
	}
	public ArrayList<FieldGenerator> getListFields() {
		return listFields;
	}
	public ArrayList<MethodGenerator> getListMethod() {
		return listMethod;
	}
	public ArrayList<AnnotationGenerator> getListAnnotatios() {
		return listAnnotatios;
	}

	private String javaFile;
	private String targetClassName;
	private ClassType type;
	
	public String getJavaFile() {
		return javaFile;
	}
	public String getTargetClassName() {
		return targetClassName;
	}
	public ClassLoaded(String javaFile, String targetClassName) {
		this.javaFile = javaFile;
		this.targetClassName = targetClassName;
		this.type = ClassType.CLASS;
	}
	
	public ClassLoaded(String javaFile, String targetClassName, ClassType type) {
		this.javaFile = javaFile;
		this.targetClassName = targetClassName;
		this.type = type;
	}

	/**
	 * 
	 * @param javaFile C:/test/test.java 
	 * @param targetClassName test.Test.java
	 * @return
	 */
	public JDefinedClass openClass() {
		JCodeModel codeModel = new JCodeModel();
		CompilationUnit cu=null;
		FileInputStream in =null;
		try {
			File fileController = new File(javaFile);
			isExist = fileController.exists();
			
			if (isExist) {
				in = new FileInputStream(fileController);
				cu = JavaParser.parse(in);
				
				List<TypeDeclaration> types = cu.getTypes();
		        for (TypeDeclaration type : types) {
		        	List<AnnotationExpr> listAnnotations = type.getAnnotations();
		        	for (AnnotationExpr annotate : listAnnotations) {
		        		AnnotationGenerator annotateGen = new AnnotationGenerator();
                		annotateGen.setName(annotate.getName().toString());
                		int i = 0;
                		for (Node node : annotate.getChildrenNodes()) {
                			if (i > 0) {
	                			if (node.toString().contains("=")) {
	                				String[] arrayTmp = node.toString().split("=");
	                				String varName = arrayTmp[0].trim();
	                				String varVal = arrayTmp[1].trim();
	                				annotateGen.addParam(varName, varVal);
	                				
	                			} else {
	                				annotateGen.addParam(node.toString());
	                			}
                			}
                			i++;
						} 
                		listAnnotatios.add(annotateGen);
					}
		        	
		        	List<BodyDeclaration> members = type.getMembers();
		            for (BodyDeclaration member : members) {
		                if (member instanceof MethodDeclaration) {
		                    MethodDeclaration method = (MethodDeclaration) member;
		                    MethodGenerator gen = new MethodGenerator();
		                    gen.setMethodName(method.getName());
		                    gen.setReturnType(method.getType().toString());
		                    
		                    List<AnnotationExpr> list = method.getAnnotations();
		                	for (AnnotationExpr annotate : list) {
		                		AnnotationGenerator fieldGen = new AnnotationGenerator();
		                		fieldGen.setName(annotate.getName().toString());
		                		int i = 0;
		                		for (Node node : annotate.getChildrenNodes()) {
		                			if (i > 0) {
			                			if (node.toString().contains("=")) {
			                				String[] arrayTmp = node.toString().split("=");
			                				String varName = arrayTmp[0].trim();
			                				String varVal = arrayTmp[1].trim();
			                				fieldGen.addParam(varName, varVal);
			                				
			                			} else {
			                				fieldGen.addParam(node.toString());
			                			}
		                			}
		                			i++;
								} 
		                		gen.addAnnotation(fieldGen);
							}
		                	
		                	List<Parameter> listparam = method.getParameters();
		                	if (CommonUtil.isNotNullOrEmpty(listparam)) {
		                		for (Parameter parameter : listparam) {
		                			FieldGenerator fieldGen = new FieldGenerator(JMod.PRIVATE, parameter.getId().toString(), parameter.getType().toString(), null);
		                			
		                			List<AnnotationExpr> listParamAnnotation = parameter.getAnnotations();
		                			if (CommonUtil.isNotNullOrEmpty(listParamAnnotation)) {
		                				for (AnnotationExpr annotate : listParamAnnotation) {
					                		AnnotationGenerator fieldGenTmp = new AnnotationGenerator();
					                		fieldGenTmp.setName(annotate.getName().toString());
					                		int i = 0;
					                		for (Node node : annotate.getChildrenNodes()) {
					                			if (i > 0) {
						                			if (node.toString().contains("=")) {
						                				String[] arrayTmp = node.toString().split("=");
						                				String varName = arrayTmp[0].trim();
						                				String varVal = arrayTmp[1].trim();
						                				fieldGenTmp.addParam(varName, varVal);
						                				
						                			} else {
						                				fieldGenTmp.addParam(node.toString());
						                			}
					                			}
					                			i++;
											} 
					                		fieldGen.addAnnotation(fieldGenTmp);
										}
		                			};
		                			gen.addParam(fieldGen);
								}
		                	}
		                	
		                	if (CommonUtil.isNotNullOrEmpty(method.getBody())) {
		                		List<Statement> listStatement = method.getBody().getStmts();
		                		if (CommonUtil.isNotNullOrEmpty(listStatement)) {
		                			for (Statement statement : listStatement) {
				                    	gen.addStatement(statement.toStringWithoutComments());
									}
		                		}
		                	}
		                   
		                    listMethod.add(gen);
		                } else if (member instanceof FieldDeclaration) {
		                	FieldDeclaration field = (FieldDeclaration) member;
		                	VariableDeclarator var = field.getVariables().get(0);
		                	FieldGenerator fieldGenTmp = null;
		                	if (var.getInit() != null) {
		                		fieldGenTmp = new FieldGenerator(getModifier(field.toString()),var.getId().toString(), field.getType().toString(), var.getInit().toString());
		                	} else {
		                		fieldGenTmp = new FieldGenerator(getModifier(field.toString()),var.getId().toString(), field.getType().toString(), null);
		                	}
		                	
		                	List<AnnotationExpr> list = field.getAnnotations();
		                	for (AnnotationExpr annotate : list) {
		                		AnnotationGenerator fieldGen = new AnnotationGenerator();
		                		fieldGen.setName(annotate.getName().toString());
		                		int i = 0;
		                		for (Node node : annotate.getChildrenNodes()) {
		                			if (i > 0) {
			                			if (node.toString().contains("=")) {
			                				String[] arrayTmp = node.toString().split("=");
			                				String varName = arrayTmp[0].trim();
			                				String varVal = arrayTmp[1].trim();
			                				fieldGen.addParam(varName, varVal);
			                				
			                			} else {
			                				fieldGen.addParam(node.toString());
			                			}
		                			}
		                			i++;
								} 
		                		fieldGenTmp.addAnnotation(fieldGen);
							}
		                	
		                	listFields.add(fieldGenTmp);
		                }
		            }
		        }
			}
			
			JDefinedClass definedClass = codeModel._class(targetClassName, type);
			if (isExist) {
				if (CommonUtil.isNotNullOrEmpty(listAnnotatios)) {
					for (AnnotationGenerator annotateGen : listAnnotatios) {
						if (CommonUtil.isNotNullOrEmpty(annotateGen)) {
							JAnnotationUse annotation = definedClass.annotate(annotateGen.getNameClass());
							if (CommonUtil.isNotNullOrEmpty(annotateGen.getParams())) 
								for (AnnotationParamGenerator map : annotateGen.getParams()) 
									if (map.getType().equalsIgnoreCase(AnnotationGenerator.TYPE_BOOLEAN))
										annotation.param(map.getName(), BooleanUtils.toBoolean(map.getValue()));
									else if (map.getType().equalsIgnoreCase(AnnotationGenerator.TYPE_CLASS)) {
										try {
											annotation.param(map.getName(), Class.forName(map.getValue()));
										} catch (ClassNotFoundException e) {
											e.printStackTrace();
										}
									}
									else 
										annotation.param(map.getName(), map.getValue());
						}
					}
				}
			}

			for (FieldGenerator fieldG : listFields) {
				JFieldVar var;
				if (CommonUtil.isNotNullOrEmpty(fieldG.getInitVal())) {
					var = definedClass.field(fieldG.getModifier(), fieldG.getTypeClass(), fieldG.getName().replace("\"", ""), JExpr.lit(fieldG.getInitVal().replace("\"", "")));
				} else {
					var = definedClass.field(fieldG.getModifier(), fieldG.getTypeClass(), fieldG.getName().replace("\"", ""));
				}
				
				if (CommonUtil.isNotNullOrEmpty(fieldG.getAnnotations())) {
					for (AnnotationGenerator annotateGen : fieldG.getAnnotations()) {
						JAnnotationUse annotation = var.annotate(annotateGen.getNameClass());
						if (CommonUtil.isNotNullOrEmpty(annotateGen.getParams())) 
							for (AnnotationParamGenerator obj : annotateGen.getParams()) 
								if (obj.getType().equalsIgnoreCase(AnnotationGenerator.TYPE_BOOLEAN))
									annotation.param(obj.getName(), BooleanUtils.toBoolean(obj.getValue()));
								else if (obj.getType().equalsIgnoreCase(AnnotationGenerator.TYPE_CLASS)) {
									try {
										annotation.param(obj.getName(), Class.forName(obj.getValue().replace(".class", "")));
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
								}
								else 
									annotation.param(obj.getName(), obj.getValue());
					}
				}
			}
			
			if (isExist) {
				for (MethodGenerator methodGen : listMethod) {
					JMethod methodForm = definedClass.method(JMod.PUBLIC,methodGen.getTypeClass(), methodGen.getMethodName());
					for (String fieldGenerator : methodGen.getStatements()) {
						methodForm.body().directStatement(fieldGenerator);
					}
					
					for (FieldGenerator fieldGenerator : methodGen.getParams()) {
						JVar var = methodForm.param(fieldGenerator.getTypeClass(), fieldGenerator.getName());
						if (CommonUtil.isNotNullOrEmpty(fieldGenerator.getAnnotations())) {
							for (AnnotationGenerator annotateGen : fieldGenerator.getAnnotations()) {
								JAnnotationUse annotation = var.annotate(annotateGen.getNameClass());
								if (CommonUtil.isNotNullOrEmpty(annotateGen.getParams())) 
									for (AnnotationParamGenerator obj : annotateGen.getParams()) {
										if (obj.getType().equalsIgnoreCase(AnnotationGenerator.TYPE_BOOLEAN))
											annotation.param(obj.getName(), BooleanUtils.toBoolean(obj.getValue()));
										else if (obj.getType().equalsIgnoreCase(AnnotationGenerator.TYPE_CLASS)) {
											try {
												annotation.param(obj.getName(), Class.forName(obj.getValue().replace(".class", "")));
											} catch (ClassNotFoundException e) {
												e.printStackTrace();
											}
										}
										else 
											annotation.param(obj.getName(), obj.getValue());
									}
							}
						}
					}
					
					for (AnnotationGenerator annotateGen : methodGen.getAnnotations()) {
						JAnnotationUse annotation = methodForm.annotate(annotateGen.getNameClass());
						if (CommonUtil.isNotNullOrEmpty(annotateGen.getParams())) 
							for (AnnotationParamGenerator map : annotateGen.getParams()) {
								if (map.getType().equalsIgnoreCase(AnnotationGenerator.TYPE_BOOLEAN))
									annotation.param(map.getName(), BooleanUtils.toBoolean(map.getValue()));
								else if (map.getType().equalsIgnoreCase(AnnotationGenerator.TYPE_CLASS)) {
									try {
										annotation.param(map.getName(), Class.forName(map.getValue().replace(".class", "")));
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
								}
								else 
									annotation.param(map.getName(), map.getValue());
							}
					}
				}
			}
			if (isExist()) in.close();
			return definedClass;
		} catch (JClassAlreadyExistsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}
	public void setListFields(ArrayList<FieldGenerator> listFields) {
		this.listFields = listFields;
	}
	public void setListMethod(ArrayList<MethodGenerator> listMethod) {
		this.listMethod = listMethod;
	}
	public void setListAnnotatios(ArrayList<AnnotationGenerator> listAnnotatios) {
		this.listAnnotatios = listAnnotatios;
	}
	private int getModifier(String fullFieldName) {
		int modifier;
		
		if (fullFieldName.contains("public ")) {
			modifier = JMod.PUBLIC;
		} else if (fullFieldName.contains("private ")) {
			modifier = JMod.PRIVATE;
		} else if (fullFieldName.contains("protected ")) {
			modifier = JMod.PROTECTED;
		} else  {
			modifier = JMod.NONE;
		}
		
		if (fullFieldName.contains("static ")) {
			modifier = modifier | JMod.STATIC;
		} 
		
		if (fullFieldName.contains("final ")) {
			modifier = modifier | JMod.FINAL;
		} 
		return modifier;
	}
}
