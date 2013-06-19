package org.openmrs.module.amrsmobileforms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Activator;

/**
 * Activator startup/shutdown methods for the mobile form entry module
 *
 * @author  Samuel Mbugua
 */
public class MobileFormEntryActivator implements Activator {

	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.info("Starting the Mobile Form Entry module");
	}
	
	/**
	 *  @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down the Mobile Form Entry module");
	}
	
}
