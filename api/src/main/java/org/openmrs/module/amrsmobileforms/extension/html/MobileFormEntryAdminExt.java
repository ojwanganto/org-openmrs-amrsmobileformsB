package org.openmrs.module.amrsmobileforms.extension.html;

import java.util.Map;
import java.util.TreeMap;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;
import org.openmrs.util.InsertedOrderComparator;

/**
 * Anchor for the module in the Main OpenMRS administration page
 * 
 * @author Samuel Mbugua
 *
 */
@SuppressWarnings("deprecation")
public class MobileFormEntryAdminExt extends AdministrationSectionExt {
	
	private static String requiredPrivileges = null;
	

	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	

	public String getTitle() {
		return "amrsmobileforms.title";
	}
	
	public String getRequiredPrivilege() {
		if (requiredPrivileges == null) {
			StringBuilder builder = new StringBuilder();
			requiredPrivileges = builder.toString();
		}
		
		return requiredPrivileges;
	}
	
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new TreeMap<String, String>(new InsertedOrderComparator());
		map.put("module/amrsmobileforms/propertiesPage.form", "amrsmobileforms.properties");
		map.put("module/amrsmobileforms/mobileResources.list", "amrsmobileforms.mobileResources");
		map.put("module/amrsmobileforms/economicObject.form", "amrsmobileforms.economicObjects");
		map.put("module/amrsmobileforms/householdMappings.list", "amrsmobileforms.householdMappings");
		map.put("module/amrsmobileforms/resolveErrors.list", "amrsmobileforms.resolveErrors.title");
		map.put("module/amrsmobileforms/syncLog.list", "amrsmobileforms.sync.title");
		return map;
	}
	
}
