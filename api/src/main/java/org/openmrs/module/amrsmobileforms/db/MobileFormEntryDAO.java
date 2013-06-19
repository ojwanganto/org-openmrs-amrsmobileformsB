package org.openmrs.module.amrsmobileforms.db;

import java.util.List;
import org.openmrs.module.amrsmobileforms.Economic;
import org.openmrs.module.amrsmobileforms.EconomicConceptMap;
import org.openmrs.module.amrsmobileforms.EconomicObject;
import org.openmrs.module.amrsmobileforms.HouseholdMember;
import org.openmrs.module.amrsmobileforms.MobileFormEntryError;
import org.openmrs.module.amrsmobileforms.MobileFormHousehold;
import org.openmrs.module.amrsmobileforms.Survey;


/**
 * Public Interface to the HibernateMobileFormEntryDAO
 * 
 * @author Samuel Mbugua
 */
public interface MobileFormEntryDAO {
	
	public MobileFormHousehold getHousehold(String householdIdentifier);
	
	public void saveHousehold(MobileFormHousehold household);

	public EconomicObject getEconomicObjectByObjectName(String objectName);

	public void createEconomicInDatabase(Economic economic);

	public void createSurvey(Survey survey);

	public List<EconomicObject> getAllEconomicObjects();

	public void saveEconomicObject(EconomicObject economicObject);

	public boolean deleteEconomicObject(EconomicObject economicObject);

	public void saveErrorInDatabase(MobileFormEntryError mobileFormEntryError);

	public List<MobileFormEntryError> getAllMobileFormEntryErrors();

	public MobileFormEntryError getErrorById(Integer errorId);

	public HouseholdMember getHouseholdMemberById(Integer identifier);

	public EconomicObject getEconomicObjectById(Integer economicObjectId);

	public void deleteError(MobileFormEntryError error);

	public void saveHouseholdMember(HouseholdMember householdMember);

	public List<HouseholdMember> getAllMembersInHousehold(MobileFormHousehold Household);

	public EconomicConceptMap getEconomicConceptMapFor(EconomicObject eo);

	public EconomicConceptMap getEconomicConceptMap(Integer id);

	public EconomicConceptMap saveEconomicConceptMap(EconomicConceptMap ecm);

	public List<MobileFormEntryError> getErrorBatch(Integer start, Integer length, String query);

	public Number countErrors(String query);
}