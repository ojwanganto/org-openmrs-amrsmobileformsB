package org.openmrs.module.amrsmobileforms;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Person;
import org.openmrs.User;

/**
 *  A household facility object
 *  
 * @author  Samuel Mbugua
 */
public class MobileFormHousehold extends BaseOpenmrsObject implements Auditable {
	
	private Integer householdId;
	private String householdIdentifier;
	private String village;
	private String sublocation;
	private String location;
	private String division;
	private String district;
	private String gpsLocation;
	private Integer adults;
	private Integer children;
	private Integer adultsEligible;
	private Integer childrenEligible;
	private Person househead;
	private User creator;
	private Date dateCreated;
	private User changedBy;
	private Collection<Economic> economics;
	private Collection<Survey> surveys;

	/**
	 * @return the householdId
	 */
	public Integer getHouseholdId() {
		return householdId;
	}
	/**
	 * @param householdId the householdId to set
	 */
	public void setHouseholdId(Integer householdId) {
		this.householdId = householdId;
	}
	/**
	 * @return the householdIdentifier
	 */
	public String getHouseholdIdentifier() {
		return householdIdentifier;
	}
	/**
	 * @param householdIdentifier the householdIdentifier to set
	 */
	public void setHouseholdIdentifier(String householdIdentifier) {
		this.householdIdentifier = householdIdentifier;
	}
	/**
	 * @return the village
	 */
	public String getVillage() {
		return village;
	}
	/**
	 * @param village the village to set
	 */
	public void setVillage(String village) {
		this.village = village;
	}
	/**
	 * @return the sublocation
	 */
	public String getSublocation() {
		return sublocation;
	}
	/**
	 * @param sublocation the sublocation to set
	 */
	public void setSublocation(String sublocation) {
		this.sublocation = sublocation;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the division
	 */
	public String getDivision() {
		return division;
	}
	/**
	 * @param division the division to set
	 */
	public void setDivision(String division) {
		this.division = division;
	}
	/**
	 * @return the district
	 */
	public String getDistrict() {
		return district;
	}
	/**
	 * @param district the district to set
	 */
	public void setDistrict(String district) {
		this.district = district;
	}
	
	/**
	 * @return the gpsLocation
	 */
	public String getGpsLocation() {
		return gpsLocation;
	}
	/**
	 * @param gpsLocation the gpsLocation to set
	 */
	public void setGpsLocation(String gpsLocation) {
		this.gpsLocation = gpsLocation;
	}
	/**
	 * @return the adults
	 */
	public Integer getAdults() {
		return adults;
	}
	/**
	 * @param adults the adults to set
	 */
	public void setAdults(Integer adults) {
		this.adults = adults;
	}
	/**
	 * @return the children
	 */
	public Integer getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(Integer children) {
		this.children = children;
	}
	/**
	 * @return the adultsEligible
	 */
	public Integer getAdultsEligible() {
		return adultsEligible;
	}
	/**
	 * @param adultsEligible the adultsEligible to set
	 */
	public void setAdultsEligible(Integer adultsEligible) {
		this.adultsEligible = adultsEligible;
	}
	/**
	 * @return the childrenEligible
	 */
	public Integer getChildrenEligible() {
		return childrenEligible;
	}
	/**
	 * @param childrenEligible the childrenEligible to set
	 */
	public void setChildrenEligible(Integer childrenEligible) {
		this.childrenEligible = childrenEligible;
	}
	/**
	 * @return the househead
	 */
	public Person getHousehead() {
		return househead;
	}
	/**
	 * @param househead the househead to set
	 */
	public void setHousehead(Person househead) {
		this.househead = househead;
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
		return this.changedBy;
	}
	
	public Date getDateChanged() {
		return null;
	}
	
	public void setChangedBy(User changedBy) {
		this.changedBy=changedBy;
	}
	
	public void setDateChanged(Date dateChanged) {
	}

	public Integer getId() {
		return getHouseholdId();
	}
	
	public void setId(Integer id) {
		setHouseholdId(id);
	}
	
	/**
	 * @return the economic
	 */
	public Collection<Economic> getEconomics() {
		return economics;
	}
	/**
	 * @param economic the economic to set
	 */
	public void setEconomics(Collection<Economic> economics) {
		this.economics = economics;
	}
	
	public void addEconomic(Economic economic) {
		if (economics==null)
			this.economics=new HashSet<Economic>();
		economic.setHousehold(this);
		economics.add(economic);
	}
	
	/**
	 * @return the surveys
	 */
	public Collection<Survey> getSurveys() {
		return surveys;
	}
	/**
	 * @param surveys the surveys to set
	 */
	public void setSurveys(Collection<Survey> surveys) {
		this.surveys = surveys;
	}
	
	public void addSurvey(Survey survey) {
		if (survey == null)
			return;
		
		if (surveys == null)
			this.surveys = new HashSet<Survey>();
		
		survey.setHousehold(this);
		surveys.add(survey);
	}
}