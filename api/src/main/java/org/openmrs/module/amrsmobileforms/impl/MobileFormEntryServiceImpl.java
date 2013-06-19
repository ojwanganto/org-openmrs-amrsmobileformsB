package org.openmrs.module.amrsmobileforms.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.amrsmobileforms.Economic;
import org.openmrs.module.amrsmobileforms.EconomicConceptMap;
import org.openmrs.module.amrsmobileforms.EconomicObject;
import org.openmrs.module.amrsmobileforms.HouseholdMember;
import org.openmrs.module.amrsmobileforms.MobileFormEntryError;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.openmrs.module.amrsmobileforms.MobileFormHousehold;
import org.openmrs.module.amrsmobileforms.MobileFormQueue;
import org.openmrs.module.amrsmobileforms.Survey;
import org.openmrs.module.amrsmobileforms.SyncLogModel;
import org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;

/**
 * @author Samuel Mbugua
 *
 */
public class MobileFormEntryServiceImpl implements MobileFormEntryService {
	private static Log log = LogFactory.getLog(MobileFormEntryServiceImpl.class);
	
	private MobileFormEntryDAO dao;
	
	public MobileFormEntryServiceImpl() {
	}
	
	@SuppressWarnings("unused")
	private MobileFormEntryDAO getMobileFormEntryDAO() {
		return dao;
	}
	
