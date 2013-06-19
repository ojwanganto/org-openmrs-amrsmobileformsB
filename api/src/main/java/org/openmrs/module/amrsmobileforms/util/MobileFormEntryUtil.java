package org.openmrs.module.amrsmobileforms.util;

import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.Economic;
import org.openmrs.module.amrsmobileforms.EconomicObject;
import org.openmrs.module.amrsmobileforms.MobileFormHousehold;
import org.openmrs.module.amrsmobileforms.MobileFormEntryConstants;
import org.openmrs.module.amrsmobileforms.MobileFormEntryError;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.openmrs.module.amrsmobileforms.Survey;
import org.openmrs.module.xforms.util.DOMUtil;
import org.openmrs.util.OpenmrsUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides utilities needed when processing mobile forms.
 *
 * @author Samuel Mbugua
 */
public class MobileFormEntryUtil {

	private static Log log = LogFactory.getLog(MobileFormEntryUtil.class);
	private static String village, sublocation, location, division, district;

	public static File getMobileFormsResourcesDir() {
		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty(MobileFormEntryConstants.GP_MOBILE_FORMS_RESOURCES_DIR,
			MobileFormEntryConstants.GP_MOBILE_FORMS_RESOURCES_DIR_DEFAULT);
		File mobileFormsErrorDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		if (log.isDebugEnabled()) {
			log.debug("Loaded mobile forms resources directory from global properties: " + mobileFormsErrorDir.getAbsolutePath());
		}

		return mobileFormsErrorDir;
	}

	public static File getMobileFormsArchiveDir(Date date) {
		AdministrationService as = Context.getAdministrationService();
		String mobileFormsArchiveFileName = as.getGlobalProperty(MobileFormEntryConstants.GP_MOBILE_FORMS_ARCHIVE_DIR, MobileFormEntryConstants.GP_MOBILE_FORMS_ARCHIVE_DIR_DEFAULT);

		// replace %Y %M %D in the folderName with the date
		String folderName = replaceVariables(mobileFormsArchiveFileName, date);

		// get the file object for this potentially new file
		File mobileFormsArchiveDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);

		if (log.isDebugEnabled()) {
			log.debug("Loaded mobile forms archive directory from global properties: " + mobileFormsArchiveDir.getAbsolutePath());
		}

