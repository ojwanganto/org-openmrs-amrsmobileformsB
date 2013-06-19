package org.openmrs.module.amrsmobileforms.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.module.xforms.XformConstants;

/**
 * Provides MobileForm download services.
 * 
 * @author Samuel Mbugua
 */
public class MobileFormDownloadServlet extends HttpServlet {
	public static final long serialVersionUID = 123427878377111L;
	private Log log = LogFactory.getLog(this.getClass());

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileName = request.getParameter("file");
		try {
			File file = new File(MobileFormEntryUtil.getMobileFormsResourcesDir().getAbsolutePath()	+ "/" + fileName);

			if (file.exists() && file.length() > 0 && file.isFile()) {
				response.setContentType(XformConstants.HTTP_HEADER_CONTENT_TYPE_XML);
				InputStream iStream = new FileInputStream(file);
				byte[] bytes = new byte[iStream.available()];
				iStream.read(bytes);
				response.getOutputStream().write(bytes);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}
}