package org.openmrs.module.amrsmobileforms.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.amrsmobileforms.MobileFormEntryConstants;
import org.openmrs.module.amrsmobileforms.SyncLogModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Process for logging all synchronizations from mobile devices
 * 
 * @author Samuel Mbugua
 *
 */
public class SyncLogger {
	private static final Log log = LogFactory.getLog(SyncLogger.class);
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private static DocumentBuilder docBuilder;
	

	private File getLogFile() {
		File logDir=MobileFormEntryUtil.getMobileFormsSyncLogDir();
		String logFileName=logDir.getAbsolutePath() + File.separator + "log-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log";
		File logFile = new File(logFileName);
		if (!logFile.exists())
			try {
				log.debug("Creating new Sync log file");
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return logFile;
	}
	
	private void writer(String filePath, String line) {
		try
		{
		    FileWriter fileWriter = new FileWriter(filePath,true);
		    fileWriter.write(line);
		    fileWriter.close();
		}
		catch(IOException e){e.printStackTrace();} 
	}
	
	public void createSyncLog(File syncedFile) throws Exception {
		docBuilder = docBuilderFactory.newDocumentBuilder();
		SyncLogModel syncLog =new SyncLogModel();
		XPathFactory xpf=XPathFactory.newInstance();
		XPath xp=xpf.newXPath();
		try {
			Document doc = docBuilder.parse(syncedFile);
			syncLog.setSyncDate(new Date(syncedFile.lastModified()));
			syncLog.setFileName(syncedFile.getName());
			syncLog.setFileSize(Math.round(Double.valueOf(syncedFile.length())/1024) + "KB");
	
			Node curNode=(Node) xp.evaluate(MobileFormEntryConstants.SURVEY_PREFIX, doc, XPathConstants.NODE);
			if (curNode != null) {
				syncLog.setProviderId(xp.evaluate(MobileFormEntryConstants.SURVEY_PROVIDER_ID, curNode)); 
			}
			
			curNode=(Node) xp.evaluate(MobileFormEntryConstants.METADATA_PREFIX, doc, XPathConstants.NODE);
			if (curNode != null) {
				syncLog.setDeviceId(xp.evaluate(MobileFormEntryConstants.META_DEVICE_ID, curNode)); 
			}
			
			curNode=(Node) xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_PREFIX + MobileFormEntryConstants.HOUSEHOLD_META_PREFIX, doc, XPathConstants.NODE);
			if (curNode != null) {
				syncLog.setHouseholdId(xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_HOUSEHOLD_ID, curNode)); 
			}
			
			File syncLogFile = getLogFile();
			writer(syncLogFile.getAbsolutePath(),syncLog.getOutputLine());
		}
		catch (Throwable t) {
			log.error("Cannot create log for file, probably an empty file");
		}
	}
}