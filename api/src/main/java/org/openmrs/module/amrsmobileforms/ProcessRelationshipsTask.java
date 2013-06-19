package org.openmrs.module.amrsmobileforms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * A task to process forms in the mobile-forms queue.
 * 
 * @author Samuel Mbugua
 */
public class ProcessRelationshipsTask  extends AbstractTask {

	private static Log log = LogFactory.getLog(ProcessRelationshipsTask.class);

	// An instance of the mobile-forms processor.
	private MobileFormPostProcessor processor = null;

	/**
	 * Default Convenience constructor 
	 */
	public ProcessRelationshipsTask() {
		if (processor == null)
			processor = new MobileFormPostProcessor();
	}

	/**
	 * Process the each mobile individual form to create relationships
	 */
	public void execute() {
		Context.openSession();
		log.debug("Running mobile forms relationships task... ");
		try {
			if (Context.isAuthenticated() == false)
				authenticate();
			processor.processPostProcessQueue();
		} catch (APIException e) {
			log.error("Error running mobile forms relationships task", e);
			throw e;
		} finally {
			Context.closeSession();
		}
	}

	/*
	 * Resources clean up
	 */
	public void shutdown() {
		processor = null;
		super.shutdown();
		log.debug("Shutting down ProcessRelationshipsTask task ...");
	}
}