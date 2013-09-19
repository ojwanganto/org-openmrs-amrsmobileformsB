/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.amrsmobileforms.web;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.*;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.module.amrsmobileforms.util.XFormEditor;
import org.openmrs.util.OpenmrsUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;



/**
 *DWR class for AMRSMobileForms module
 */
public class DWRAMRSMobileFormsService {

    private static final Log log = LogFactory.getLog(DWRAMRSMobileFormsService.class);

    public EconomicConceptMap saveEconomicConceptMap(Integer id, Integer economicId, Integer conceptId) {
        MobileFormEntryService service = Context.getService(MobileFormEntryService.class);

        EconomicConceptMap ecm;

        if (id == null) {
            ecm = new EconomicConceptMap();
        } else {
            ecm = service.getEconomicConceptMap(id);
        }

        ecm.setEconomic(service.getEconomicObjectById(economicId));
        ecm.setConcept(Context.getConceptService().getConcept(conceptId));

        return service.saveEconomicConceptMap(ecm);
    }



    public List<MobileFormEntryErrorModel> populateCommentForm(Integer errorId) {
        return getErrorObject(errorId);
    }
    /**
     * Given an id, this method creates an error model
     *
     * @param errorId
     * @return List of errors
     */
    private static List<MobileFormEntryErrorModel> getErrorObject(Integer errorId) {
        MobileFormEntryService mfs = (MobileFormEntryService) Context.getService(MobileFormEntryService.class);
        List<MobileFormEntryErrorModel> list = new Vector<MobileFormEntryErrorModel>();
        MobileFormEntryError error = mfs.getErrorById(errorId);
        if (error != null) {
            String formName = error.getFormName();
            String filePath = getAbsoluteFilePath(formName, mfs);
            error.setFormName(createFormData(error.getFormName(), mfs));
            MobileFormEntryErrorModel errorForm = new MobileFormEntryErrorModel(error, getFormType(formName));
            errorForm.setFormPath(filePath);
            list.add(errorForm);
        }
        return list;
    }

    private static String getFormType(String formName) {
        if (StringUtils.isEmpty(formName)) {
            return null;
        }
        // TODO make this more secure ... not all forms will have "HCT" in the name.
        if (formName.contains("HCT")) {
            return "household";
        }
        return "patient";
    }

    /**
     * Converts an xml file specified by <b>formPath</b> to a string
     *
     * @param formName
     * @param mfs
     * @return String representation of the file
     */
    private static String createFormData(String formName, MobileFormEntryService mfs) {

        MobileFormQueue queue = mfs.getMobileFormEntryQueue(MobileFormEntryUtil.getMobileFormsErrorDir().getAbsolutePath()
                + formName);
        return queue.getFormData();
    }

    /**
     * Takes in Mobile Queue and returns an absolute Path
     *
     * @param formName
     * @param mfs
     * @return String absolute path of the file
     */
    private static String getAbsoluteFilePath(String formName, MobileFormEntryService mfs) {

        MobileFormQueue queue = mfs.getMobileFormEntryQueue(MobileFormEntryUtil.getMobileFormsErrorDir().getAbsolutePath()
                + formName);
        return queue.getFileSystemUrl();
    }

    /**
     * Controller for commentOnError post jsp Page
     */
    public String saveComment(Integer errorId, String comment) {
        if (comment.trim().length() > 0) {
            MobileFormEntryService mfs = (MobileFormEntryService) Context.getService(MobileFormEntryService.class);
            MobileFormEntryError error = mfs.getErrorById(errorId);
            error.setComment(comment);
            error.setCommentedBy(Context.getAuthenticatedUser());
            error.setDateCommented(new Date());
            mfs.saveErrorInDatabase(error);
            return "Comment saved successfully";
        } else {

            return "A null comment was encountered";
        }

    }

    /**
     * Controller for resolveError post jsp Page
     */

