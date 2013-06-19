package org.openmrs.module.amrsmobileforms;

import java.util.Date;

/**
 * @author Samuel Mbugua
 *
 */
public class SyncLogModel {
	private int syncId;
	private Date syncDate;
	private String providerId;
	private String deviceId;
	private String householdId;
	private String fileName;
	private String fileSize;
	
	
	/**
	 * @return the syncId
	 */
	public int getSyncId() {
		return syncId;
	}
	/**
	 * @param syncId the syncId to set
	 */
	public void setSyncId(int syncId) {
		this.syncId = syncId;
	}
	/**
	 * @return the syncDate
	 */
	public Date getSyncDate() {
		return syncDate;
	}
	/**
	 * @param syncDate the syncDate to set
	 */
	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
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
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return the householdId
	 */
	public String getHouseholdId() {
		return householdId;
	}
	/**
	 * @param householdId the householdId to set
	 */
	public void setHouseholdId(String householdId) {
		this.householdId = householdId;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the fileSize
	 */
	public String getFileSize() {
		return fileSize;
	}
	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	
	/**
	 * Returns a CSV type text line of all properties in the order 
	 * <b>syncId,syncDate,providerId,deviceId,householdId,fileName,fileSize</b>
	 */
	public String getOutputLine() {
		return syncId + "," + syncDate  + "," + providerId  + "," + deviceId 
		 + "," + householdId  + "," + fileName  + "," + fileSize  + "\n";
	}
}