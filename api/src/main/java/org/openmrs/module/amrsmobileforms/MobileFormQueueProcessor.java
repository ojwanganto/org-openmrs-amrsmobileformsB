package org.openmrs.module.amrsmobileforms;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.module.amrsmobileforms.util.SyncLogger;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.openmrs.Location;
import org.xml.sax.SAXParseException;

/**
 * Processes Mobile forms Queue entries.
 *
 * When the processing is successful, For unsuccessful processing, the queue
 * entry is put in the Mobile forms error folder.
 *
 * @author Samuel Mbugua
 *
 */
@Transactional
public class MobileFormQueueProcessor {

	private static final Log log = LogFactory.getLog(MobileFormQueueProcessor.class);
	private XPathFactory xPathFactory;
	private static Boolean isRunning = false; // allow only one running
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private SyncLogger syncLogger;

	public MobileFormQueueProcessor() {
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (Exception e) {
			log.error("Problem occurred while creating document builder", e);
		}
	}

	/**
	 * Process all existing queue entries in the mobile form queue
	 *
	 * @param queue
	 */
	private void processMobileForm(MobileFormQueue queue) throws APIException {
		log.debug("Transforming mobile form entry queue");
		String formData = queue.getFormData();
		String householdIdentifier = null;
		String householdGps = null;
		MobileFormEntryService mfes = (MobileFormEntryService) Context.getService(MobileFormEntryService.class);
        String providerId=null;
        String locationId=null;

		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			XPathFactory xpf = getXPathFactory();
			XPath xp = xpf.newXPath();
			Document doc = docBuilder.parse(IOUtils.toInputStream(formData));
			Node curNode = (Node) xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_PREFIX + MobileFormEntryConstants.HOUSEHOLD_META_PREFIX, doc, XPathConstants.NODE);
			householdIdentifier = xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_HOUSEHOLD_ID, curNode);
			householdGps = xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_GPS_LOCATION, curNode);
            String householdLocation = xp.evaluate(MobileFormEntryConstants.PATIENT_CATCHMENT_AREA, curNode);

            Node surveyNode = (Node) xp.evaluate(MobileFormEntryConstants.SURVEY_PREFIX, doc, XPathConstants.NODE);
            locationId=MobileFormEntryUtil.cleanLocationEntry(householdLocation) ;

           // providerId = Integer.toString(MobileFormEntryUtil.getProviderId(xp.evaluate(MobileFormEntryConstants.SURVEY_PROVIDER_ID, surveyNode)));

            providerId = xp.evaluate(MobileFormEntryConstants.SURVEY_PROVIDER_ID, surveyNode);
            providerId=providerId.trim();
			// check household identifier and gps were entered correctly
			if (StringUtils.isBlank(householdIdentifier) || StringUtils.isBlank(householdGps)) {
				log.debug("Null household identifier or GPS");
				saveFormInError(queue.getFileSystemUrl());
				mfes.saveErrorInDatabase(MobileFormEntryUtil.
					createError(getFormName(queue.getFileSystemUrl()), "Error processing household",
					"This household has no identifier or GPS specified", providerId,locationId ));
				return;
			}

			//pull out household data: includes meta, survey, economic, household_meta

			//Search for the identifier in the household database
			if (!MobileFormEntryUtil.isNewHousehold(householdIdentifier)
				&& !MobileFormEntryUtil.isSameHousehold(householdIdentifier, householdGps)) {

				log.error("household with identifier " + householdIdentifier + " has conflicting GPS coordinates: " + householdGps);
				saveFormInError(queue.getFileSystemUrl());
				mfes.saveErrorInDatabase(MobileFormEntryUtil.
					createError(getFormName(queue.getFileSystemUrl()), "Error processing household",
					"A duplicate household different from this one exists with the same identifier (" + householdIdentifier + ")", providerId,locationId ));
			} else {

				// get or create household
				log.debug("Processing household with id " + householdIdentifier);
				MobileFormHousehold household = MobileFormEntryUtil.getHousehold(mfes.getHousehold(householdIdentifier), doc, xp);

				// add economics
				for (Economic economic : MobileFormEntryUtil.getEconomic(doc, xp)) {
					household.addEconomic(economic);
				}

				// add Survey
				Survey survey = MobileFormEntryUtil.getSurvey(doc, xp);
				household.addSurvey(survey);

				// save the household
				mfes.saveHousehold(household);

				// save the household and encounter in Household Module
				HouseholdModuleConverter.getInstance().addHousehold(household);
				HouseholdModuleConverter.getInstance().addEncounter(household, survey);

				// queue form for splitting
				saveFormInPendingSplit(queue.getFileSystemUrl());
			}
		} catch (SAXParseException s) {
			log.info("An invalid household file. Automatically deleted", s);
			MobileFormEntryUtil.deleteFile(queue.getFileSystemUrl());
		} catch (Throwable t) {
			log.error("Error while parsing mobile entry (" + householdIdentifier + ")", t);
			//put file in error table and move it to error directory
			saveFormInError(queue.getFileSystemUrl());
			mfes.saveErrorInDatabase(MobileFormEntryUtil.
				createError(getFormName(queue.getFileSystemUrl()), "Error Parsing household form", t.getMessage(), providerId,locationId ));
		}
	}

	/**
	 * Transform the next pending MobileFormQueue entry. If there are no
	 * pending items in the queue, this method simply returns quietly.
	 *
	 * @return true if a queue entry was processed, false if queue was empty
	 */
	public void processMobileFormQueue() {
		MobileFormEntryService mobileService;
		synchronized (isRunning) {
			if (isRunning) {
				log.warn("MobileFormsQueue processor aborting (another processor already running)");
				return;
			}
			isRunning = true;
		}
		try {
			mobileService = (MobileFormEntryService) Context.getService(MobileFormEntryService.class);
		} catch (APIException e) {
			log.debug("MobileFormEntryService not found", e);
			return;
		}
		try {
			File queueDir = MobileFormEntryUtil.getMobileFormsDropDir();
			for (File file : queueDir.listFiles()) {
				MobileFormQueue queue = mobileService.getMobileFormEntryQueue(file.getAbsolutePath());

				// Log this sync
				SyncLogger logger = getSyncLogger();
				logger.createSyncLog(file);
				processMobileForm(queue);
			}
		} catch (Exception e) {
			log.error("Problem occured while processing AMRS Mobile Forms queue", e);
		} finally {
			isRunning = false;
		}
	}

	/**
	 * Stores a form in a specified folder after processing.
	 */
	private void saveForm(String oldFormPath, String newFormPath) {
		try {
			if (oldFormPath != null) {
				File file = new File(oldFormPath);

				//move the file to specified new directory
				file.renameTo(new File(newFormPath));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	/**
	 * Archives a mobile form after successful processing
	 */
	private void saveFormInPendingSplit(String formPath) {
		String pendingSplitFilePath = MobileFormEntryUtil.getMobileFormsPendingSplitDir().getAbsolutePath() + getFormName(formPath);

		saveForm(formPath, pendingSplitFilePath);
	}

	/**
	 * Stores an erred form in the error directory
	 *
	 * @param formPath
	 */
	private void saveFormInError(String formPath) {
		String errorFilePath = MobileFormEntryUtil.getMobileFormsErrorDir().getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, errorFilePath);
	}

	/**
	 * Extracts form name from an absolute file path
	 *
	 * @param formPath
	 * @return
	 */
	private String getFormName(String formPath) {
		return formPath.substring(formPath.lastIndexOf(File.separatorChar));
	}

	/**
	 * @return XPathFactory to be used for obtaining data from the parsed
	 * XML
	 */
	private XPathFactory getXPathFactory() {
		if (xPathFactory == null) {
			xPathFactory = XPathFactory.newInstance();
		}
		return xPathFactory;
	}

	/**
	 * @return SyncLogger to be used by the process
	 */
	private SyncLogger getSyncLogger() {
		if (syncLogger == null) {
			try {
				syncLogger = new SyncLogger();
			} catch (APIException e) {
				log.debug("SyncLogger not found", e);
				return null;
			}
		}
		return syncLogger;
	}
}
