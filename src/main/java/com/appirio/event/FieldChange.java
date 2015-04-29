package com.appirio.event;

/**
 * Contains data about the change in value for a field.
 * 
 * @author james
 */
public class FieldChange {
	private String field;
	private String beforeValue;
	private String afterValue;

	public FieldChange() {
		// for deserialization
	}

	public FieldChange(String field, String beforeValue, String afterValue) {
		this.field = field;
		this.beforeValue = beforeValue;
		this.afterValue = afterValue;
	}

	public String getField() {
		return field;
	}

	public void setField(String name) {
		this.field = name;
	}

	public String getBeforeValue() {
		return beforeValue;
	}

	public void setBeforeValue(String description) {
		this.beforeValue = description;
	}

	public String getAfterValue() {
		return afterValue;
	}

	public void setAfterValue(String afterValue) {
		this.afterValue = afterValue;
	}

}
