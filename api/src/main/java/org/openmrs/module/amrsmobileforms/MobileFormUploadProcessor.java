package org.openmrs.module.amrsmobileforms;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryFileUploader;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.module.amrsmobileforms.util.XFormEditor;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Processes Mobile forms Split Queue entries. Submits all split forms to the
 * xforms module for processing
 *
 * @author Samuel Mbugua
 */
@Transactional
public class MobileFormUploadProcessor {

	private static final Log log = LogFactory.getLog(MobileFormUploadProcessor.class);
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private XPathFactory xPathFactory;
	private MobileFormEntryService mobileService;
    private Integer providerPersonId;
	// allow only one running instance
	private static Boolean isRunning = false;

	public MobileFormUploadProcessor() {
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			this.getMobileService();
		} catch (Exception e) {
			log.error("Problem occurred while creating document builder", e);
		}
	}

	/**
	 * Process an existing entries in the mobile form split queue
	 *
	 * @param filePath
	 */
	private void processSplitForm(String filePath, MobileFormQueue queue) throws APIException {

		log.debug("Sending splitted mobile forms to the xform module");
        String providerId=null;
        String locationId =null;
        String householdLocation=null;


		try {

			// establish a doc to work from
			String formData = queue.getFormData();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			XPathFactory xpf = getXPathFactory();
			XPath xp = xpf.newXPath();
			Document doc = docBuilder.parse(IOUtils.toInputStream(formData));


			Node curNode = (Node) xp.evaluate(MobileFormEntryConstants.PATIENT_NODE, doc, XPathConstants.NODE);
			String patientIdentifier = xp.evaluate(MobileFormEntryConstants.PATIENT_IDENTIFIER, curNode);
			String patientAmpathIdentifier = xp.evaluate(MobileFormEntryConstants.PATIENT_HCT_IDENTIFIER, curNode);
			String householdId = xp.evaluate(MobileFormEntryConstants.PATIENT_HOUSEHOLD_IDENTIFIER, curNode);
			String birthDate = xp.evaluate(MobileFormEntryConstants.PATIENT_BIRTHDATE, curNode);
			String familyName = xp.evaluate(MobileFormEntryConstants.PATIENT_FAMILYNAME, curNode);
			String givenName = xp.evaluate(MobileFormEntryConstants.PATIENT_GIVENNAME, curNode);
			String middleName = xp.evaluate(MobileFormEntryConstants.PATIENT_MIDDLENAME, curNode);


          //find  provider Id from the document
            curNode=(Node)  xp.evaluate(MobileFormEntryConstants.ENCOUNTER_NODE, doc, XPathConstants.NODE);
            providerPersonId=MobileFormEntryUtil.getProviderId(xp.evaluate(MobileFormEntryConstants.ENCOUNTER_PROVIDER, curNode));

            providerId = xp.evaluate(MobileFormEntryConstants.ENCOUNTER_PROVIDER, curNode);
            providerId=providerId.trim();

            householdLocation=xp.evaluate(MobileFormEntryConstants.ENCOUNTER_LOCATION, curNode);

            //Clean location id by removing decimal points
            locationId=MobileFormEntryUtil.cleanLocationEntry(householdLocation) ;

           //Ensure there is a patient identifier in the form and
			// if without names just delete the form
			if (MobileFormEntryUtil.getPatientIdentifier(doc) == null || MobileFormEntryUtil.getPatientIdentifier(doc).trim() == "") {
				if ((familyName == null || familyName.trim() == "")
						&& (givenName == null || givenName == "")) {
					MobileFormEntryUtil.deleteFile(filePath);
					log.info("Deleted an empty individual file");
				} else {
					// form has no patient identifier but has names : move to error
					saveFormInError(filePath);
					mobileService.saveErrorInDatabase(MobileFormEntryUtil.createError(getFormName(filePath), "Error processing patient",
							"Patient has no identifier, or the identifier provided is invalid", providerId, locationId));
				}
				return;
			}

			// Ensure Family name and Given names are not blanks
			if (StringUtils.isBlank(familyName) || StringUtils.isBlank(givenName)) {
				saveFormInError(filePath);
				mobileService.saveErrorInDatabase(MobileFormEntryUtil.createError(getFormName(filePath), "Error processing patient",
						"Patient has no valid names specified, Family Name and Given Name are required", providerId, locationId));
				return;
			}

			// Ensure there is a valid provider id or name and return provider_id in the form
			if (StringUtils.isBlank(providerId)) {
				// form has no valid provider : move to error
				saveFormInError(filePath);
				mobileService.saveErrorInDatabase(MobileFormEntryUtil.createError(getFormName(filePath), "Error processing patient form",
						"Provider for this encounter is not provided, or the provider identifier provided is invalid", providerId, locationId));
				return;
			} else {
				// TODO understand why this is being replaced with itself -- seems frivolous
				XFormEditor.editNode(filePath,
						MobileFormEntryConstants.ENCOUNTER_NODE + "/" + MobileFormEntryConstants.ENCOUNTER_PROVIDER, providerPersonId.toString());
			}

			// ensure patient has birth date
			if (StringUtils.isBlank(birthDate)) {
				Integer yearOfBirth = MobileFormEntryUtil.getBirthDateFromAge(doc);
				if (yearOfBirth == null) {//patient has no valid birth-date
					saveFormInError(filePath);
					mobileService.saveErrorInDatabase(MobileFormEntryUtil.createError(getFormName(filePath), "Error processing patient", "Patient has no valid Birthdate", providerId, locationId));
					return;
				} else {
					//fix birth-date from age
					birthDate = "" + yearOfBirth + "-01-01";
					XFormEditor.editNode(filePath,
							MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_BIRTHDATE, birthDate);
				}
			}

			// Ensure that the patient has a household to link to
			if (StringUtils.isBlank(householdId) || MobileFormEntryUtil.isNewHousehold(householdId)) {
				saveFormInError(filePath);
				mobileService.saveErrorInDatabase(MobileFormEntryUtil.createError(getFormName(filePath), "Error processing patient",
						"Patient is not linked to household or household Id provided is invalid", providerId, locationId));
				return;
			}

			// Ensure if not new it is same person
			if (!MobileFormEntryUtil.isNewPatient(patientIdentifier)) {
				Patient pat = MobileFormEntryUtil.getPatient(patientIdentifier);

				// if we did not find any matches, throw a comprehensive error
				if (!matchesOnNameParts(Arrays.asList(givenName, middleName, familyName), pat.getNames())) {
					saveFormInError(filePath);
					StringBuilder sb = new StringBuilder();
					sb.append("The person with identifier ");
					sb.append(patientIdentifier);
					sb.append(" and name ");
					sb.append(givenName + " " + middleName + " " + familyName);
					sb.append(" did not match names in the system: ");
					sb.append(StringUtils.join(pat.getNames(), ", "));
					sb.append(".");

					mobileService.saveErrorInDatabase(MobileFormEntryUtil.createError(getFormName(filePath),
							"Error processing patient", sb.toString(), providerId, locationId));
					return;
				}
			}

			// If patient has an AMPATH ID we use it to create the patient
			if (!StringUtils.isBlank(patientAmpathIdentifier)) {
				XFormEditor.editNode(filePath,
						MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_IDENTIFIER, patientAmpathIdentifier);
				XFormEditor.editNode(filePath,
						MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_IDENTIFIER_TYPE, "3");
				XFormEditor.editNode(filePath,
						MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_HCT_IDENTIFIER, patientIdentifier);
			}


			//Finally send to xforms for processing
           	MobileFormEntryFileUploader.submitXFormFile(filePath);
            saveFormInPendingLink(filePath);

        } catch (Throwable t) {
			log.error("Error while sending form to xform module", t);

			// put file in error queue
			saveFormInError(filePath);
			mobileService.saveErrorInDatabase(MobileFormEntryUtil.createError(getFormName(filePath), "Error sending form to xform module", t.getMessage(), providerId, locationId));
		}
	}

	/**
	 * check for a match of name parts against the names from a given patient
	 *
	 * @param providedNames the list of name parts to check against
	 * @param personNames   set of PersonName objects for review
	 * @return whether a name part from the set of PersonNames matches one from the first list
	 * @should return true when only one name part is found in a person name
	 * @should return true when more than one name part matches
	 * @should return true when the found patient has more than one name
	 * @should return true when the matching name is not preferred
	 * @should match on name parts with additional spaces
	 * @should match on name parts with different capitalization
	 * @should return false if no name part is found and there is only one name
	 * @should return false if no name part is found and there are multiple names
	 * @should return false if no providedNames are provided
	 * @should return false if no personNames are provided
	 */
	protected boolean matchesOnNameParts(List<String> providedNames, Set<PersonName> personNames) {

		// check for null
		if (providedNames == null || personNames == null) {
			return false;
		}

		// build a list of normalized name parts from what we got in the form
		Set<String> nameParts = new HashSet<String>();
		for (String namePart : providedNames) {
			if (!StringUtils.isEmpty(namePart)) {
				nameParts.add(namePart.trim().toLowerCase());



			}
		}

		// check over the set of PersonNames
		for (PersonName personName : personNames) {
			for (String namePart : personName.getFullName().split(" ")) {
				if (!StringUtils.isBlank(namePart) && nameParts.contains(namePart.trim().toLowerCase())) {
					return true;
				}
			}
		}

		// assume there was no match
		return false;
	}

	/**
	 * Processes each split queue entry. If there are no pending items in the
	 * queue, this method simply returns quietly.
	 */
	public void processMobileFormUploadQueue() {
		synchronized (isRunning) {
			if (isRunning) {
				log.warn("MobileFormsSplitQueue processor aborting (another processor already running)");
				return;
			}
			isRunning = true;
		}

		try {
			File splitQueueDir = MobileFormEntryUtil.getMobileFormsQueueDir();
			for (File file : splitQueueDir.listFiles()) {
				MobileFormQueue queue = mobileService.getMobileFormEntryQueue(file.getAbsolutePath());
				processSplitForm(file.getAbsolutePath(), queue);
			}
		} catch (Exception e) {
			log.error("Problem occured while processing split queue", e);
		} finally {
			isRunning = false;
		}
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
	 * Stores a new patient file to pending link directory
	 *
	 * @param formPath
	 */
	private void saveFormInPendingLink(String formPath) {
		String pendingFilePath = MobileFormEntryUtil.getMobileFormsPendingLinkDir().getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, pendingFilePath);
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
