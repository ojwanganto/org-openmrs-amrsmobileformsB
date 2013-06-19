package org.openmrs.module.amrsmobileforms;

import java.io.File;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.module.amrsmobileforms.util.XFormEditor;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

/**
 * Processes Mobile forms Drop Queue entries.
 * 
 * Splits composite forms from the drop queue
 * When the processing is successful, the queue entry is submitted to the Mobile form entry Queue while
 * the split forms are submitted to xformsEntry queue
 * For unsuccessful processing, the queue entry is put in the Mobile forms error folder.
 * 
 * @author Samuel Mbugua
 *
 */
@Transactional
public class MobileFormSplitProcessor {

	private static final Log log = LogFactory.getLog(MobileFormSplitProcessor.class);
	private static Boolean isRunning = false; // allow only one running
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private MobileFormEntryService mobileService;
	public MobileFormSplitProcessor(){
		try{
			docBuilder = docBuilderFactory.newDocumentBuilder();
			this.getMobileService();
		}
		catch(Exception e){
			log.error("Problem occurred while creating document builder", e);
		}
	}

	/**
	 * Process all existing queue entries in the mobile form queue
	 * @param queue 
	 */
	private boolean splitMobileForm(MobileFormQueue queue) throws APIException {
		String formData = queue.getFormData();
        String providerId=null;
        String locationId=null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(IOUtils.toInputStream(formData));
			log.debug("Splitting mobile xforms");
			XFormEditor.createIndividualFiles(doc);
		}
		catch (Throwable t) {
			log.error("Error splitting document", t);
			//Move form to error queue
			saveFormInError(queue.getFileSystemUrl());
			mobileService.saveErrorInDatabase(MobileFormEntryUtil.
					createError(getFormName(queue.getFileSystemUrl()), "Error splitting document", t.getMessage(),providerId,locationId));
			return false;
		}
		return true;
	}
	
	/**
	 * Split the next pending MobileForm drop entry. If there are no pending
	 * items in the drop_dir, this method simply returns quietly.
	 * 
	 * @return true if a queue entry was processed, false if queue was empty
	 */
	public void splitForms() {
		MobileFormEntryService mobileService;
		synchronized (isRunning) {
			if (isRunning) {
				log.warn("MobileForms splitting process aborting (another process already running)");
				return;
			}
			isRunning = true;
		}
		try {
			mobileService= (MobileFormEntryService)Context.getService(MobileFormEntryService.class);
		}catch (APIException e) {
			log.debug("MobileFormEntryService not found");
			return;
		}
		try {			
			File pendingSplitDir = MobileFormEntryUtil.getMobileFormsPendingSplitDir();
			for (File file : pendingSplitDir.listFiles()) {
				MobileFormQueue queue = mobileService.getMobileFormEntryQueue(file.getAbsolutePath());
				
				if (splitMobileForm(queue))
					//Move form to archive
					saveFormInArchive(queue.getFileSystemUrl());
			}
		}
		catch(Exception e){
			log.error("Problem occured while splitting", e);
		}
		finally {
			isRunning = false;
		}
	}
	

	/**
	 * Stores a form in a specified folder after processing.
	 */
	private void saveForm(String oldFormPath, String newFormPath){
		try{
			if(oldFormPath != null){
				File file=new File(oldFormPath);
				
				//move the file to specified new directory
				file.renameTo(new File(newFormPath));
			}
		}
		catch(Exception e){
			log.error(e.getMessage(),e);
		}

	}

	/**
	 * Archives a mobile form after successful processing
	 */
	private void saveFormInArchive(String formPath){
		String archiveFilePath= MobileFormEntryUtil.getMobileFormsArchiveDir(new Date()).getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, archiveFilePath);
	}

	
	private void saveFormInError(String formPath){
		String errorFilePath= MobileFormEntryUtil.getMobileFormsErrorDir().getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, errorFilePath);
	}
	
	/**
	 * Extracts form name from an absolute file path
	 * @param formPath
	 * @return
	 */
	private String getFormName(String formPath) {
		return formPath.substring(formPath.lastIndexOf(File.separatorChar)); 
	}
	
	/**
	 * @return MobileFormEntryService to be used by the process
	 */
	private MobileFormEntryService getMobileService() {
		if (mobileService == null) {
			try {
				mobileService= (MobileFormEntryService)Context.getService(MobileFormEntryService.class);
			}catch (APIException e) {
				log.debug("MobileFormEntryService not found");
				return null;
			}
		}
		return mobileService;
	}
	
}