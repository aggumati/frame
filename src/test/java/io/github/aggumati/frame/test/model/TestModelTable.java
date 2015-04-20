package io.github.aggumati.frame.test.model;

import io.github.aggumati.frame.formtype.FrameFieldDate;
import io.github.aggumati.frame.formtype.FrameFieldDateTime;
import io.github.aggumati.frame.formtype.FrameFieldText;
import io.github.aggumati.frame.formtype.ItemDropdown;
import io.github.aggumati.frame.list.FrameListColumn;
import io.github.aggumati.frame.list.FrameListColumnCheck;
import io.github.aggumati.frame.list.FrameListColumnDate;
import io.github.aggumati.frame.list.FrameListColumnDatetime;
import io.github.aggumati.frame.list.FrameListColumnDropdown;
import io.github.aggumati.frame.list.FrameListColumnFile;
import io.github.aggumati.frame.list.FrameListColumnText;
import io.github.aggumati.jdummy.config.DummyAddress;
import io.github.aggumati.jdummy.config.DummyCity;
import io.github.aggumati.jdummy.config.DummyFirstName;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

/**
 * this class is used to show to the developers how to use table as part of
 * form. this type of generated code usually is used for master detail form.
 * warning : THIS CLASS IN FOR TESTING PURPOSE ONLY.
 * 
 * @author AnggunG
 *
 */
public class TestModelTable {

	@DummyAddress
	@FrameListColumn
	@FrameListColumnText
	@FrameFieldText
	private String exampleColText;
	
	@DummyFirstName
	@FrameListColumn
	@FrameListColumnCheck(value="testval")
	@FrameFieldText
	private String exampleColCheck;
	
	@DummyCity
	@FrameListColumn
	@FrameFieldText
	@FrameListColumnDropdown(value = {
			@ItemDropdown(key = "First", val = "ftdropdown"),
			@ItemDropdown(key = "Second", val = "sddropdown"),
			@ItemDropdown(key = "Third", val = "tddropdown") })
	private String exampleColDropdown;
	@FrameListColumnDate
	@FrameFieldDate
	private Date exampleColDate;
	@FrameListColumnDatetime
	@FrameFieldDateTime
	private Date exampleColDatetime;
	@JsonIgnore
	@FrameListColumnFile
	private MultipartFile exampleColFile;

	public String getExampleColText() {
		return exampleColText;
	}

	public void setExampleColText(String exampleColText) {
		this.exampleColText = exampleColText;
	}

	public String getExampleColCheck() {
		return exampleColCheck;
	}

	public void setExampleColCheck(String exampleColCheck) {
		this.exampleColCheck = exampleColCheck;
	}

	public String getExampleColDropdown() {
		return exampleColDropdown;
	}

	public void setExampleColDropdown(String exampleColDropdown) {
		this.exampleColDropdown = exampleColDropdown;
	}

	public Date getExampleColDate() {
		return exampleColDate;
	}

	public void setExampleColDate(Date exampleColDate) {
		this.exampleColDate = exampleColDate;
	}

	public Date getExampleColDatetime() {
		return exampleColDatetime;
	}

	public void setExampleColDatetime(Date exampleColDatetime) {
		this.exampleColDatetime = exampleColDatetime;
	}

	public MultipartFile getExampleColFile() {
		return exampleColFile;
	}

	public void setExampleColFile(MultipartFile exampleColFile) {
		this.exampleColFile = exampleColFile;
	}
}