    public List resolveError(
            String householdId,
            Integer errorId,
            String errorItemAction,
            String birthDate,
            String patientIdentifier,
            String providerId,
            String householdIdentifier,
            Integer patientId
    ) {
        MobileFormEntryService mobileService;
        String filePath;
        List statusInfo = new ArrayList();


        // user must be authenticated (avoids authentication errors)
        if (Context.isAuthenticated()) {
            if (!Context.getAuthenticatedUser().hasPrivilege(
                    MobileFormEntryConstants.PRIV_RESOLVE_MOBILE_FORM_ENTRY_ERROR)) {
                //httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "amrsmobileforms.action.noRights");
                statusInfo.add(0);
                statusInfo.add("Sorry, you do not have privileges for the operation");
                return statusInfo;
            }

            mobileService = Context.getService(MobileFormEntryService.class);

            // fetch the MobileFormEntryError item from the database
            MobileFormEntryError errorItem = mobileService.getErrorById(errorId);
            filePath = MobileFormEntryUtil.getMobileFormsErrorDir().getAbsolutePath() + errorItem.getFormName();
            if ("linkHousehold".equals(errorItemAction)) {
                if (mobileService.getHousehold(householdId) == null) {
                    //httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "amrsmobileforms.resolveErrors.action.createLink.error");
                    statusInfo.add(0);
                    statusInfo.add("You provided a wrong household ID!");
                    return statusInfo;

                } else {

                    if (XFormEditor.editNode(filePath,
                            MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_HOUSEHOLD_IDENTIFIER, householdId)) {
                        // put form in queue for normal processing

                        if (OpenmrsUtil.nullSafeEquals(getFormType(errorItem.getFormName()), "household")) {
                            moveAndDeleteError(MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath(), errorItem);
                        } else {
                            moveAndDeleteError(MobileFormEntryUtil.getMobileFormsQueueDir().getAbsolutePath(), errorItem);
                        }
                        statusInfo.add(1);
                        statusInfo.add("Patient-Household link creation was successfull");
                        return statusInfo;

                    }
                    else {
                        statusInfo.add(0);
                        statusInfo.add("could not update form details!");
                        return statusInfo;
                    }
                }
            } else if ("assignBirthdate".equals(errorItemAction)) {
                // format provided birthdate and insert into patient data like so:
                // <patient.birthdate openmrs_table="patient" openmrs_attribute="birthdate">2009-12-25</patient.birthdate>
                if (StringUtils.isNotEmpty(birthDate)) {
                    DateFormat reader = DateFormat.getDateInstance(DateFormat.SHORT, Context.getLocale());
                    DateFormat writer = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        String formattedDate = writer.format(reader.parse(birthDate));
                        if (XFormEditor.editNode(filePath,
                                MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_BIRTHDATE, formattedDate)) {
                            // put form in queue for normal processing
                            if (OpenmrsUtil.nullSafeEquals(getFormType(errorItem.getFormName()), "household")) {
                                moveAndDeleteError(MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath(), errorItem);
                            } else {
                                moveAndDeleteError(MobileFormEntryUtil.getMobileFormsQueueDir().getAbsolutePath(), errorItem);
                            }

                            statusInfo.add(1);
                            statusInfo.add("Birth Date assigned successfully");
                            return statusInfo;
                        }
                    } catch (ParseException e) {
                        String error = "Birthdate was not assigned, Invalid date entered: " + birthDate;
                        //httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, error);
                        log.error(error, e);

                        statusInfo.add(0);
                        statusInfo.add("Invalid date of birth");
                        return statusInfo;

                    }
                } else {
                    //httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Birthdate was not assigned, Null object entered");

                    statusInfo.add(0);
                    statusInfo.add("You entered an empty date of birth");
                    return statusInfo;

                }
            } else if ("newIdentifier".equals(errorItemAction)) {
                if (patientIdentifier != null && patientIdentifier.trim() != "") {
                    if (reverseNodes(filePath, patientIdentifier)) {
                        // put form in queue for normal processing
                        if (OpenmrsUtil.nullSafeEquals(getFormType(errorItem.getFormName()), "household")) {
                            moveAndDeleteError(MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath(), errorItem);
                        } else {
                            moveAndDeleteError(MobileFormEntryUtil.getMobileFormsQueueDir().getAbsolutePath(), errorItem);
                        }

                        statusInfo.add(1);
                        statusInfo.add("The new Patient ID was assigned successfully");
                        return statusInfo;

                    }
                } else {
                    //httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "amrsmobileforms.resolveErrors.action.newIdentifier.error");
                    statusInfo.add(0);
                    statusInfo.add("You entered an empty identifier");
                    return statusInfo;

                }
            } else if ("linkProvider".equals(errorItemAction)) {
                if (providerId != null && providerId.trim() != "") {

                    //edit node with the right provider id using the provided system/person id
                    providerId =Context.getUserService().getUser(Integer.parseInt(providerId)).getSystemId() ;

                    if (OpenmrsUtil.nullSafeEquals(getFormType(errorItem.getFormName()), "household")) {
                        String encounterNodeToEdit=  MobileFormEntryConstants.HOUSEHOLD_PREFIX +MobileFormEntryConstants
                                .HOUSEHOLD_INDIVIDUALS_PREFIX+"/individual/encounter/encounter.provider_id";

                        String surveyNodeToEdit = MobileFormEntryConstants.SURVEY_PREFIX + "/" + MobileFormEntryConstants
                                .SURVEY_PROVIDER_ID;

                        if (XFormEditor.editNode(filePath,surveyNodeToEdit,providerId) && XFormEditor.editNode
                                (filePath,encounterNodeToEdit,providerId) ) {
                            moveAndDeleteError(MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath(), errorItem);
                            statusInfo.add(1);
                            statusInfo.add("Error-Provider link created successfully");
                            return statusInfo;
                        }
                        else {
                            statusInfo.add(0);
                            statusInfo.add("Could not link the form to the provider");
                            return statusInfo;
                        }
                    }
                    else {

                        String encounterNodeToEdit = MobileFormEntryConstants.ENCOUNTER_NODE + "/" +
                                MobileFormEntryConstants
                                .ENCOUNTER_PROVIDER;
                        if (XFormEditor.editNode(filePath,encounterNodeToEdit,providerId)){
                            moveAndDeleteError(MobileFormEntryUtil.getMobileFormsQueueDir().getAbsolutePath(), errorItem);
                            statusInfo.add(1);
                            statusInfo.add("Error-Provider link created successfully");
                            return statusInfo;
                        }
                        else {
                            statusInfo.add(0);
                            statusInfo.add("Could not link the form to the provider");
                            return statusInfo;
                        }

                    }

                    //providerId = Context.getUserService().getUser(Integer.parseInt(providerId)).getSystemId();
                }
                else{
                    statusInfo.add(0);
                    statusInfo.add("A null provider was submitted");
                    return statusInfo;
                }
            }else if ("linkPatient".equals(errorItemAction)) {


                if (patientId != null) {
                    // patientId = Context.getPatientService().getPatient(Integer.parseInt(patientId)).getPatientId();
                    if (XFormEditor.editNode(filePath,
                            MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_ID, patientId.toString())) {
                        // put form in queue for normal processing
                        if (OpenmrsUtil.nullSafeEquals(getFormType(errorItem.getFormName()), "household")) {
                            moveAndDeleteError(MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath(), errorItem);
                        } else {
                            moveAndDeleteError(MobileFormEntryUtil.getMobileFormsQueueDir().getAbsolutePath(), errorItem);
                        }

                        log.info("The system managed to find the following patient: "+patientId);
                        statusInfo.add(1);
                        statusInfo.add("Error-Patient link created successfully");
                        return statusInfo;
                    }
                    else{

                        statusInfo.add(0);
                        statusInfo.add("Error! Could not associate the error with the selected Patient.");
                        return statusInfo;

                    }
                } else {
                    //httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "(Null) Invalid provider ID");

                    statusInfo.add(0);
                    statusInfo.add("No Patient was selected!");
                    return statusInfo;
                }
            } else if ("createPatient".equals(errorItemAction)) {
                // put form in queue for normal processing
                if (OpenmrsUtil.nullSafeEquals(getFormType(errorItem.getFormName()), "household")) {
                    moveAndDeleteError(MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath(), errorItem);
                } else {
                    moveAndDeleteError(MobileFormEntryUtil.getMobileFormsQueueDir().getAbsolutePath(), errorItem);
                }

                statusInfo.add(1);
                statusInfo.add("Patient created successfully");
                return statusInfo;

            } else if ("deleteError".equals(errorItemAction)) {
                // delete the mobileformentry error queue item
                mobileService.deleteError(errorItem);
                //and delete from the file system
                MobileFormEntryUtil.deleteFile(filePath);
                statusInfo.add(1);
                statusInfo.add("Error Deleted successfully");
                return statusInfo;

            } else if ("deleteComment".equals(errorItemAction)) {
                //set comment to null and save
                errorItem.setComment(null);
                mobileService.saveErrorInDatabase(errorItem);

                statusInfo.add(1);
                statusInfo.add("Comment Deleted successfully");
                return statusInfo;

            } else if ("newHousehold".equals(errorItemAction)) {
                if (householdIdentifier != null && householdIdentifier.trim() != "") {
                    // first change household id
                    if (XFormEditor.editNode(filePath,
                            MobileFormEntryConstants.HOUSEHOLD_PREFIX + MobileFormEntryConstants.HOUSEHOLD_META_PREFIX + "/"
                                    + MobileFormEntryConstants.HOUSEHOLD_META_HOUSEHOLD_ID, householdIdentifier)) {
                    } else {
                        //httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Error assigning new household identififer");

                        statusInfo.add(0);
                        statusInfo.add("Could not change Household Id");
                        return statusInfo;
                    }

                    // then change all patient household pointers
                    if (XFormEditor.editNodeList(filePath,
                            MobileFormEntryConstants.HOUSEHOLD_PREFIX + MobileFormEntryConstants.HOUSEHOLD_INDIVIDUALS_PREFIX,
                            "patient/" + MobileFormEntryConstants.PATIENT_HOUSEHOLD_IDENTIFIER, householdIdentifier)) {
                        // drop form in queue for normal processing
                        if (OpenmrsUtil.nullSafeEquals(getFormType(errorItem.getFormName()), "household")) {
                            moveAndDeleteError(MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath(), errorItem);
                        } else {
                            moveAndDeleteError(MobileFormEntryUtil.getMobileFormsQueueDir().getAbsolutePath(), errorItem);
                        }
                        statusInfo.add(1);
                        statusInfo.add("The new Household ID was assigned successfully");
                        return statusInfo;

                    } else {
                        //httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Error assigning new household identififer");

                        statusInfo.add(0);
                        statusInfo.add("Could not change household pointers");
                        return statusInfo;
                    }
                } else {
                    //httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Error assigning new household identififer");

                    statusInfo.add(0);
                    statusInfo.add("You provided an empty Household Id");
                    return statusInfo;
                }
            } else if ("noChange".equals(errorItemAction)) {

                statusInfo.add(2);
                statusInfo.add("No change was made to the error");
                return statusInfo;

            } else {
                throw new APIException("Invalid action selected for: " + errorId);
            }
        }

