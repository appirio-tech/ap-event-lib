package com.appirio.event;

import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {

	private String id;
	private String createdBy;
	private DateTime createdAt;

	private String sourceObjectType;
	private String sourceObjectId;
	private String eventSubType;
	private String eventType;
	private String sourceObjectContent;
	private List<FieldChange> fieldChanges;

	public Event() {
		// for deserialization
	}

	/**
	 * Event constructor
	 * 
	 * @param createdBy
	 *            The id of the user that generated the event
	 */
	public Event(String userId, String createdBy) {
		setCreatedBy(createdBy);
	}

	@JsonProperty
	public String getCreatedBy() {
		return createdBy;
	}

	@JsonProperty
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@JsonProperty
	public DateTime getCreatedAt() {
		return createdAt;
	}

	@JsonProperty
	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	@JsonProperty
	public String getId() {
		return id;
	}

	@JsonProperty
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty
	public List<FieldChange> getFieldChanges() {
		return fieldChanges;
	}

	@JsonProperty
	public void setFieldChanges(List<FieldChange> fieldChanges) {
		this.fieldChanges = fieldChanges;
	}

	@JsonProperty
	public String getSourceObjectType() {
		return sourceObjectType;
	}

	public void setSourceObjectType(String sourceObjectType) {
		this.sourceObjectType = sourceObjectType;
	}

	@JsonProperty
	public String getSourceObjectId() {
		return sourceObjectId;
	}

	public void setSourceObjectId(String sourceObjectId) {
		this.sourceObjectId = sourceObjectId;
	}

	@JsonProperty
	public String getEventSubType() {
		return eventSubType;
	}

	public void setEventSubType(String eventPublishingType) {
		this.eventSubType = eventPublishingType;
	}

	@JsonProperty
	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	@JsonProperty
	public String getSourceObjectContent() {
		return sourceObjectContent;
	}

	public void setSourceObjectContent(String objectContent) {
		this.sourceObjectContent = objectContent;
	}

}
