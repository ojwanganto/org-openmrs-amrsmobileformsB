package org.openmrs.module.amrsmobileforms.util;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.openmrs.module.xforms.download.XformDataUploadManager;
import org.openmrs.module.xforms.util.XformsUtil;
import org.openmrs.util.OpenmrsUtil;

/**
 * Uploads patient forms from this module to the xforms module
 * 
 * @author Samuel Mbugua
 */
public class MobileFormEntryFileUploader {
	
	public static void submitXFormFile(String filePath) throws IOException, Exception {
		File file = new File(filePath);
		XformDataUploadManager.processXform(OpenmrsUtil.getFileAsString(file), getRandomSessionString(), XformsUtil.getEnterer(),true,null);
	}
	
	/**
	 * Generate a random alphanumeric session string 
	 * @return a random string
	 */
	private static String getRandomSessionString() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
}