        //httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "amrsmobileforms.resolveErrors.action.success");

        statusInfo.add(2);
        statusInfo.add("Please, you are not authenticated to do this");

        return statusInfo;
    }


    /**
     * Reverses patient Identifier nodes after for a form with more than one
     *
     * @param filePath
     * @param patientIdentifier
     * @return
     */
    private static boolean reverseNodes(String filePath, String patientIdentifier) {
        try {

            File file = new File(filePath);

            // Create instance of DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();

            // Using existing XML Document
            Document doc = docBuilder.parse(file);
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xp = xpf.newXPath();

            Node curNode = (Node) xp.evaluate(MobileFormEntryConstants.PATIENT_NODE, doc, XPathConstants.NODE);
            String patientAmpathIdentifier = xp.evaluate(MobileFormEntryConstants.PATIENT_HCT_IDENTIFIER, curNode);

            // If patient has an AMPATH ID we use it to create the patient
            if (patientAmpathIdentifier != null && patientAmpathIdentifier != "") {
                XFormEditor.editNode(filePath,
                        MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_IDENTIFIER, patientAmpathIdentifier);
                XFormEditor.editNode(filePath,
                        MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_IDENTIFIER_TYPE, "3");
                XFormEditor.editNode(filePath,
                        MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_HCT_IDENTIFIER, patientIdentifier);
            } else {
                //Patient has only one id
                XFormEditor.editNode(filePath,
                        MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_IDENTIFIER, patientIdentifier);
                XFormEditor.editNode(filePath,
                        MobileFormEntryConstants.PATIENT_NODE + "/" + MobileFormEntryConstants.PATIENT_IDENTIFIER_TYPE, "8");
            }
        } catch (Throwable t) {
            log.error("Error reversing nodes", t);
            return false;
        }
        return true;
    }


    /**
     * Stores a form in a specified folder
     */
    private static boolean saveForm(String oldFormPath, String newFormPath) {

        try {
            if (oldFormPath != null) {
                File file = new File(oldFormPath);
                File newDestination = new File(newFormPath);

                //move the file to specified new directory
                /*check to see if the new file path exists. renameTo does nothing if same path is encountered*/
                if(newDestination.exists())
                    newDestination.deleteOnExit();//safely deletes the file.

                if(file.renameTo(new File(newFormPath))){
                    log.info("Moving error file to drop directory was successful");
                    return true;
                }
                else {
                    log.info("Failed to move file to drop directory");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return false;

    }


    /**
     *
     * @return
     */
    private MobileFormEntryService getMobileFormEntryService(){
        return Context.getService(MobileFormEntryService.class);
    }

    /**
     *
     * @param destination
     * @param error
     */
    private void moveAndDeleteError(String destination, MobileFormEntryError error) {
        // find error location
        String filePath = MobileFormEntryUtil.getMobileFormsErrorDir().getAbsolutePath() + error.getFormName();
        // put form in queue for normal processing
        if(saveForm(filePath, destination + error.getFormName())){
            // delete the mobileformentry error queue item
            getMobileFormEntryService().deleteError(error);
        }
        else{
            log.error("Could not complete error resolution process");
        }

    }

    /*
      * A function that enables for searching of patients using either patient number or names
      *
      *
      *
      */

    public List<Person> getPersons(String phrase){

        PersonService ps = Context.getPersonService();
        List<Person> foundPersons = new ArrayList<Person>();


        foundPersons = ps.getPeople(phrase, false);

        return foundPersons;
    }

}
