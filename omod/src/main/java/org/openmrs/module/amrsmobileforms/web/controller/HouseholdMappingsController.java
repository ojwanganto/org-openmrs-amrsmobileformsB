/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.amrsmobileforms.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.EconomicConceptMap;
import org.openmrs.module.amrsmobileforms.EconomicObject;
import org.openmrs.module.amrsmobileforms.MobileFormEntryConstants;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.openmrs.module.household.service.HouseholdService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author jkeiper
 */
@Controller
public class HouseholdMappingsController {
	
	private static final String LIST_VIEW = "module/amrsmobileforms/householdMappings";
	private final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value="/module/amrsmobileforms/householdMappings.list")
	public String list(ModelMap modelMap) {
		MobileFormEntryService service = Context.getService(MobileFormEntryService.class);
		List<EconomicObject> economicObjects = service.getAllEconomicObjects();
		
		List<EconomicConceptMap> economicMaps = new ArrayList<EconomicConceptMap>();
		for (EconomicObject eo: economicObjects) {
			EconomicConceptMap ecm = service.getEconomicConceptMapFor(eo);
			if (ecm == null)
				ecm = new EconomicConceptMap(eo);
			economicMaps.add(ecm);
		}
		modelMap.put("economicMaps", economicMaps);
		
		HouseholdService hservice = Context.getService(HouseholdService.class);
		
		Map<String, String> gpMaps = new HashMap<String, String>();
		for (String gp: new String[]{
				MobileFormEntryConstants.GP_CONCEPT_ADULTS,
				MobileFormEntryConstants.GP_CONCEPT_CHILDREN,
				MobileFormEntryConstants.GP_CONCEPT_ELIGIBLE_ADULTS,
				MobileFormEntryConstants.GP_CONCEPT_ELIGIBLE_CHILDREN,
				MobileFormEntryConstants.GP_CONCEPT_RETURN_VISIT_DATE })
			gpMaps.put(gp, Context.getAdministrationService().getGlobalProperty(gp));
		modelMap.put("gpMaps", gpMaps);
		
		modelMap.put("defaultHouseholdDefinition", 
				Context.getAdministrationService().getGlobalProperty(
					MobileFormEntryConstants.GP_DEFAULT_HOUSEHOLD_DEFINITION));

		modelMap.put("householdDefinitions", hservice.getAllHouseholdDefinitions());
		
		modelMap.put("defaultHouseholdEncounterType", 
				Context.getAdministrationService().getGlobalProperty(
					MobileFormEntryConstants.GP_DEFAULT_HOUSEHOLD_ENCOUNTER_TYPE));

		modelMap.put("householdEncounterTypes", hservice.getAllHouseholdEncounterTypes());
		
		return LIST_VIEW;
	}
	
}
