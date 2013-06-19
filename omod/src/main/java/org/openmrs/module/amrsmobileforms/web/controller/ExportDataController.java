/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.amrsmobileforms.web.controller;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.MobileFormHousehold;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author jkeiper
 */
@Controller
public class ExportDataController {
	
	@RequestMapping(value="/module/amrsmobileforms/household.json")
	public String getJSONHousehold(@RequestParam("householdIdentifier") String householdIdentifier) throws IOException {
		MobileFormHousehold household = getService().getHousehold(householdIdentifier);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(household);
	}
	
	private MobileFormEntryService getService() {
		return Context.getService(MobileFormEntryService.class);
	}
	
	private <T> T initializeAndUnproxy(T entity) {
    if (entity == null) {
        throw new 
           NullPointerException("Entity passed for initialization is null");
    }

    Hibernate.initialize(entity);
    if (entity instanceof HibernateProxy) {
        entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
                .getImplementation();
    }
    return entity;
}
}
