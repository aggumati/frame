package io.github.aggumati.frame.test.model;

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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

/**
 * this class is used to show to the developers how to configure data model for generating the other classes.
 * in this class, the developers have to mind :
 * 1. field annotations; the field annotations is used by framework to define type of generated code (form)
 * 2. data type of field; the data type is used by framework to do auto mapping from user interface (jsp) with the data model.
 * 3. getter and setter; the methods is used by framework to do auto mappign from user interface (jsp) with the data model 
 * (together with type of field)
 * 
 * warning : THIS CLASS IN FOR TESTING PURPOSE ONLY.
 * @author AnggunG
 */
@SuppressWarnings("serial")
public class TestModelForm implements Serializable {
	/**
	 * textExample is example of text field generated code (input text in html term).
	 * this type of input need @FrameFieldText annotated. actually, we don't have to consider data type,
	 * there's no error if you use any data type as long as the data type is primitive or primitive wrapper data type. 
	 * But, you are strongly recommended to use String as data type.
	 */
	@FrameFieldText
	private String textExample;
	
	/**
	 * passwordExample is example of password field generated code.
	 * this type of input need @FrameFieldPassword annotated. actually, we don't have to consider data type,
	 * there's no error if you use any data type as long as the data type is primitive or primitive wrapper data type. 
	 * But, you are strongly recommended to use String as data type.
	 */
	@FrameFieldPassword
	private String passwordExample;
	
	/**
	 * emailExample is example of email field generated code.
	 * this type of input need @FrameFieldEmail annotated. actually, we don't have to consider data type,
	 * there's no error if you use any data type as long as the data type is primitive or primitive wrapper data type. 
	 * But, you are strongly recommended to use String as data type.
	 */
	@FrameFieldEmail
	private String emailExample;
	
	/**
	 * dateExample is example of date field generated code.
	 * this type of input need @FrameFieldDate annotated. for use this type of generated code, you have to set 
	 * a class variable with java.util.Date as the type.
	 */
	@FrameFieldDate
	private Date dateExample;
	
	/**
	 * datetimeExample is example of date time field generated code.
	 * this type of input need @FrameFieldDateTime annotated. for use this type of generated code, you have to set 
	 * a class variable with java.util.Date as the type.
	 */
	@FrameFieldDateTime
	private Date datetimeExample;
	
	/**
	 * fileExample is example of file field generated code. this is used to generate file upload field.
	 * this type of input need @FrameFieldFile annotated. for use this type of generated code, you have to set 
	 * a class variable with org.springframework.web.multipart.MultipartFile as the type.
	 */
	@JsonIgnore
	@FrameFieldFile
	private MultipartFile fileExample;
	
	/**
	 * radioExample is example of radio button field generated code. this is used to choose one from some options.
	 * this type of input need @FrameFieldRadio annotated. for use this type of generated code, you have to set 
	 * a class variable with String as the type.
	 * for configuring this type of generated code, we need the other annotation; @ItemRadio. this item contains key and value.
	 * key is used by framework to generate label of radio button
	 * val is used by framework to set the value.
	 */
	@FrameFieldRadio(value={
			@ItemRadio(key="First", val="ftradio"),
			@ItemRadio(key="Second", val="sdradio"),
			@ItemRadio(key="Third", val="tdradio"),
	})
	private String radioExample;
	
	/**
	 * dropdownExample is example of dropdown field generated code. this is used to choose one from some options.
	 * this type of input need @FrameFieldDropdown annotated. for use this type of generated code, you have to set 
	 * a class variable with String as the type.
	 * for configuring this type of generated code, we need the other annotation; @ItemDropdown. this item contains key and value.
	 * key is used by framework to generate label of dropdown menu
	 * val is used by framework to set the value.
	 */
	@FrameFieldDropdown(value={
			@ItemDropdown(key="First", val="ftdropdown"),
			@ItemDropdown(key="Second", val="sddropdown"),
			@ItemDropdown(key="Third", val="tddropdown"),
	})
	private String dropdownExample;
	
