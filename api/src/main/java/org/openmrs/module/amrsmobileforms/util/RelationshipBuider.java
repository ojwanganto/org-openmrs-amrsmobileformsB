package org.openmrs.module.amrsmobileforms.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.MobileFormHousehold;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;

/**
 * @author Samuel Mbugua
 *
 */
public class RelationshipBuider {
	
	private static Log log = LogFactory.getLog(RelationshipBuider.class);
	private static PersonService ps;
	private static MobileFormEntryService mfes;
	
	public static boolean createRelationship(Person person, String relationshipTohead, String householdIdentifier) {
		Person househead = null;
		ps= Context.getPersonService();
		mfes=(MobileFormEntryService) Context.getService(MobileFormEntryService.class);
		try {
			//SELF RELATIONSHIP - ACTUALLY THIS IS HOUSEHEAD
			if (relationshipTohead.equals(RelationshipCodes.SELF)){
				createHouseHead(householdIdentifier, person);
				return true;
			}else {
				househead=mfes.getHousehold(householdIdentifier).getHousehead();
			}
			
			//IF HOUSEHEAD IS NULL, RETURN HERE
			if (househead == null) {
				log.debug("Household (" + householdIdentifier + ") has no head yet .... NOT CREATING RELATIONSHIP");
				return false;
			}
			
			//SPOUSE RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.SPOUSE)) {
				saveRelashionship(person, househead, RelationshipCodes.SPOUSE_VALUE);
				return true;		
			}
			//CHILD RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.CHILD)) {
				saveRelashionship(househead, person, RelationshipCodes.PARENT_CHILD_VALUE);
				return true;
			}
			//PARENT RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.PARENT)) {
				saveRelashionship(person, househead, RelationshipCodes.PARENT_CHILD_VALUE);
				return true;
			}
			//SIBLING RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.SIBLING)) {
				saveRelashionship(person, househead, RelationshipCodes.SIBLING_VALUE);
				return true;
			}
			//GRAND PARENT RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.GRAND_PARENT)) {
				saveRelashionship(househead, person, RelationshipCodes.GRAND_PARENT_CHILD_VALUE);
				return true;
			}
			//GRAND CHILD RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.GRAND_CHILD)) {
				saveRelashionship(person, househead, RelationshipCodes.GRAND_PARENT_CHILD_VALUE);
				return true;
			}
			//PARENT IN LAW RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.PARENT_IN_LAW)) {
				saveRelashionship(househead, person, RelationshipCodes.PARENT_IN_LAW_VALUE);
				return true;
			}
			//STEP PARENT RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.STEP_PARENT)) {
				saveRelashionship(househead, person, RelationshipCodes.STEP_PARENT_CHILD_VALUE);
				return true;
			}
			//STEP CHILD RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.STEP_CHILD)) {
				saveRelashionship(person, househead, RelationshipCodes.STEP_PARENT_CHILD_VALUE);
				return true;
			}
			//AUNT-UNCLE RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.AUNT_UNCLE)) {
				saveRelashionship(person, househead, RelationshipCodes.AUNT_UNCLE_NIECE_NEPHEW_VALUE);
				return true;
			}
			//NIECE-NEPHEW RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.NIECE_NEPHEW)) {
				saveRelashionship(househead, person, RelationshipCodes.AUNT_UNCLE_NIECE_NEPHEW_VALUE);
				return true;
			}
			//COUSIN RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.COUSIN)) {
				saveRelashionship(person, househead, RelationshipCodes.COUSIN_VALUE);
				return true;
			}
			//FOSTER CHILD RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.FOSTER_CHILD)) {
				saveRelashionship(person, househead, RelationshipCodes.FOSTER_PARENT_CHILD_VALUE);
				return true;
			}
			//FRIEND RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.FRIEND)) {
				saveRelashionship(person, househead, RelationshipCodes.FRIEND_VALUE);
				return true;
			}
			//HOUSE HELP OR EMPLOYEE RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.HOUSE_HELP) || relationshipTohead.equals(RelationshipCodes.EMPLOYEE)) {
				saveRelashionship(person, househead, RelationshipCodes.EMPLOYEE_VALUE);
				return true;
			}
			//TENANT RELATIONSHIP 
			if (relationshipTohead.equals(RelationshipCodes.TENANT)) {
				saveRelashionship(person, househead, RelationshipCodes.TENANT_VALUE);
				return true;
			}
		}
		catch (Throwable t) {
			log.error("Error creating relationship", t);
		}
		return false;
	}
	
	private static void createHouseHead(String householdIdentifier, Person person) {
		MobileFormHousehold household = mfes.getHousehold(householdIdentifier);
		if (household != null && household.getHousehead() == null){
			household.setHousehead(person);
			mfes.saveHousehold(household);
		}
	}
	
	private static void saveRelashionship(Person personA, Person personB, Integer relationshipCode){
		Relationship rlp = new Relationship();
		RelationshipType rlpType=ps.getRelationshipType(relationshipCode);
		
		if (isNewRelationship(personA, personB, rlpType)){
			rlp.setPersonA(personA);
			rlp.setPersonB(personB);
			rlp.setRelationshipType(rlpType);
			ps.saveRelationship(rlp);
			log.info("Created a relationship of type: " + relationshipCode);
		}else
			log.info("Similar relationship exists between these people");
	}
	
	private static boolean isNewRelationship(Person personA, Person personB, RelationshipType relationshipType) {
		List<Relationship> relationships = ps.getRelationships(personA, personB, relationshipType);
		if (relationships == null || relationships.size() < 1)
			return true;
		else
			return false;
	}
	
}