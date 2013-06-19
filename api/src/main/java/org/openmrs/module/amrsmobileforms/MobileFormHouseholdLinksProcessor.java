package org.openmrs.module.amrsmobileforms;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;

/**
 * Processes Patient forms by linking patients to their specified households
 *
 * @author Samuel Mbugua
 */
@Transactional
public class MobileFormHouseholdLinksProcessor {

	private static final Log log = LogFactory.getLog(MobileFormHouseholdLinksProcessor.class);
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private XPathFactory xPathFactory;
	private MobileFormEntryService mobileService;

	// allow only one running instance
	private static Boolean isRunning = false;

	public MobileFormHouseholdLinksProcessor() {
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			this.getMobileService();
		} catch (Exception e) {
			log.error("Problem occurred while creating document builder", e);
		}
	}

	/**
	 * Processes all forms in pending link directory linking patients to
	 * households
	 *
	 * @param filePath
	 * @param queue
	 * @throws APIException
	 */
	private void processPendingLinkForm(String filePath, MobileFormQueue queue) throws APIException {

		log.debug("Linking Patient to household");
        String providerId=null;
        String locationId=null;
        String householdLocation=null;
		try {
			String formData = queue.getFormData();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			XPathFactory xpf = getXPathFactory();
			XPath xp = xpf.newXPath();
			Document doc = docBuilder.parse(IOUtils.toInputStream(formData));

            Node curNode = (Node) xp.evaluate("/form/patient", doc, XPathConstants.NODE);
			String patientIdentifier = xp.evaluate(MobileFormEntryConstants.PATIENT_IDENTIFIER, curNode);
			String householdId = xp.evaluate(MobileFormEntryConstants.PATIENT_HOUSEHOLD_IDENTIFIER, curNode);

            //find  provider Id from the document
            curNode=(Node)  xp.evaluate(MobileFormEntryConstants.ENCOUNTER_NODE, doc, XPathConstants.NODE);
            providerId = xp.evaluate(MobileFormEntryConstants.ENCOUNTER_PROVIDER, curNode);
            providerId=providerId.trim();

            householdLocation=xp.evaluate(MobileFormEntryConstants.ENCOUNTER_LOCATION, curNode);

            //Clean location id by removing decimal points
            locationId=MobileFormEntryUtil.cleanLocationEntry(householdLocation) ;


            // First Ensure there is at least a patient identifier in the form
            if (!StringUtils.hasText(MobileFormEntryUtil.getPatientIdentifier(doc))) {
                // form has no patient identifier : move to error
                saveFormInError(filePath);
                mobileService.saveErrorInDatabase(MobileFormEntryUtil.createError(getFormName(filePath), "Error linking patient",
                        "Patient has no identifier, or the identifier provided is invalid",providerId, locationId));
                return;
            }

			if (!StringUtils.hasText(householdId) || MobileFormEntryUtil.isNewHousehold(householdId)) {
				saveFormInError(filePath);
				mobileService.saveErrorInDatabase(MobileFormEntryUtil.createError(getFormName(filePath), "Error linking patient",
						"Patient is not linked to household or household Id provided is invalid", providerId, locationId));
			} else {
				Patient pat = MobileFormEntryUtil.getPatient(patientIdentifier);
				MobileFormHousehold household = mobileService.getHousehold(householdId);
				if (pat != null && household != null) {
					if (MobileFormEntryUtil.isNewLink(pat.getId())) {
						HouseholdMember householdMember = new HouseholdMember();
						householdMember.setHouseholdMemberId(pat.getId());
						householdMember.setHousehold(household);
						mobileService.saveHouseholdMember(householdMember);
					}

					// create a membership in MobileFormHousehold Module
					// HouseholdModuleConverter.getInstance().addMembership(pat, household, false);
				}
				saveFormInPostProcessor(filePath);
			}
		} catch (Throwable t) {
			log.error("Error while linking patient to household", t);
			//put file in error queue
			saveFormInError(filePath);
			mobileService.saveErrorInDatabase(MobileFormEntryUtil.createError(getFormName(filePath), "Error while linking patient to house hold", t.getMessage(), providerId, locationId));
		}
	}

	/**
	 * Processes each pending link entry. If there are no pending items in the
	 * queue, this method simply returns quietly.
	 */
	public void processMobileFormPendingLinkQueue() {
		synchronized (isRunning) {
			if (isRunning) {
				log.warn("MobileFormsHouseholdsLinks processor aborting (another processor already running)");
				return;
			}
			isRunning = true;
		}

		try {
			File pendingLinkQueueDir = MobileFormEntryUtil.getMobileFormsPendingLinkDir();
			for (File file : pendingLinkQueueDir.listFiles()) {
				MobileFormQueue queue = mobileService.getMobileFormEntryQueue(file.getAbsolutePath());
				processPendingLinkForm(file.getAbsolutePath(), queue);
			}
		} catch (Exception e) {
			log.error("Problem occured while processing pending link queue", e);
		} finally {
			isRunning = false;
		}
	}

	/**
	 * Sends a form to post processor after successful processing
	 */
	private void saveFormInPostProcessor(String formPath) {
		String postProcessFilePath = MobileFormEntryUtil.getMobileFormsPostProcessDir().getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, postProcessFilePath);
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
	 * Extracts form name from an absolute file path
	 *
	 * @param formPath
	 * @return
	 */
	private String getFormName(String formPath) {
		return formPath.substring(formPath.lastIndexOf(File.separatorChar));
	}

	/**
	 * @return XPathFactory to be used for obtaining data from the parsed XML
	 */
	private XPathFactory getXPathFactory() {
		if (xPathFactory == null) {
			xPathFactory = XPathFactory.newInstance();
		}
		return xPathFactory;
	}

	/**
	 * @return MobileFormEntryService to be used by the process
	 */
	private MobileFormEntryService getMobileService() {
		if (mobileService == null) {
			try {
				mobileService = (MobileFormEntryService) Context.getService(MobileFormEntryService.class);
			} catch (APIException e) {
				log.debug("MobileFormEntryService not found");
				return null;
			}
		}
		return mobileService;
	}
}
