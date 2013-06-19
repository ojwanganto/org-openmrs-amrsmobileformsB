package org.openmrs.module.amrsmobileforms;

/**
 * Module wide constants are kept here
 *
 * @author Samuel Mbugua
 *
 */
public class MobileFormEntryConstants {

    /**
     * Name of the global property for the directory where mobile xforms and other special files are stored.
     */
    public final static String GP_MOBILE_FORMS_RESOURCES_DIR = "amrsmobileforms.resources_dir";

    /** The default mobile-forms-resources  directory. */
    public final static String GP_MOBILE_FORMS_RESOURCES_DIR_DEFAULT = "amrsmobileforms/resources_dir";

    /** Mobile-forms-sync-log directory. */
    public final static String GP_MOBILE_FORMS_SYNC_LOG_DIR_DEFAULT = "amrsmobileforms/synclog";

    /**
     * Name of the global property for the directory where mobile devices sends forms.
     */
    public final static String GP_MOBILE_FORMS_DROP_DIR = "amrsmobileforms.drop_dir";

    /** The default mobile-forms-drop  directory. */
    public final static String GP_MOBILE_FORMS_DROP_DIR_DEFAULT = "amrsmobileforms/drop_dir";

    /**
     * Name of the global property for the directory for queuing mobile forms
     * before they are processed.
     */
    public final static String GP_MOBILE_FORMS_QUEUE_DIR = "amrsmobileforms.queue_dir";

    /** The default mobile-forms queue directory. */
    public final static String GP_MOBILE_FORMS_QUEUE_DIR_DEFAULT = "amrsmobileforms/queue";

    /**
     * Name of the global property for the directory where to put forms that
     * erred during processing
     */
    public final static String GP_MOBILE_FORMS_ERROR_DIR = "amrsmobileforms.error_dir";

    /** The default mobile-forms error directory. */
    public final static String GP_MOBILE_FORMS_ERROR_DIR_DEFAULT = "amrsmobileforms/error";

    /*
      * I do not believe this is the best way since the xform module will archive
      * the files as well as the formentry module, this could be a table entry
      * with just form names that can later be retrieved from the xform archive
      * directory
      */
    public final static String GP_MOBILE_FORMS_ARCHIVE_DIR = "amrsmobileforms.archive_dir";

    /** The default mobile forms archive directory. */
    public final static String GP_MOBILE_FORMS_ARCHIVE_DIR_DEFAULT = "amrsmobileforms/archive/%Y/%M";

    /** Mobile-forms-pending link directory. */
    public final static String GP_MOBILE_FORMS_PENDING_LINK_DIR = "amrsmobileforms/pending_link";

    /** Mobile-forms-pending split directory. */
    public final static String GP_MOBILE_FORMS_PENDING_SPLIT_DIR = "amrsmobileforms/pending_split";

    /** Mobile-forms post processing directory. */
    public final static String GP_MOBILE_FORMS_POST_PROCESS_DIR = "amrsmobileforms/post_process";

    // Concept global properties
    public static final String GP_CONCEPT_ADULTS = "amrsmobileforms.concept.adults";
    public static final String GP_CONCEPT_CHILDREN = "amrsmobileforms.concept.children";
    public static final String GP_CONCEPT_ELIGIBLE_ADULTS = "amrsmobileforms.concept.eligibleAdults";
    public static final String GP_CONCEPT_ELIGIBLE_CHILDREN = "amrsmobileforms.concept.eligibleChildren";
    public static final String GP_CONCEPT_RETURN_VISIT_DATE = "amrsmobileforms.concept.returnVisitDate";

    // Household-related default global properties
    public static final String GP_DEFAULT_HOUSEHOLD_DEFINITION = "amrsmobileforms.defaultHouseholdDefinition";
    public static final String GP_DEFAULT_HOUSEHOLD_ENCOUNTER_TYPE = "amrsmobileforms.defaultHouseholdEncounterType";

    // Post-processor defaults for creating identifiers and attributes
    public static final String GP_HCT_IDENTIFIER_TYPE = "amrsmobileforms.hctIdentifierType";
    public static final String GP_PHONENUMBER_ATTRIBUTE_TYPE = "amrsmobileforms.phonenumberAttributeType";

    /** The text/xml http content type. */
    public final static String HTTP_HEADER_CONTENT_TYPE_XML = "text/xml; charset=utf-8";

    /** The metadata nodes prefix. */
    public static final String METADATA_PREFIX = "/form/meta";

    /** The survey nodes prefix. */
    public final static String SURVEY_PREFIX = "/form/survey";

    /** The household nodes prefix. */
    public final static String HOUSEHOLD_PREFIX = "/form/household";