	public void setMobileFormEntryDAO(MobileFormEntryDAO dao) {
		this.dao = dao;
	}
	

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getMobileFormEntryQueue(java.lang.String)
	 */
	public MobileFormQueue getMobileFormEntryQueue(String absoluteFilePath) {
		MobileFormQueue queueItem = new MobileFormQueue();
		queueItem.setFileSystemUrl(absoluteFilePath);
		log.debug(absoluteFilePath);
		return queueItem;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getSystemVariables()
	 */
	public SortedMap<String,String> getSystemVariables() {
		TreeMap<String, String> systemVariables = new TreeMap<String, String>();
		systemVariables.put("MOBILE_FORMS_RESOURCES_DIR", MobileFormEntryUtil.getMobileFormsResourcesDir().getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_DROP_DIR", MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_QUEUE_DIR", MobileFormEntryUtil.getMobileFormsQueueDir().getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_ARCHIVE_DIR", MobileFormEntryUtil.getMobileFormsArchiveDir(null).getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_ERROR_DIR", MobileFormEntryUtil.getMobileFormsErrorDir().getAbsolutePath());
		return systemVariables;
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getMobileResources()
	 */
	public List<File> getMobileResources() {
		File resourcesDir=MobileFormEntryUtil.getMobileFormsResourcesDir();
		List<File> lst=new ArrayList<File>();
		for(File file:resourcesDir.listFiles()) {
			lst.add(file);
		}
		return lst;
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getHousehold(java.lang.String)
	 */
	public MobileFormHousehold getHousehold(String householdIdentifier) {
		return dao.getHousehold(householdIdentifier);
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#createHouseholdInDatabase(org.openmrs.module.amrsmobileforms.MobileFormHousehold)
	 */
	public void saveHousehold(MobileFormHousehold household) {
		dao.saveHousehold(household);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getEconomicObjectByObjectName(java.lang.String)
	 */
	public EconomicObject getEconomicObjectByObjectName(String objectName) {
		return dao.getEconomicObjectByObjectName(objectName);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#createEconomicInDatabase(org.openmrs.module.amrsmobileforms.Economic)
	 */
	public void createEconomicInDatabase(Economic economic) {
		dao.createEconomicInDatabase(economic);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#createSurvey(org.openmrs.module.amrsmobileforms.Survey)
	 */
	public void createSurvey(Survey survey) {
		dao.createSurvey(survey);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getAllEconomicObjects()
	 */
	public List<EconomicObject> getAllEconomicObjects() {
		return dao.getAllEconomicObjects();
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#saveEconomicObject(org.openmrs.module.amrsmobileforms.EconomicObject)
	 */
	public void saveEconomicObject(EconomicObject economicObject) {
		dao.saveEconomicObject(economicObject);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#deleteEconomicObject(org.openmrs.module.amrsmobileforms.EconomicObject)
	 */
	public boolean deleteEconomicObject(EconomicObject economicObject) {
		return dao.deleteEconomicObject(economicObject);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#saveErrorInDatabase(org.openmrs.module.amrsmobileforms.MobileFormEntryError)
	 */
	public void saveErrorInDatabase(MobileFormEntryError mobileFormEntryError) {
		dao.saveErrorInDatabase(mobileFormEntryError);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getAllMobileFormEntryErrors()
	 */
	public List<MobileFormEntryError> getAllMobileFormEntryErrors() {
		return dao.getAllMobileFormEntryErrors();
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getErrorById(java.lang.Integer)
	 */
	public MobileFormEntryError getErrorById(Integer errorId) {
		return dao.getErrorById(errorId);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getHouseholdMemberById(java.lang.Integer)
	 */
	public HouseholdMember getHouseholdMemberById(Integer identifier) {
		return dao.getHouseholdMemberById(identifier);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getEconomicObjectById(java.lang.Integer)
	 */
	public EconomicObject getEconomicObjectById(Integer economicObjectId) {
		return dao.getEconomicObjectById(economicObjectId);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#deleteError(org.openmrs.module.amrsmobileforms.MobileFormEntryError)
	 */
	public void deleteError(MobileFormEntryError error) {
		dao.deleteError(error);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#saveHouseholdMember(org.openmrs.module.amrsmobileforms.HouseholdMember)
	 */
	public void saveHouseholdMember(HouseholdMember householdMember) {
		dao.saveHouseholdMember(householdMember);
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getAllMembersInHousehold(java.lang.Integer)
	 */
	public List<HouseholdMember> getAllMembersInHousehold(MobileFormHousehold household) {
		return dao.getAllMembersInHousehold(household);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getSyncLog(java.util.Date)
	 */
	public List<SyncLogModel> getSyncLog(Date logDate) {
		List<SyncLogModel> logList = new ArrayList<SyncLogModel>();
		File logDir=MobileFormEntryUtil.getMobileFormsSyncLogDir();
		if (logDate == null)
			logDate=new Date();
		String logFileName = logDir.getAbsolutePath() + File.separator + "log-" + new SimpleDateFormat("yyyy-MM-dd").format(logDate) + ".log";
		File logFile = new File(logFileName);
		if (!logFile.exists())
			return null;			
		String line = null;
		try {
			BufferedReader input =  new BufferedReader(new FileReader(logFile));
			try {
				while (( line = input.readLine()) != null){
					if (line.indexOf(",")!=-1) {
						SyncLogModel logModel = getLogModel(line);
						if (logModel != null)
							logList.add(logModel);
					}
				}
			}
			finally {
				 input.close();
			 }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
		return logList;
	}
	
	/**
	 * Takes a Comma Separated line and creates an object of type {@link SyncLogModel}
	 */
	private static SyncLogModel getLogModel(String line) {
		SyncLogModel syncLogModel = new SyncLogModel();
		// syncId
		if (line.indexOf(",") != -1) {
			syncLogModel.setSyncId(Integer.parseInt(line.substring(0,line.indexOf(","))));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return null;
		// syncDate
		if (line.indexOf(",") != -1) {
			try {
				DateFormat df =new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
				syncLogModel.setSyncDate(df.parse(line.substring(0,line.indexOf(","))));
				line=line.substring(line.indexOf(",") + 1);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}else
			return null;
		
		// providerId
		if (line.indexOf(",") != -1) {
			syncLogModel.setProviderId(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return null;
		
		// deviceId
		if (line.indexOf(",") != -1) {
			syncLogModel.setDeviceId(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return null;
		
		// 	householdId;
		if (line.indexOf(",") != -1) {
			syncLogModel.setHouseholdId(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return syncLogModel;
		
		// fileName;
		if (line.indexOf(",") != -1) {
			syncLogModel.setFileName(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return syncLogModel;
		
		// fileSize;
		if (line.indexOf(",") != -1) {
			syncLogModel.setFileSize(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			syncLogModel.setFileSize(line);
		
		return syncLogModel;
	
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getAllSyncLogs()
	 */
	public List<String> getAllSyncLogs() {
		List<String> logFiles = new ArrayList<String>();
		File logDir=MobileFormEntryUtil.getMobileFormsSyncLogDir();
		DateFormat df =new SimpleDateFormat("yyyy-MM-dd");
		DateFormat df1 =new SimpleDateFormat("yyyy-MMM-dd");
		for (File file : logDir.listFiles()) {
			String fileName=file.getName();
			if (fileName.indexOf("-") != -1 && fileName.indexOf(".") != -1) {
				try {
					fileName=fileName.substring(fileName.indexOf("-")+1,fileName.lastIndexOf("."));
					Date date=df.parse(fileName);
					logFiles.add(df1.format(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return logFiles;
	}

	public EconomicConceptMap getEconomicConceptMapFor(EconomicObject eo) {
		return dao.getEconomicConceptMapFor(eo);
	}

	public EconomicConceptMap getEconomicConceptMap(Integer id) {
		return dao.getEconomicConceptMap(id);
	}

	public EconomicConceptMap saveEconomicConceptMap(EconomicConceptMap ecm) {
		return dao.saveEconomicConceptMap(ecm);
	}

	public List<MobileFormEntryError> getErrorBatch(Integer start, Integer length, String query) {
		return dao.getErrorBatch(start, length, query);
	}

	public Number countErrors(String query) {
		return dao.countErrors(query);
	}
}