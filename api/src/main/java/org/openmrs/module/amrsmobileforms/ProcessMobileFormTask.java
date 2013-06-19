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
public class ProcessMobileFormTask  extends AbstractTask {

	private static Log log = LogFactory.getLog(ProcessMobileFormTask.class);

	// An instance of the mobile-forms processor.
	private MobileFormProcessor processor = null;

	/**
	 * Default Convenience constructor 
	 */
	public ProcessMobileFormTask() {
		if (processor == null)
			processor = new MobileFormProcessor();
	}

	/**
	 * Process the each mobile xform in the queue and remove it from this queue
	 */
	public void execute() {
		Context.openSession();
		log.debug("Running mobile form task... ");
		try {
			if (Context.isAuthenticated() == false)
				authenticate();
			processor.processMobileForms();
		} catch (APIException e) {
			log.error("Error running mobile forms task", e);
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
		log.debug("Shutting down ProcessMobileForms task ...");
	}
}