package org.openmrs.module.amrsmobileforms;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import org.springframework.transaction.annotation.Transactional;

/**
 * Required methods for any mobile form entry service
 * 
 * @author  Samuel Mbugua
 */
@Transactional
public interface MobileFormEntryService {
	
	/**
	 * A getter for all resource files uploaded into the module
	 * @return List of resource files
	 */
	public List<File> getMobileResources();
	
	/**
	 * Get a mobile queue given the absolute path to an xml file
	 * @param absoluteFilePath
	 * @return {@link MobileFormQueue}
	 */
	public MobileFormQueue getMobileFormEntryQueue(String absoluteFilePath);

	/**
	 * Get all system variables for this module
	 */
	public SortedMap<String, String> getSystemVariables();
	
	//HOUSEHOLD RELATED METHODS 
	/**
	 * Get a household object given a string household identifier
	 * @param householdIdentifier
	 * @return {@link MobileFormHousehold}
	 */
	public MobileFormHousehold getHousehold(String householdIdentifier);
	
	/**
	 * Create or update a household in the database
	 * @param household
	 */
	public void saveHousehold(MobileFormHousehold household);
	
	/**
	 * Get a single household member object given a member identifier
	 * @param identifier
	 * @return {@link HouseholdMember}
	 */
	public HouseholdMember getHouseholdMemberById(Integer identifier);
	
	/** Create a new HouseholdMember object in the database. If <b> {@link HouseholdMember} </b> exists
	 * it updates the existing object.
	 * @param householdMember
	 */
	public void saveHouseholdMember(HouseholdMember householdMember);
	
	/**
	 * Get all persons in a household
	 * @param householdId
	 */
	public List<HouseholdMember> getAllMembersInHousehold(MobileFormHousehold Household);
	
	//ECONOMIC RELATED METHODS
	/**
	 * Create a new economic item in the database
	 * @param economic
	 */
	public void createEconomicInDatabase(Economic economic);
	
	/**
	 * Get all economic objects that are in the database
	 * @return list of {@link EconomicObject}
	 */
	public List<EconomicObject> getAllEconomicObjects();
	
	/**
	 * Get a specific object given its name
	 * @param objectName
	 * @return {@link EconomicObject}
	 */
	public EconomicObject getEconomicObjectByObjectName(String objectName);
	
	/**
	 * Get a specific object given its id
	 * @param economicObjectId
	 * @return {@link EconomicObject}
	 */
	public EconomicObject getEconomicObjectById(Integer economicObjectId);
	
	/**
	 * Create a new economic object in the database
	 * @param economicObject
	 */
	public void saveEconomicObject(EconomicObject economicObject);
	
	/**
	 * Delete a specified economic object from the database
	 * @param economicObject
	 * @return <b>true</b> if delete was successful otherwise <b>false</b>
	 */
	public boolean deleteEconomicObject(EconomicObject economicObject);
	
	//SURVEY RELATED METHODS
	/**
	 * Create a new survey record in the database
	 * @param survey
	 */
	public void createSurvey(Survey survey);
	
	//ERRORS RELATED METHODS
	/**
	 * Get all errors logged in the error table
	 * @return list of {@link MobileFormEntryError}
	 */
	public List<MobileFormEntryError> getAllMobileFormEntryErrors();
	
	/**
	 * Get a specific  error by a specified id
	 * @param errorId
	 * @return {@link MobileFormEntryError}
	 */
	public MobileFormEntryError getErrorById(Integer errorId);
	
	/**
	 * Create a new error record in the error table
	 * @param mobileFormEntryError
	 */
	public void saveErrorInDatabase(MobileFormEntryError mobileFormEntryError);
	
	/** Delete a specified error from the database
	 * @param error
	 */
	public void deleteError(MobileFormEntryError error);

	/**
	 * Generate sync logs for a specific date. If no logs return null
	 * @param logDate
	 * @return List of sync logs
	 */
	public List<SyncLogModel> getSyncLog(Date logDate);
	
	/**
	 * Get all date encoded sync log files. If none return null
	 * @return List of sync log files
	 */
	public List<String> getAllSyncLogs();

	public EconomicConceptMap getEconomicConceptMapFor(EconomicObject eo);

	public EconomicConceptMap getEconomicConceptMap(Integer id);

	public EconomicConceptMap saveEconomicConceptMap(EconomicConceptMap ecm);

	public List<MobileFormEntryError> getErrorBatch(Integer start, Integer length, String query);

	public Number countErrors(String query);
}