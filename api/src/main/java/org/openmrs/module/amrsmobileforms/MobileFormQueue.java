package org.openmrs.module.amrsmobileforms;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.util.OpenmrsUtil;

/**
 * Object holding the metadata around the mobile form queue
 * 
 * @author Samuel Mbugua
 *
 */
public class MobileFormQueue {
	
	private Log log = LogFactory.getLog(this.getClass());
	private Integer mobileFormQueueId;
	private String formData;
	private User creator;
	private Date dateCreated;
	
	private String fileSystemUrl;
	
	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
    	if (!(obj instanceof MobileFormQueue))
    		return false;
    	
    	MobileFormQueue other = (MobileFormQueue)obj;
    	
    	if (other.getMobileFormQueueId() != null && getMobileFormQueueId() != null)
    		return other.getMobileFormQueueId().equals(mobileFormQueueId);
    	
    	if (other.getFormData() != null && getFormData() != null)
    		return other.getFormData().equals(formData);
    	
    	if (other.getFileSystemUrl() != null && getFileSystemUrl() != null)
    		return other.getFileSystemUrl().equals(fileSystemUrl);
    	
    	return false;
    }
    
	/**
	 * Gets the xml that this queue item holds.  If formData is null
	 * and fileSystemUrl is not null, the data is "lazy loaded" from
	 * the filesystem
	 * 
	 * @return Returns the formData.
	 */
	public String getFormData() {
		
		if (formData == null && fileSystemUrl != null) {
			// lazy load the form data from the filesystem
			
			File file = new File(fileSystemUrl);
			
			if (file.exists()) {
				try {
					formData = OpenmrsUtil.getFileAsString(file);
					return formData;
				}
				catch (IOException io) {
					log.warn("Unable to lazy load the formData from: " + fileSystemUrl, io);
				}
			}
			else {
				log.warn("File system url does not exist for formentry queue item.  Url: '" + fileSystemUrl + "'");
			}
				
		}
		
		return formData;
	}

	/**
	 * @param formData
	 *            The formData to set.
	 */
	public void setFormData(String formData) {
		this.formData = formData;
	}

	/**
	 * @return Returns the creator.
	 */
	public User getCreator() {
		return creator;
	}

	/**
	 * @param creator
	 *            The creator to set.
	 */
	public void setCreator(User creator) {
		this.creator = creator;
	}

	/**
	 * @return Returns the dateCreated.
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            The dateCreated to set.
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * Used when the queue items are stored on the server filesystem.
	 * 
     * @return the fileSystemUrl
     */
    public String getFileSystemUrl() {
    	return fileSystemUrl;
    }

	/**
	 * Used when the queue items are stored on the server filesystem
     * @param fileSystemUrl the fileSystemUrl to set
     */
    public void setFileSystemUrl(String fileSystemUrl) {
    	this.fileSystemUrl = fileSystemUrl;
    }
    
	/**
	 * @return Returns the MobileFormQueueId.
	 */
	public Integer getMobileFormQueueId() {
		return mobileFormQueueId;
	}

	/**
	 * @param MobileFormQueueId The MobileFormQueueId to set.
	 */
	public void setMobileFormQueueId(Integer mobileFormQueueId) {
		this.mobileFormQueueId = mobileFormQueueId;
	}
}