		return mobileFormsArchiveDir;
	}

	public static File getMobileFormsSyncLogDir() {

		String folderName = MobileFormEntryConstants.GP_MOBILE_FORMS_SYNC_LOG_DIR_DEFAULT;
		// get the file object for this potentially new file
		File mobileFormsSyncLogDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);

		if (log.isDebugEnabled()) {
			log.debug("Loaded mobile forms sync log directory from global properties: " + mobileFormsSyncLogDir.getAbsolutePath());
		}

		return mobileFormsSyncLogDir;
	}

	public static File getMobileFormsErrorDir() {
		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty(MobileFormEntryConstants.GP_MOBILE_FORMS_ERROR_DIR,
			MobileFormEntryConstants.GP_MOBILE_FORMS_ERROR_DIR_DEFAULT);
		File mobileFormsErrorDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		if (log.isDebugEnabled()) {
			log.debug("Loaded mobile forms error directory from global properties: " + mobileFormsErrorDir.getAbsolutePath());
		}

		return mobileFormsErrorDir;
	}

	/**
	 * Directory where forms are placed pending to be linked to households
	 *
	 * @return directory
	 */
	public static File getMobileFormsPendingLinkDir() {
		String folderName = MobileFormEntryConstants.GP_MOBILE_FORMS_PENDING_LINK_DIR;
		File mobileFormsPendingLinkDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		if (log.isDebugEnabled()) {
			log.debug("Loaded mobile forms pending link directory from global properties: " + mobileFormsPendingLinkDir.getAbsolutePath());
		}

		return mobileFormsPendingLinkDir;
	}

	/**
	 * Directory where forms are placed pending to be split into individual
	 * persons
	 *
	 * @return directory
	 */
	public static File getMobileFormsPendingSplitDir() {
		String folderName = MobileFormEntryConstants.GP_MOBILE_FORMS_PENDING_SPLIT_DIR;
		File mobileFormsPendingSplitDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		if (log.isDebugEnabled()) {
			log.debug("Loaded mobile forms pending split directory from global properties: " + mobileFormsPendingSplitDir.getAbsolutePath());
		}

		return mobileFormsPendingSplitDir;
	}

	/**
	 * Directory where forms are placed pending post processing
	 *
	 * @return directory
	 */
	public static File getMobileFormsPostProcessDir() {
		String folderName = MobileFormEntryConstants.GP_MOBILE_FORMS_POST_PROCESS_DIR;
		File mobileFormsPostProcessDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		if (log.isDebugEnabled()) {
			log.debug("Loaded mobile forms post process directory from global properties: " + mobileFormsPostProcessDir.getAbsolutePath());
		}

		return mobileFormsPostProcessDir;
	}

	/**
	 * Directory where forms are dropped by mobile devices
	 *
	 * @return directory
	 */
	public static File getMobileFormsDropDir() {
		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty(MobileFormEntryConstants.GP_MOBILE_FORMS_DROP_DIR,
			MobileFormEntryConstants.GP_MOBILE_FORMS_DROP_DIR_DEFAULT);
		File mobileFormsDropDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		if (log.isDebugEnabled()) {
			log.debug("Loaded mobile forms drop queue directory from global properties: " + mobileFormsDropDir.getAbsolutePath());
		}

		return mobileFormsDropDir;
	}

	public static File getMobileFormsQueueDir() {
		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty(MobileFormEntryConstants.GP_MOBILE_FORMS_QUEUE_DIR,
			MobileFormEntryConstants.GP_MOBILE_FORMS_QUEUE_DIR_DEFAULT);
		File mobileFormQueueDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		if (log.isDebugEnabled()) {
			log.debug("Loaded mobile forms queue directory from global properties: " + mobileFormQueueDir.getAbsolutePath());
		}

		return mobileFormQueueDir;
	}

	/**
	 * Replaces %Y in the string with the four digit year. Replaces %M with
	 * the two digit month Replaces %D with the two digit day Replaces %w
	 * with week of the year Replaces %W with week of the month
	 *
	 * @param str String filename containing variables to replace with date
	 * strings
	 * @return String with variables replaced
	 */
	public static String replaceVariables(String str, Date d) {

		Calendar calendar = Calendar.getInstance();
		if (d != null) {
			calendar.setTime(d);
		}

		int year = calendar.get(Calendar.YEAR);
		str = str.replace("%Y", Integer.toString(year));

		int month = calendar.get(Calendar.MONTH) + 1;
		String monthString = Integer.toString(month);
		if (month < 10) {
			monthString = "0" + monthString;
		}
		str = str.replace("%M", monthString);

		int day = calendar.get(Calendar.DATE);
		String dayString = Integer.toString(day);
		if (day < 10) {
			dayString = "0" + dayString;
		}
		str = str.replace("%D", dayString);

		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		String weekString = Integer.toString(week);
		if (week < 10) {
			weekString = "0" + week;
		}
		str = str.replace("%w", weekString);

		int weekmonth = calendar.get(Calendar.WEEK_OF_MONTH);
		String weekmonthString = Integer.toString(weekmonth);
		if (weekmonth < 10) {
			weekmonthString = "0" + weekmonthString;
		}
		str = str.replace("%W", weekmonthString);

		return str;
	}

	public static boolean isNewHousehold(String householdId) {
		MobileFormEntryService mfes = (MobileFormEntryService) Context.getService(MobileFormEntryService.class);
		if (mfes.getHousehold(householdId) == null) {
			return true;
		}
		return false;
	}

	/**
	 * identifies a household by its identifier and gpsLocation as being already entered.
	 *
	 * @param identifier
	 * @param gpsLocation
	 * @return true if household already exists in the database
	 * @should ignore GPS coordinates
	 */
	public static boolean isSameHousehold(String identifier, String gpsLocation) {
		MobileFormEntryService mfes = (MobileFormEntryService) Context.getService(MobileFormEntryService.class);
		MobileFormHousehold household = mfes.getHousehold(identifier);

		// GPS restrictions are causing errors -- need to loosen, ignoring for now.
		/*
		if (household.getGpsLocation().equals(getGPS(gpsLocation))) {
			return true;
		}
		return false;
		*/
		
		return (household != null);
	}

	public static boolean isNewPatient(String patientIdentifier) {
		List<Patient> patients = Context.getPatientService().getPatients(null, patientIdentifier, null, true);
		if (patients == null || patients.size() < 1) {
			return true;
		}
		return false;
	}

	public static Patient getPatient(String patientIdentifier) {
		return Context.getPatientService().getPatients(null, patientIdentifier, null, false).get(0);
	}

	public static boolean isNewLink(Integer patientId) {
		MobileFormEntryService mfes = Context.getService(MobileFormEntryService.class);
		if (mfes.getHouseholdMemberById(patientId) == null) {
			return true;
		}
		return false;
	}

	public static MobileFormEntryError createError(String formName, String error, String errorDetails,String  providerId, String locationId) {
		MobileFormEntryError mobileFormEntryError = new MobileFormEntryError();
		mobileFormEntryError.setFormName(formName);
		mobileFormEntryError.setError(error);
		mobileFormEntryError.setErrorDetails(errorDetails);
        mobileFormEntryError.setProviderId(providerId);
        mobileFormEntryError.setLocationId(locationId);
		return mobileFormEntryError;
	}
    public static String cleanLocationEntry(String householdLocation) {
        householdLocation=householdLocation.trim();
        String locationId=null;
        if(householdLocation.length()>0)  {

            if(householdLocation.length()>2) {
                        String lastDec=householdLocation.substring(householdLocation.length()-2);
                        if (lastDec.equals(".0")) {
                            locationId=householdLocation.substring(0,householdLocation.length()-2);
                        } else {
                            locationId= householdLocation;
                        }

            }
            else{
                locationId= householdLocation;
            }

        }

        return   locationId;
    }
	public static MobileFormHousehold getHousehold(MobileFormHousehold household, Document doc, XPath xp) throws XPathExpressionException {
		Node householdMetaNode = (Node) xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_PREFIX + MobileFormEntryConstants.HOUSEHOLD_META_PREFIX, doc, XPathConstants.NODE);
		if (household == null) {
			household = new MobileFormHousehold();
		}
		if (xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_VILLAGE, householdMetaNode) != null) {
			setLocations(xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_VILLAGE, householdMetaNode));
		}

		//Set household identifier
		household.setHouseholdIdentifier(xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_HOUSEHOLD_ID, householdMetaNode));

		//Set the locations
		household.setVillage(village);
		household.setSublocation(sublocation);
		household.setLocation(location);
		household.setDivision(division);
		household.setDistrict(district);

		//Set household occupants
		household.setAdults(getInteger(xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_HOUSEHOLD_ADULTS, householdMetaNode)));
		household.setChildren(getInteger(xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_CHILDREN_UNDER13, householdMetaNode)));

		//Set eligibles for testing
		household.setAdultsEligible(getInteger(xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_HOUSEHOLD_ADULTS_ELIGIBLE, householdMetaNode)));
		household.setChildrenEligible(getInteger(xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_CHILDREN_UNDER13_ELIGIBLE, householdMetaNode)));

		//Set GPS location
		household.setGpsLocation(getGPS(xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_GPS_LOCATION, householdMetaNode)));
		return household;

	}

	public static List<Economic> getEconomic(Document doc, XPath xp) throws XPathExpressionException {
		Node economicNode = (Node) xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_PREFIX + MobileFormEntryConstants.HOUSEHOLD_ECONOMIC_PREFIX, doc, XPathConstants.NODE);
		MobileFormEntryService mfes = (MobileFormEntryService) Context.getService(MobileFormEntryService.class);
		List<Economic> lstEconomic = new ArrayList<Economic>();

		//CREATE ECONOMIC OBJECTS
		lstEconomic.add(createEconomic(MobileFormEntryConstants.ECONOMIC_LAND_OWNED, xp.evaluate(MobileFormEntryConstants.ECONOMIC_LAND_OWNED, economicNode), mfes));
		lstEconomic.add(createEconomic(MobileFormEntryConstants.ECONOMIC_COWS_OWNED, xp.evaluate(MobileFormEntryConstants.ECONOMIC_COWS_OWNED, economicNode), mfes));
		lstEconomic.add(createEconomic(MobileFormEntryConstants.ECONOMIC_GOATS_OWNED, xp.evaluate(MobileFormEntryConstants.ECONOMIC_GOATS_OWNED, economicNode), mfes));
		lstEconomic.add(createEconomic(MobileFormEntryConstants.ECONOMIC_SHEEP_OWNED, xp.evaluate(MobileFormEntryConstants.ECONOMIC_SHEEP_OWNED, economicNode), mfes));
		lstEconomic.add(createEconomic(MobileFormEntryConstants.ECONOMIC_BEDNETS_OWNED, xp.evaluate(MobileFormEntryConstants.ECONOMIC_BEDNETS_OWNED, economicNode), mfes));
		lstEconomic.add(createEconomic(MobileFormEntryConstants.ECONOMIC_BEDNETS_GIVEN, xp.evaluate(MobileFormEntryConstants.ECONOMIC_BEDNETS_GIVEN, economicNode), mfes));
		lstEconomic.add(createEconomic(MobileFormEntryConstants.ECONOMIC_CHILDREN_IN_HOUSEHOLD, xp.evaluate(MobileFormEntryConstants.ECONOMIC_CHILDREN_IN_HOUSEHOLD, economicNode), mfes));
		lstEconomic.add(createEconomic(MobileFormEntryConstants.ECONOMIC_CHILDREN_IN_SCHOOL, xp.evaluate(MobileFormEntryConstants.ECONOMIC_CHILDREN_IN_SCHOOL, economicNode), mfes));
		lstEconomic.add(createEconomic(MobileFormEntryConstants.ECONOMIC_BEDNET_VOUCHER, xp.evaluate(MobileFormEntryConstants.ECONOMIC_BEDNET_VOUCHER, economicNode), mfes));

		return lstEconomic;
	}

	private static Economic createEconomic(String objectName, String objectValue, MobileFormEntryService mfes) {
		Economic economic;
		economic = new Economic();
		EconomicObject economicObject = mfes.getEconomicObjectByObjectName(objectName);
		economic.setValueQuestion(economicObject);
		if (economicObject != null) {
			if (economicObject.getObjectType().equalsIgnoreCase("numeric")) {
				economic.setValueNumeric(getDouble(objectValue));
			} else {
				economic.setValueText(objectValue);
			}
		}

		return economic;
	}

	public static Survey getSurvey(Document doc, XPath xp) throws XPathExpressionException {
		Survey survey = new Survey();
		Node surveyNode = (Node) xp.evaluate(MobileFormEntryConstants.SURVEY_PREFIX, doc, XPathConstants.NODE);
		if (surveyNode == null || !surveyNode.hasChildNodes()) {
			log.warn("empty or missing survey node; returning null survey.");
			return null;
		}

		//CREATE SURVEY
		survey.setSurveyIdentifier(xp.evaluate(MobileFormEntryConstants.SURVEY_SURVEY_ID, surveyNode));
		survey.setAllowedIn(xp.evaluate(MobileFormEntryConstants.SURVEY_ALLOWED_IN, surveyNode));
		survey.setReturnDate(getDate(xp.evaluate(MobileFormEntryConstants.SURVEY_RETURN_DATE, surveyNode)));
		survey.setProviderId(xp.evaluate(MobileFormEntryConstants.SURVEY_PROVIDER_ID, surveyNode));
		survey.setTeamId(xp.evaluate(MobileFormEntryConstants.SURVEY_TEAM_ID, surveyNode));
		//add meta data
		surveyNode = (Node) xp.evaluate(MobileFormEntryConstants.METADATA_PREFIX, doc, XPathConstants.NODE);
		survey.setStartTime(getTimestamp(xp.evaluate(MobileFormEntryConstants.META_START_TIME, surveyNode)));
		survey.setEndTime(getTimestamp(xp.evaluate(MobileFormEntryConstants.META_END_TIME, surveyNode)));
		survey.setDeviceId(xp.evaluate(MobileFormEntryConstants.META_DEVICE_ID, surveyNode));
		survey.setSubscriberId(xp.evaluate(MobileFormEntryConstants.META_SUBSCRIBER_ID, surveyNode));

		return survey;
	}

	private static Timestamp getTimestamp(String timestampString) {
		timestampString = timestampString.replace("T", " ");
		return Timestamp.valueOf(timestampString);
	}

	private static Integer getInteger(String integerString) {
		try {
			Integer integer = Integer.parseInt(integerString);
			return integer;
		} catch (NumberFormatException e) {
			log.debug("Error parsing string to Integer", e);
			return null;
		}
	}

	private static Double getDouble(String doubleString) {
		try {
			Double dbl = Double.parseDouble(doubleString != null && doubleString.trim() != "" ? doubleString : "0");
			return dbl;
		} catch (NumberFormatException e) {
			log.debug("Error parsing string to double", e);
			return null;
		}
	}

	private static Date getDate(String dateString) {
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			return date;
		} catch (ParseException e) {
			log.debug("Error parsing string to date", e);
			return null;
		}
	}

	private static String getGPS(String s) {
		// commented out because we want to retain the more precise values
		// from the mobile device; this code may be used elsewhere for exporting
		// GPS to a Garmin eTrex device.
		// String[] sa = s.split(" ");
		// return formatGps(Double.parseDouble(sa[0]),"lat") + " " + formatGps(Double.parseDouble(sa[1]),"lon");
		return s;
	}

	/**
	 * Format Gps from double to a readable string
	 *
	 * @param coordinates
	 * @param type
	 * @return
	 */
	private static String formatGps(double coordinates, String type) {
		String location = Double.toString(coordinates);
		String degreeSign = "\u00B0";

		//set degree section DD
		String degree = location.substring(0, location.indexOf(".")) + degreeSign;

		//set the minutes part MM
		location = "0." + location.substring(location.indexOf(".") + 1);
		double temp = Double.valueOf(location) * 60;
		location = Double.toString(temp);
		String mins = location.substring(0, location.indexOf(".")) + "'";

		//set the seconds part SS.s
		location = "0." + location.substring(location.indexOf(".") + 1);
		temp = Double.valueOf(location) * 60;
		String secs = roundToOneDecimalPlace(temp) + '"';

		if (type.equalsIgnoreCase("lon")) {
			if (degree.startsWith("-")) {
				degree = "W" + degree.replace("-", "") + mins + secs;
			} else {
				degree = "E" + degree.replace("-", "") + mins + secs;
			}
		} else {
			if (degree.startsWith("-")) {
				degree = "S" + degree.replace("-", "") + mins + secs;
			} else {
				degree = "N" + degree.replace("-", "") + mins + secs;
			}
		}
		return degree;
	}

	private static String roundToOneDecimalPlace(double dbl) {
		DecimalFormat decimalFormat = new DecimalFormat("#,###,###,##0.0");
		String formated = decimalFormat.format(dbl);
		return formated.substring(0, formated.indexOf(".")) + formated.substring(formated.indexOf(".") + 1);
	}

	/**
	 * Breaks a comma separated string to the individual portions
	 *
	 * @param item
	 */
	private static void setLocations(String item) {
		village = district = division = location = sublocation = "";
		//First get the district
		if (item.indexOf(",") != item.lastIndexOf(",") && item.indexOf(",") != -1) {
			district = item.substring(item.lastIndexOf(",") + 1);
			item = item.substring(0, item.lastIndexOf(","));
		}

		//then the division
		if (item.indexOf(",") != item.lastIndexOf(",") && item.indexOf(",") != -1) {
			division = item.substring(item.lastIndexOf(",") + 1);
			item = item.substring(0, item.lastIndexOf(","));
		}

		//then the location
		if (item.indexOf(",") != item.lastIndexOf(",") && item.indexOf(",") != -1) {
			location = item.substring(item.lastIndexOf(",") + 1);
			item = item.substring(0, item.lastIndexOf(","));
		}

		//then the sublocation
		if (item.indexOf(",") != item.lastIndexOf(",") && item.indexOf(",") != -1) {
			sublocation = item.substring(item.lastIndexOf(",") + 1);
			item = item.substring(0, item.lastIndexOf(","));
		} else {
			if (item.indexOf(",") != -1) {
				sublocation = item.substring(item.lastIndexOf(",") + 1);
				village = item.substring(0, item.lastIndexOf(","));
			}
		}
	}

	/**
	 * Authenticate in-line users
	 *
	 * @param auth
	 * @return <b>true</b> if authentication was successful otherwise
	 * <b>false</b>
	 */
	public static boolean authenticate(String auth) {
		boolean authenticated = false;
		if (Context.isAuthenticated()) {
			authenticated = true;
		}
		if (auth != null && !auth.isEmpty()) {
			if (auth.indexOf(":") != -1) {
				String[] credentials = auth.split(":");
				Context.authenticate(credentials[0], credentials[1]);
				authenticated = true;
			}
		}
		return authenticated;
	}

	/**
	 * Retrieves a patient identifier from a patient form
	 *
	 * @param doc
	 * @return patientIdentifier
	 */
	public static String getPatientIdentifier(Document doc) {
		NodeList elemList = doc.getDocumentElement().getElementsByTagName("patient");
		if (!(elemList != null && elemList.getLength() > 0)) {
			return null;
		}

		Element patientNode = (Element) elemList.item(0);

		NodeList children = patientNode.getChildNodes();
		int len = patientNode.getChildNodes().getLength();
		for (int index = 0; index < len; index++) {
			Node child = children.item(index);
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if ("patient_identifier".equalsIgnoreCase(((Element) child).getAttribute("openmrs_table"))
				&& "identifier".equalsIgnoreCase(((Element) child).getAttribute("openmrs_attribute"))) {
				return child.getTextContent();
			}
		}

		return null;
	}

	/**
	 * Given an age it returns a birth year
	 *
	 * @param doc
	 * @return
	 */
	public static Integer getBirthDateFromAge(Document doc) {
		String age = DOMUtil.getElementValue(doc, MobileFormEntryConstants.ESTIMATED_AGE);
		try {
			if (age == null || age.trim().length() == 0) {
				throw new Exception("Cannot get age");
			}

			Double dbl = Double.valueOf(age);
			int ageInMonths = dbl.intValue();

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());

			//convert age to days
			ageInMonths *= -30.4375;

			// add (actually subtract) number of days
			cal.add(Calendar.DATE, ageInMonths);
			return cal.get(Calendar.YEAR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns provider id given username
	 *
	 * @param userName
	 * @return
	 */
	public static Integer getProviderId(String userName) {
		User userProvider;
		Person personProvider;

		// assume its a normal user-name or systemId formatted with a dash
		userProvider = Context.getUserService().getUserByUsername(userName);
		if (userProvider != null) {
			return userProvider.getPerson().getPersonId();
		}

		// next assume it is a internal providerId (Note this is a person_id 
		// not a user_id) and try again
		try {
			personProvider = Context.getPersonService().getPerson(Integer.parseInt(userName));
			if (personProvider != null) {
				return personProvider.getPersonId();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}


		// now assume its a systemId without a dash: fix the dash and try again
		if (userName != null && userName.trim() != "") {
			if (userName.indexOf("-") == -1 && userName.length() > 1) {
				userName = userName.substring(0, userName.length() - 1) + "-" + userName.substring(userName.length() - 1);
				userProvider = Context.getUserService().getUserByUsername(userName);
				if (userProvider != null) {
					return userProvider.getPerson().getPersonId();
				}
			}
		}

		return null;
	}

 	/**
	 * Deletes a file specified by form path
	 */
	public static void deleteFile(String filePath) {
		try {
			if (filePath != null) {
				File fileToDelete = new File(filePath);

				//delete the file
				fileToDelete.delete();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}