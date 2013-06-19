package org.openmrs.module.amrsmobileforms.web.controller;

import java.util.HashMap;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.MobileFormEntryConstants;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controllerf for properties page jsp
 * 
 * @author Samuel Mbugua
 */
@Controller
public class PropertiesPageController {
	
	@RequestMapping(value="/module/amrsmobileforms/propertiesPage", method=RequestMethod.GET)
	public Map<String, Object> populateForm() {
		MobileFormEntryService mfs = Context.getService(MobileFormEntryService.class);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("systemVars", mfs.getSystemVariables());
		
		map.put("identifierTypes", Context.getPatientService().getAllPatientIdentifierTypes());
		map.put("attributeTypes", Context.getPersonService().getAllPersonAttributeTypes());
		
		map.put("hctIdentifierType", Context.getAdministrationService()
				.getGlobalProperty(MobileFormEntryConstants.GP_HCT_IDENTIFIER_TYPE));
		map.put("phonenumberAttributeType", Context.getAdministrationService()
				.getGlobalProperty(MobileFormEntryConstants.GP_PHONENUMBER_ATTRIBUTE_TYPE));
		
		return map;
	}
}
