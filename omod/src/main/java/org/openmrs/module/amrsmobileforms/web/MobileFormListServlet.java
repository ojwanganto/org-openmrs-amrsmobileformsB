package org.openmrs.module.amrsmobileforms.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.module.xforms.XformConstants;

/**
 * Creates an formList XMl from which a phone will get resources
 * 
 * @author Samuel Mbugua
 */

public class MobileFormListServlet extends HttpServlet {
	private static Log log = LogFactory.getLog(MobileFormListServlet.class);
	private static final long serialVersionUID = 6786000880818376733L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			File queueDir = MobileFormEntryUtil.getMobileFormsResourcesDir();

			String xml = "<?xml version='1.0' encoding='UTF-8' ?>";
			xml += "\n<forms>";

			for (File file : queueDir.listFiles()) {
				String fileName = file.getName();
				String url = request.getRequestURL().toString();
				String fileUrl = url.substring(0, url.lastIndexOf('/') + 1);
				fileUrl += "fileDownload?file=";

				xml += "\n  <form ";
				xml += "url=\"" + fileUrl + fileName + "\">";
				xml += fileName.replace('_', ' ').substring(0,fileName.lastIndexOf("."));
				xml += "</form>";
			}

			xml += "\n</forms>";
			response.setContentType(XformConstants.HTTP_HEADER_CONTENT_TYPE_XML);
			response.getOutputStream().print(xml);
		}

		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}
}