package org.openmrs.module.amrsmobileforms;

import java.util.Date;

import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;



/**
 * @author Samuel Mbugua
 */
public class Economic extends BaseOpenmrsObject implements Auditable{
	private Integer economicId;
	private MobileFormHousehold household;
	private EconomicObject valueQuestion;
	private Double valueNumeric;
	private String valueText;
	private User creator;
	private Date dateCreated;
	private User changedBy;
	/**
	 * @return the economicId
	 */
	public Integer getEconomicId() {
		return economicId;
	}
	/**
	 * @param economicId the economicId to set
	 */
	public void setEconomicId(Integer economicId) {
		this.economicId = economicId;
	}
	/**
	 * @return the householdId
	 */
	public MobileFormHousehold getHousehold() {
		return household;
	}
	/**
	 * @param householdId the householdId to set
	 */
	public void setHousehold(MobileFormHousehold household) {
		this.household = household;
	}
	/**
	 * @return the valueQuestion
	 */
	public EconomicObject getValueQuestion() {
		return valueQuestion;
	}
	/**
	 * @param valueQuestion the valueQuestion to set
	 */
	public void setValueQuestion(EconomicObject valueQuestion) {
		this.valueQuestion = valueQuestion;
	}
	/**
	 * @return the valueNumeric
	 */
	public Double getValueNumeric() {
		return valueNumeric;
	}
	/**
	 * @param valueNumeric the valueNumeric to set
	 */
	public void setValueNumeric(Double valueNumeric) {
		this.valueNumeric = valueNumeric;
	}
	/**
	 * @return the valueText
	 */
	public String getValueText() {
		return valueText;
	}
	/**
	 * @param valueText the valueText to set
	 */
	public void setValueText(String valueText) {
		this.valueText = valueText;
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
		
		return getEconomicId();
	}
	public void setId(Integer id) {
		this.setEconomicId(id);
		
	}

	
}