	/**
	 * checkboxExample is example of checkbox field generated code. this is used to choose mutliple value from some options.
	 * this type of input need @FrameFieldCheck annotated. for use this type of generated code, you have to set 
	 * a class variable with java.util.List<String> as the type.
	 * for configuring this type of generated code, we need the other annotation; @ItemCheck. this item contains key and value.
	 * key is used by framework to generate label of dropdown menu
	 * val is used by framework to set the value.
	 */
	@FrameFieldCheck(value={
			@ItemCheck(key="First", val="ftcheck"),
			@ItemCheck(key="Second", val="sdcheck"),
			@ItemCheck(key="Third", val="tdcheck"),
	})
	private List<String> checkboxExample;
	
	/**
	 * listExample is example of multiple dropdown selected field generated code. this is used to choose multiple values from some options.
	 * this type of input need @FrameFieldList annotated. for use this type of generated code, you have to set 
	 * a class variable with String as the type.
	 * for configuring this type of generated code, we need the other annotation; @ItemList. this item contains key and value.
	 * key is used by framework to generate label of dropdown menu
	 * val is used by framework to set the value.
	 */
	@FrameFieldList(value={
			@ItemList(key="First", val="ftlist"),
			@ItemList(key="Second", val="sdlist"),
			@ItemList(key="Third", val="tddlist"),
	})
	private List<String> listExample;
	
	
	/**
	 * objExample is example of table inside from (master detail form). 
	 * this type of input need @FrameFieldObject annotated. for use this type of generated code, you have to set 
	 * a class variable with list of extended Object class (any class) data type (set generic on java.util.list). 
	 * the class member of associated class will generated as field on the form as column of the table.
	 */
	@FrameFieldObject
	private TestModelAssociatedObj objExample;
	
	
	/**
	 * tableExample is example of form inside from (inner form/associated form). 
	 * this type of input need @FrameFieldObject annotated. for use this type of generated code, you have to set 
	 * a class variable with extended Object class (any class). the class member of associated class will generated as field on 
	 * the form.
	 */
	@FrameFieldTable
	private List<TestModelTable> tableExample;

	public String getTextExample() {
		return textExample;
	}

	public void setTextExample(String textExample) {
		this.textExample = textExample;
	}

	public String getPasswordExample() {
		return passwordExample;
	}

	public void setPasswordExample(String passwordExample) {
		this.passwordExample = passwordExample;
	}

	public String getEmailExample() {
		return emailExample;
	}

	public void setEmailExample(String emailExample) {
		this.emailExample = emailExample;
	}

	public Date getDateExample() {
		return dateExample;
	}

	public void setDateExample(Date dateExample) {
		this.dateExample = dateExample;
	}

	public Date getDatetimeExample() {
		return datetimeExample;
	}

	public void setDatetimeExample(Date datetimeExample) {
		this.datetimeExample = datetimeExample;
	}

	public MultipartFile getFileExample() {
		return fileExample;
	}

	public void setFileExample(MultipartFile fileExample) {
		this.fileExample = fileExample;
	}

	public String getRadioExample() {
		return radioExample;
	}

	public void setRadioExample(String radioExample) {
		this.radioExample = radioExample;
	}

	public String getDropdownExample() {
		return dropdownExample;
	}

	public void setDropdownExample(String dropdownExample) {
		this.dropdownExample = dropdownExample;
	}

	public List<String> getCheckboxExample() {
		return checkboxExample;
	}

	public void setCheckboxExample(List<String> checkboxExample) {
		this.checkboxExample = checkboxExample;
	}

	public List<String> getListExample() {
		return listExample;
	}

	public void setListExample(List<String> listExample) {
		this.listExample = listExample;
	}

	public TestModelAssociatedObj getObjExample() {
		return objExample;
	}

	public void setObjExample(TestModelAssociatedObj objExample) {
		this.objExample = objExample;
	}

	public List<TestModelTable> getTableExample() {
		return tableExample;
	}

	public void setTableExample(List<TestModelTable> tableExample) {
		this.tableExample = tableExample;
	}
	
}
