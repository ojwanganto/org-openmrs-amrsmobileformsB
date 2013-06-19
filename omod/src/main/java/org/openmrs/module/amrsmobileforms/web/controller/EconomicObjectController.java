package org.openmrs.module.amrsmobileforms.web.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.EconomicObject;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for economic objects jsp page
 * 
 * @author Samuel Mbugua
 */
@Controller
public class EconomicObjectController {

	@ModelAttribute("economicObject")
	@RequestMapping(value="/module/amrsmobileforms/economicObject", method=RequestMethod.GET)
	public List<EconomicObject> populateForm() {
		MobileFormEntryService mfs = (MobileFormEntryService)Context.getService(MobileFormEntryService.class);
		return mfs.getAllEconomicObjects();
	}
	
	@RequestMapping(value="/module/amrsmobileforms/economicObject", method=RequestMethod.POST, params = "action=Create New Object")
	public String saveObject(HttpSession httpSession, @RequestParam String objectName, @RequestParam String objectType){
		
		MobileFormEntryService mfs = (MobileFormEntryService)Context.getService(MobileFormEntryService.class);
		EconomicObject econObj= mfs.getEconomicObjectByObjectName(objectName);
		if (econObj == null) {
			econObj=new EconomicObject();
			econObj.setObjectName(objectName);
			econObj.setObjectType(objectType);
			mfs.saveEconomicObject(econObj);
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "New Economic Object saved successfully" );
		}else
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "An Object already exists with similar name" );
		
		return "redirect:/module/amrsmobileforms/economicObject.form";
	}

	@RequestMapping(value="/module/amrsmobileforms/economicObject", method=RequestMethod.POST,params="action=Delete Selected Object(s)")
	public String deleteObject(HttpSession httpSession,
								@RequestParam("objectId") List<Integer> selectedIds){
		MobileFormEntryService mfs = (MobileFormEntryService)Context.getService(MobileFormEntryService.class);
		String deletedIds="";
		for (Integer id : selectedIds) {
			if (mfs.deleteEconomicObject(mfs.getEconomicObjectById(id)))
				deletedIds += " " + id;		
		}
		if (deletedIds !="")
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Successfully deleted objects " + deletedIds);
		else
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Error deleting selected objects");
		
		return "redirect:/module/amrsmobileforms/economicObject.form";
	}
}	