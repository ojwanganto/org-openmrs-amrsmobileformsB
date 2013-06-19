package org.openmrs.module.amrsmobileforms;

import java.sql.Timestamp;
import java.util.Date;

import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;

/**
 * @author Samuel Mbugua
 *
 */
public class Survey extends BaseOpenmrsObject implements Auditable {
	private Integer householdSurveyId;
	private MobileFormHousehold household;
	private String surveyIdentifier;
	private String providerId;
	private String teamId;
	private String allowedIn;
	private String deviceId;
	private String subscriberId;
	private Date returnDate;
	private Timestamp startTime;
	private Timestamp endTime;
	private User creator;
	private Date dateCreated;
	
	/**
	 * @return the surveyId
	 */
	public Integer getHouseholdSurveyId() {
		return householdSurveyId;
	}
	/**
	 * @param surveyId the surveyId to set
	 */
	public void setHouseholdSurveyId(Integer surveyId) {
		this.householdSurveyId = surveyId;
	}
	/**
	 * @return the household
	 */
	public MobileFormHousehold getHousehold() {
		return household;
	}
	/**
	 * @param household the household to set
	 */
	public void setHousehold(MobileFormHousehold household) {
		this.household = household;
	}
	/**
	 * @return the surveyIdentifier
	 */
	public String getSurveyIdentifier() {
		return surveyIdentifier;
	}
	/**
	 * @param surveyIdentifier the surveyIdentifier to set
	 */
	public void setSurveyIdentifier(String surveyIdentifier) {
		this.surveyIdentifier = surveyIdentifier;
	}
	/**
	 * @return the providerId
	 */
	public String getProviderId() {
		return providerId;
	}
	/**
	 * @param providerId the providerId to set
	 */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	/**
	 * @return the teamId
	 */
	public String getTeamId() {
		return teamId;
	}
	/**
	 * @param teamId the teamId to set
	 */
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	/**
	 * @return the allowedIn
	 */
	public String getAllowedIn() {
		return allowedIn;
	}
	/**
	 * @param allowedIn the allowedIn to set
	 */
	public void setAllowedIn(String allowedIn) {
		this.allowedIn = allowedIn;
	}
	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return the subscriberId
	 */
	public String getSubscriberId() {
		return subscriberId;
	}
	/**
	 * @param subscriberId the subscriberId to set
	 */
	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}
	/**
	 * @return the returnDate
	 */
	public Date getReturnDate() {
		return returnDate;
	}
	/**
	 * @param returnDate the returnDate to set
	 */
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	/**
	 * @return the startTime
	 */
	public Timestamp getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the endTime
	 */
	public Timestamp getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
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
	public User getChangedBy() {
		// TODO Auto-generated method stub
		return null;
	}
	public Date getDateChanged() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setChangedBy(User changedBy) {
		// TODO Auto-generated method stub
		
	}
	public void setDateChanged(Date dateChanged) {
		// TODO Auto-generated method stub
		
	}
	public Integer getId() {
		return getHouseholdSurveyId();
	}
	public void setId(Integer id) {
		this.setHouseholdSurveyId(id);
	}
}