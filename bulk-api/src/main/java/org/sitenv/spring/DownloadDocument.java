package org.sitenv.spring;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class is used for downloading attachment documents
 * Files are stored in {catalina.base}/data folder
 */
@WebServlet(urlPatterns = {"/download/*"}, displayName = "Authorize endpoint for FHIR Server")
public class DownloadDocument extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = System.getProperty("catalina.base");
        String[] pathInfo = request.getPathInfo().split("/");
        String fileName = pathInfo[1];
        

        File downloadFile = new File(contextPath + "/Data/" + fileName);
        FileInputStream inputStream = null;
        OutputStream outStream = null;

        try {
            inputStream = new FileInputStream(downloadFile);

            response.setContentLength((int) downloadFile.length());
            response.setContentType("application/json+fhir");

            // response header
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
            response.setHeader(headerKey, headerValue);

            // Write response
            outStream = response.getOutputStream();
            IOUtils.copy(inputStream, outStream);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream)
                    inputStream.close();
                if (null != inputStream)
                    outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
