/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.amrsmobileforms;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.household.model.Household;
import org.openmrs.module.household.service.HouseholdService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 *
 * @author jkeiper
 */
public class HouseholdModuleConverterTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void testAddHouseholdAndEncounter() throws Exception {
		executeDataSet("org/openmrs/module/amrsmobileforms/test/include/testHouseholdData.xml");
		MobileFormHousehold mfh = Context.getService(MobileFormEntryService.class).getHousehold("12345678");
		Assert.assertNotNull(mfh);
		
//		// convert the MobileFormHousehold to a Household
//		HouseholdModuleConverter.getInstance().addHouseholdAndEncounter(mfh);
//		Household h = Context.getService(HouseholdService.class).getHouseholdGroupByIdentifier("12345678");
//		Assert.assertNotNull(h);
	}

	@Test
	public void testAddMembership() {
		// pass
	}
}
