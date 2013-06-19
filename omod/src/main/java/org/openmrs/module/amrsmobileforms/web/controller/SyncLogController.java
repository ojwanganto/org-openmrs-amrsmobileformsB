/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.amrsmobileforms.web.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the synclog jsp page
 * 
 * @author Samuel Mbugua
 */
@Controller
public class SyncLogController {
	private static Date syncLogDate;
	private DateFormat df =new SimpleDateFormat("yyyy-MMM-dd");
	@RequestMapping(value = "/module/amrsmobileforms/syncLog", method = RequestMethod.GET)
	public String showPage(ModelMap model) throws Exception {
		MobileFormEntryService mfs = (MobileFormEntryService)Context.getService(MobileFormEntryService.class);
		model.addAttribute("logs", mfs.getSyncLog(syncLogDate));
		model.addAttribute("files", mfs.getAllSyncLogs());
		model.addAttribute("logDate", syncLogDate == null?df.format(new Date()):df.format(syncLogDate));
		
		return "/module/amrsmobileforms/syncLog";
	}
	
	@RequestMapping(value="/module/amrsmobileforms/syncLog", method=RequestMethod.POST)
	public String changeLogDate(HttpSession httpSession, @RequestParam String logDate) {
		if (logDate.trim().length() > 0) {
			try {
				syncLogDate=df.parse(logDate);
			} catch (ParseException e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Invalid date format" );
				e.printStackTrace();
			}
		}else
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "No Date selected");
		return "redirect:syncLog.list";		
	}	
}