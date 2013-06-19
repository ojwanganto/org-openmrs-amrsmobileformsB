package org.openmrs.module.amrsmobileforms.web.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Controller for Mobile resources jsp page
 * 
 * @author Samuel Mbugua
 */
@Controller
public class MobileResourcesController {
	
	public static final String VALID_CSV_FILE = "[ _\\-\\.A-Za-z0-9]*.csv";
	public static final String VALID_XML_FILE = "[ _\\-\\.A-Za-z0-9]*.x[ht]*ml";

	@ModelAttribute("mobileResources")
	@RequestMapping(value="/module/amrsmobileforms/mobileResources", method=RequestMethod.GET)
	public List<Object> populateForm() {
		MobileFormEntryService mfs = (MobileFormEntryService)Context.getService(MobileFormEntryService.class);
		List<Object> lst=new ArrayList<Object>();
		List<File> listOfFiles = mfs.getMobileResources();
		for (File file : listOfFiles) {
			FileResource fileResource=new FileResource();
			String fileName=file.getName();
			fileResource.setAbsoluteName(fileName);
			fileResource.setFileName(fileName.substring(0,fileName.lastIndexOf(".")));
			fileResource.setFileType(fileName.substring(fileName.lastIndexOf(".")+1) + " file");
			fileResource.setFileMeta(generateMeta(file.lastModified()));
			lst.add(fileResource);
		}
		return lst;
	}
	
	@RequestMapping(value="/module/amrsmobileforms/mobileResources", method=RequestMethod.POST, params = "action=Upload")
	public String saveObject(HttpSession httpSession,HttpServletRequest request, @RequestParam Object resourceFile){
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile("resourceFile");
		try {
			if (multipartFile != null && !multipartFile.isEmpty()) {
				String fileName = multipartFile.getOriginalFilename();
				if (fileName.matches(VALID_XML_FILE) || fileName.matches(VALID_CSV_FILE)) {
					String resourceDir = MobileFormEntryUtil.getMobileFormsResourcesDir().getAbsolutePath() + "/";
					File file = new File(resourceDir + fileName);
			        multipartFile.transferTo(file);
			        httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Successfully uploaded " + file.getName());
				}else
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "File type not allowed");
			}else
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "No file selected for upload or it is corrupted");
		} catch (IllegalStateException e) {
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Error uploading file");
			e.printStackTrace();
		} catch (IOException e) {
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Error uploading file");
			e.printStackTrace();
		}
		return "redirect:mobileResources.list";
	}
	
	@RequestMapping(value="/module/amrsmobileforms/mobileResources", method=RequestMethod.POST, params = "action=Delete Selected Resource(s)")
	public String deleteObject(HttpSession httpSession, @RequestParam("absoluteName") List<String> listNames){
		if (listNames.size() > 0) {
			for (String name : listNames) {
				String resourceDir=MobileFormEntryUtil.getMobileFormsResourcesDir().getAbsolutePath() + "/";
				String filePath = resourceDir + name;
				File file=new File(filePath);
				if (file.exists() && file.isFile()){
					file.delete();
				}
			}
		}
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Resources successfully deleted");
		return "redirect:mobileResources.list";
	}
	
	public class FileResource {
		private String absoluteName;
		private String fileName;
		private String fileType;
		private String fileMeta;

		public String getAbsoluteName() {return absoluteName;}
		public void setAbsoluteName(String absoluteName) {this.absoluteName = absoluteName;}
		public String getFileName() {return fileName; }
		public void setFileName(String fileName) {this.fileName = fileName;}
		public String getFileType() {return fileType;}
		public void setFileType(String fileType) {this.fileType = fileType;}
		public String getFileMeta() {return fileMeta;}
		public void setFileMeta(String fileMeta) {this.fileMeta = fileMeta;}
	}
	
	private String generateMeta(long timestamp) {
		Date d = new Date(timestamp);
		return new SimpleDateFormat("EEE, MMM dd, yyyy 'at' HH:mm").format(d);
	}
}	