    /** The economics nodes prefix. */
    public final static String HOUSEHOLD_ECONOMIC_PREFIX = "/economics";

    /** The household metadata nodes prefix. */
    public final static String HOUSEHOLD_META_PREFIX = "/meta_data";

    /** The individuals nodes prefix. */
    public final static String HOUSEHOLD_INDIVIDUALS_PREFIX = "/individuals";

    public final static String META_START_TIME = "start_time";
    public final static String META_END_TIME = "end_time";
    public final static String META_DEVICE_ID = "device_id";
    public final static String META_SUBSCRIBER_ID= "subscriber_id";


    public final static String SURVEY_PROVIDER_ID = "provider_id";
    public final static String SURVEY_TEAM_ID = "team_id";
    public final static String SURVEY_SURVEY_ID= "survey_id";
    public final static String SURVEY_ALLOWED_IN = "allowed_in";
    public final static String SURVEY_RETURN_DATE = "return_date";

    public final static String HOUSEHOLD_META_HOUSEHOLD_ID = "household_id";
    public final static String HOUSEHOLD_META_GPS_LOCATION = "gps_location";
    public final static String HOUSEHOLD_META_VILLAGE = "village";
    public final static String HOUSEHOLD_META_LOCATION = "location";
    public final static String HOUSEHOLD_META_SUBLOCATION = "sublocation";
    public final static String HOUSEHOLD_META_DISTRICT = "district";
    public final static String HOUSEHOLD_META_DIVISION = "division";
    public final static String HOUSEHOLD_META_HOUSEHOLD_ADULTS = "household_adults";
    public final static String HOUSEHOLD_META_CHILDREN_UNDER13 = "children_under13";
    public final static String HOUSEHOLD_META_HOUSEHOLD_ADULTS_ELIGIBLE = "eligible_adults";
    public final static String HOUSEHOLD_META_CHILDREN_UNDER13_ELIGIBLE = "eligible_under13";

    public final static String ECONOMIC_BEDNETS_OWNED = "bednets_owned";
    public final static String ECONOMIC_BEDNETS_GIVEN = "bednets_given";
    public final static String ECONOMIC_BEDNET_VOUCHER = "bednet_voucher";
    public final static String ECONOMIC_LAND_OWNED = "land_owned";
    public final static String ECONOMIC_COWS_OWNED = "cows_owned";
    public final static String ECONOMIC_GOATS_OWNED = "goats_owned";
    public final static String ECONOMIC_SHEEP_OWNED = "sheep_owned";
    public final static String ECONOMIC_CHILDREN_IN_HOUSEHOLD = "children_in_household";
    public final static String ECONOMIC_CHILDREN_IN_SCHOOL = "children_in_school";

    public final static String PATIENT_NODE = "/form/patient";
    public final static String PATIENT_ID = "patient.patient_id";
    public final static String PATIENT_IDENTIFIER = "patient.medical_record_number";
    public final static String PATIENT_IDENTIFIER_TYPE = "patient_identifier.identifier_type_id";
    public final static String PATIENT_HCT_IDENTIFIER = "patient.hct_id";
    public final static String PATIENT_FAMILYNAME = "patient.family_name";
    public final static String PATIENT_GIVENNAME = "patient.given_name";
    public final static String PATIENT_MIDDLENAME = "patient.middle_name";
    public final static String PATIENT_HOUSEHOLD_IDENTIFIER = "household";
    public final static String ESTIMATED_AGE = "approximate_age";
    public final static String PATIENT_BIRTHDATE = "patient.birthdate";
    public final static String PATIENT_PHONE = "patient.contact_phone_number";
    public final static String PATIENT_CATCHMENT_AREA = "catchment_area";

    public final static String ENCOUNTER_NODE = "/form/encounter";
    public final static String ENCOUNTER_PROVIDER = "encounter.provider_id";
    public final static String ENCOUNTER_LOCATION= "encounter.location_id";

    //TEMPORAL
    public final static String OBS_NODE = "/form/obs";
    public final static String OBS_RELATIONSHIP = "relation_to_househead";

    //PRIVILEGES
    public static final String PRIV_RESOLVE_MOBILE_FORM_ENTRY_ERROR = "Resolve Mobile Form Entry Error";
    public static final String PRIV_COMMENT_ON_MOBILE_FORM_ENTRY_ERRORS = "Comment on Mobile Form Entry Errors";
    public static final String PRIV_VIEW_MOBILE_FORM_ERROR = "View Mobile Form Error";
    public static final String PRIV_VIEW_MOBILE_FORM_PROPERTY = "View Mobile Form Entry Properties";
    public static final String PRIV_MANAGE_ECONOMIC_OBJECT = "Manage Economic Objects";
}
