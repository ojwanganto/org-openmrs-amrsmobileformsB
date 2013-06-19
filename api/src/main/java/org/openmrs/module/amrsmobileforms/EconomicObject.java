package org.openmrs.module.amrsmobileforms;

import java.util.Date;

import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;

/**
 * Economic Java Object 
 * @author Samuel Mbugua
 */
public class EconomicObject extends BaseOpenmrsObject implements Auditable{
	private Integer objectId;
	private String objectName;
	private String objectType;
	private User creator;
	private Date dateCreated;
	private User changedBy;

	
	/**
	 * @return the objectId
	 */
	public Integer getObjectId() {
		return objectId;
	}
	/**
	 * @param objectId the objectId to set
	 */
	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}
	/**
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}
	/**
	 * @param objectName the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}
	/**
	 * @param objectType the objectType to set
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	/**
	 * @return the creator
	 */
	public User getCreator() {
		return creator;
	}
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(User creator) {
		this.creator = creator;
	}
	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}
	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	/**
	 * @return the changedBy
	 */
	public User getChangedBy() {
		return changedBy;
	}
	/**
	 * @param changedBy the changedBy to set
	 */
	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}
	public Date getDateChanged() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setDateChanged(Date dateChanged) {
		// TODO Auto-generated method stub
		
	}
	public Integer getId() {
		
		return getObjectId();
	}
	public void setId(Integer id) {
		this.setObjectId(id);
		
	}

	
}
