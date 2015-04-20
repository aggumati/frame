package io.github.aggumati.frame.model;

import java.util.ArrayList;
import java.util.List;

public class FormGenerator {
	private String label;
	private String name;
	private String type;
	private String value;
	private Integer sizeList;
	private List<KeyValueForm> keyVal;
	private List<FormGenerator> table;

	public FormGenerator() {
		this.keyVal = new ArrayList<KeyValueForm>();
	}

	public FormGenerator(String name, String type) {
		this.name = name;
		this.type = type;
		this.keyVal = new ArrayList<KeyValueForm>();
	}

	public FormGenerator(String label, String name, String type) {
		this.label = label;
		this.name = name;
		this.type = type;
		this.keyVal = new ArrayList<KeyValueForm>();
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

	public List<KeyValueForm> getKeyVal() {
		return keyVal;
	}

	public void setKeyVal(List<KeyValueForm> keyVal) {
		this.keyVal = keyVal;
	}

	public void addKeyVal(String key, String val) {
		KeyValueForm form = new KeyValueForm();
		form.setKey(key);
		form.setVal(val);
		this.keyVal.add(form);
	}

	public void addKeyVal(String label, String key, String val) {
		KeyValueForm form = new KeyValueForm();
		form.setLabel(label);
		form.setKey(key);
		form.setVal(val);
		this.keyVal.add(form);
	}

	public Integer getSizeList() {
		return sizeList;
	}

	public void setSizeList(Integer sizeList) {
		this.sizeList = sizeList;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<FormGenerator> getTable() {
		return table;
	}

	public void setTable(List<FormGenerator> table) {
		this.table = table;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
