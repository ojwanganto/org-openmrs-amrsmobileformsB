package org.openmrs.module.amrsmobileforms;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * This class is only used as the model for the ResolveErrosFormController and its jsp page
 *
 * @author Samuel Mbugua
 */
public class MobileFormEntryErrorModel extends MobileFormEntryError {

	protected final Log log = LogFactory.getLog(getClass());
	private DocumentBuilderFactory documentBuilderFactory;

	// data from the formData
	private String name = "";
	private String birthdate = "";
	private String gender = "";
	private String identifier = "";

	// data from the formData encounter section
    //data from household meta-data section
    private String totalHousehold = "";
    private String totalEligible = "";

    // data from the formData encounter section
    //encounter.provider_id
    private String providerId = "";
	private String location = "";
	private String encounterDate = "";
	private String formModelName = "";
	private String formId = "";
	private String formPath = "";

	private String errorType = "";

	/**
	 * Creates a model object from the given MobileFormEntryError. Parses data out of the &lt;patient> section to fill in
	 * the name/birthdate/etc fields
	 *
	 * @param error MobileFormEntryError to duplicate
	 */
	public MobileFormEntryErrorModel(MobileFormEntryError error, String errorType) {

		setErrorType(errorType);

		setMobileFormEntryErrorId(error.getMobileFormEntryErrorId());
		setFormName(error.getFormName());
		setError(error.getError());
		setErrorDetails(error.getErrorDetails());
		setDateCreated(error.getDateCreated());

		//For resolve form
		setComment(error.getComment());
		setCommentedBy(error.getCommentedBy());
		setDateCommented(error.getDateCommented());

		if (getFormName() != null && getFormName().length() > 0) {
			try {

				Document formDataDoc = getDocumentForErrorQueueItem(getFormName());

				XPath xp = getXPathFactory().newXPath();

               if ("household".equals(errorType)) {
					setName("Household");
					setBirthdate("N/A");
					setIdentifier(xp.evaluate("/form/household/meta_data/household_id", formDataDoc));
					setGender("N/A");
					setLocation(xp.evaluate("/form/household/meta_data/catchment_area", formDataDoc));
					setEncounterDate(xp.evaluate("/form/meta/start_time", formDataDoc));
                    setTotalHousehold(xp.evaluate("/form/household/meta_data/total_household", formDataDoc));
                    setTotalEligible(xp.evaluate("/form/household/meta_data/total_eligible", formDataDoc));
                    setProviderId(xp.evaluate("/form/encounter/encounter.provider_id",formDataDoc));


               } else {
					setName(xp.evaluate("/form/patient/patient.given_name", formDataDoc) + " " +
							xp.evaluate("/form/patient/patient.middle_name", formDataDoc) + " " +
							xp.evaluate("/form/patient/patient.family_name", formDataDoc));

					setBirthdate(xp.evaluate("/form/patient/patient.birthdate", formDataDoc));
					setIdentifier(xp.evaluate("/form/patient/patient.medical_record_number", formDataDoc));
					setGender(xp.evaluate("/form/patient/patient.sex", formDataDoc));

					// parse the encounter info from the form data
					String location = xp.evaluate("/form/encounter/encounter.location_id", formDataDoc);
					setLocation(location.substring(location.indexOf("^") + 1));
					setEncounterDate(xp.evaluate("/form/encounter/encounter.encounter_datetime", formDataDoc));
                    setTotalHousehold("N/A");
                    setTotalEligible("N/A");

                    setProviderId(xp.evaluate("/form/encounter/encounter.provider_id",formDataDoc));

               }
				setFormModelName(xp.evaluate("/form/@name", formDataDoc));
				setFormId(xp.evaluate("/form/@version", formDataDoc));

			} catch (Exception e) {
				log.warn("Unable to get xml document from formData for formentryerror with id: " + error.getMobileFormEntryErrorId(), e);
                e.printStackTrace();
			}
		}
	}

	/**
	 * @return the birthdate
	 */
	public String getBirthdate() {
		return birthdate;
	}

	/**
	 * @param birthdate the birthdate to set
	 */
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the encounterDate
	 */
	public String getEncounterDate() {
		return encounterDate;
	}

	/**
	 * @param encounterDate the encounterDate to set
	 */
	public void setEncounterDate(String encounterDate) {
		this.encounterDate = encounterDate;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the formModelName
	 */
	public String getFormModelName() {
		return formModelName;
	}
	/**
	 * @param formModelName the formModelName to set
	 */
	public void setFormModelName(String formModelName) {
		this.formModelName = formModelName;
	}
	/**
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getFormPath() {
		return formPath;
	}

	public void setFormPath(String formPath) {
		this.formPath = formPath;
	}

    /**
     * @return the totalHousehold
     */
    public String getTotalHousehold() {
        return totalHousehold;
    }

    /**
     * @param totalHousehold the totalHousehold to set
     */
    public void setTotalHousehold(String totalHousehold) {
        this.totalHousehold = totalHousehold;
    }


    /**
     * @return the totalEligible
     */
    public String getTotalEligible() {
        return totalEligible;
    }

    /**
     * @param totalEligible the totalEligible to set
     */
    public void setTotalEligible(String totalEligible) {
        this.totalEligible = totalEligible;
    }

    /**
     * @return the providerId
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * @param providerId the providerId to set
     */
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }


    /**
	 * @return the errorType
	 */
	public String getErrorType() {
		return errorType;
	}

	/**
	 * @param errorType the errorType to set
	 */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	/**
	 * Fetch the xml doc for this error
	 *
	 * @param formData
	 * @throws Exception
	 */
	public Document getDocumentForErrorQueueItem(String formData) throws Exception {
		DocumentBuilderFactory dbf = getDocumentBuilderFactory();
		DocumentBuilder db = dbf.newDocumentBuilder();
        /* check for special characters using this
        replaceAll("[^\\x20-\\x7e]", "");*/
		Document doc = db.parse(IOUtils.toInputStream(formData.replaceAll("[^\\x20-\\x7e]", "")));
		return doc;
	}

	private DocumentBuilderFactory getDocumentBuilderFactory() {
		if (documentBuilderFactory == null)
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
		return documentBuilderFactory;
	}

	private XPathFactory xPathFactory;

	/**
	 * @return XPathFactory to be used for obtaining data from the parsed XML
	 */
	private XPathFactory getXPathFactory() {
		if (xPathFactory == null)
			xPathFactory = XPathFactory.newInstance();
		return xPathFactory;
	}

}
