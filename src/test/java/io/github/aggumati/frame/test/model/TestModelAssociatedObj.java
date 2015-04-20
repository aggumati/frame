package io.github.aggumati.frame.test.model;

import io.github.aggumati.frame.formtype.FrameFieldCheck;
import io.github.aggumati.frame.formtype.FrameFieldDate;
import io.github.aggumati.frame.formtype.FrameFieldDateTime;
import io.github.aggumati.frame.formtype.FrameFieldDropdown;
import io.github.aggumati.frame.formtype.FrameFieldEmail;
import io.github.aggumati.frame.formtype.FrameFieldFile;
import io.github.aggumati.frame.formtype.FrameFieldList;
import io.github.aggumati.frame.formtype.FrameFieldPassword;
import io.github.aggumati.frame.formtype.FrameFieldRadio;
import io.github.aggumati.frame.formtype.FrameFieldText;
import io.github.aggumati.frame.formtype.ItemCheck;
import io.github.aggumati.frame.formtype.ItemDropdown;
import io.github.aggumati.frame.formtype.ItemList;
import io.github.aggumati.frame.formtype.ItemRadio;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

/**
 * this class is used to show to developer how to use object relation code generated. refer to the annotations,
 * the annotation and data type playing role is same with TestModelForm. the different is this class is sub form of the
 * TextModelForm. developer can put all of the type. so far, the framework still cannot support recursive function 
 * (class has sub form the same class) 
 * warning : THIS CLASS IN FOR TESTING PURPOSE ONLY.
 * @author AnggunG
 */
public class TestModelAssociatedObj {
	
	@FrameFieldText
	private String subTextExample;
	@FrameFieldPassword
	private String subPasswordExample;
	@FrameFieldEmail
	private String subEmailExample;
	@FrameFieldDate
	private Date subDateExample;
	@FrameFieldDateTime
	private Date subDatetimeExample;
	@JsonIgnore
	@FrameFieldFile
	private MultipartFile subFileExample;
	@FrameFieldRadio(value={
			@ItemRadio(key="First", val="ftradio"),
			@ItemRadio(key="Second", val="sdradio"),
			@ItemRadio(key="Third", val="tdradio"),
	})
	private String subRadioExample;
	@FrameFieldDropdown(value={
			@ItemDropdown(key="First", val="ftdropdown"),
			@ItemDropdown(key="Second", val="sddropdown"),
			@ItemDropdown(key="Third", val="tddropdown"),
	})
	private String subDropdownExample;
	@FrameFieldCheck(value={
			@ItemCheck(key="First", val="ftcheck"),
			@ItemCheck(key="Second", val="sdcheck"),
			@ItemCheck(key="Third", val="tdcheck"),
	})
	private List<String> subCheckboxExample;
	@FrameFieldList(value={
			@ItemList(key="First", val="ftlist"),
			@ItemList(key="Second", val="sdlist"),
			@ItemList(key="Third", val="tddlist"),
	})
	private List<String> subListExample;
	public String getSubTextExample() {
		return subTextExample;
	}
	public void setSubTextExample(String subTextExample) {
		this.subTextExample = subTextExample;
	}
	public String getSubPasswordExample() {
		return subPasswordExample;
	}
	public void setSubPasswordExample(String subPasswordExample) {
		this.subPasswordExample = subPasswordExample;
	}
	public String getSubEmailExample() {
		return subEmailExample;
	}
	public void setSubEmailExample(String subEmailExample) {
		this.subEmailExample = subEmailExample;
	}
	public Date getSubDateExample() {
		return subDateExample;
	}
	public void setSubDateExample(Date subDateExample) {
		this.subDateExample = subDateExample;
	}
	public Date getSubDatetimeExample() {
		return subDatetimeExample;
	}
	public void setSubDatetimeExample(Date subDatetimeExample) {
		this.subDatetimeExample = subDatetimeExample;
	}
	public MultipartFile getSubFileExample() {
		return subFileExample;
	}
	public void setSubFileExample(MultipartFile subFileExample) {
		this.subFileExample = subFileExample;
	}
	public String getSubRadioExample() {
		return subRadioExample;
	}
	public void setSubRadioExample(String subRadioExample) {
		this.subRadioExample = subRadioExample;
	}
	public String getSubDropdownExample() {
		return subDropdownExample;
	}
	public void setSubDropdownExample(String subDropdownExample) {
		this.subDropdownExample = subDropdownExample;
	}
	public List<String> getSubCheckboxExample() {
		return subCheckboxExample;
	}
	public void setSubCheckboxExample(List<String> subCheckboxExample) {
		this.subCheckboxExample = subCheckboxExample;
	}
	public List<String> getSubListExample() {
		return subListExample;
	}
	public void setSubListExample(List<String> subListExample) {
		this.subListExample = subListExample;
	}
}
