package com.littlecloud.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.littlecloud.control.dao.branch.DdnsRecordsDAO;
import com.littlecloud.control.dao.branch.criteria.DdnsRecordsCriteria;
import com.littlecloud.control.entity.DdnsRecords;
import com.littlecloud.pool.object.utils.DdnsUtils;

/**
 * Servlet implementation class DdnsServlet
 */

@WebServlet(name="DdnsServlet", urlPatterns="/Network/Ddns")
public class DdnsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(DdnsServlet.class);       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DdnsServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pWriter = null;
		try{
			DdnsRecordsCriteria criteria = new DdnsRecordsCriteria();
			
			String params = "";
			
			if (request.getParameter("lastUpdate") != null){
				String lastUpdateString = request.getParameter("lastUpdate");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
				Date lastUpdateDate = formatter.parse(lastUpdateString);
				criteria.setLastUpdate(lastUpdateDate);
				params += " lastUpdate:" + criteria.getLastUpdate();
			}
			if (request.getParameter("ddnsName") != null){
				String ddnsName = request.getParameter("ddnsName");
				criteria.setDdnsName(ddnsName);
				params += " ddnsName:" + criteria.getDdnsName();
			}
			if (request.getParameter("ianaId") != null){
				String ianaId = request.getParameter("ianaId");
				Integer intInanId = Integer.valueOf(ianaId);
				criteria.setIanaId(intInanId);
				
				params += " ianaId:" + criteria.getIanaId();
			}
			if (request.getParameter("sn") != null){
				String sn = request.getParameter("sn");
				criteria.setSn(sn);
				params += " sn:" + criteria.getSn();
			}
			
			List<DdnsRecords> ddnsRecordsList = null;
			if (criteria.getIanaId() != null && criteria.getSn() != null){
				log.debug("DDNS20140402 - DdnsServlet - " + params);
				ddnsRecordsList = DdnsUtils.getDdnsRecordsList(criteria.getIanaId(), criteria.getSn());
			}else {
			
				log.debug("DDNS20140402 - DdnsServlet - " + params);
				DdnsRecordsDAO ddnsDao = new DdnsRecordsDAO();
				ddnsRecordsList = ddnsDao.getDdnsRecordsList(criteria);
			}
			pWriter = response.getWriter();
			StringBuilder sb = new StringBuilder();
			
			if (ddnsRecordsList != null){
				for (DdnsRecords ddnsRecords: ddnsRecordsList){
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
					sb.append(ddnsRecords.getId());
					sb.append(",");
					sb.append(ddnsRecords.getIanaId());
					sb.append(",");
					sb.append(ddnsRecords.getSn());
					sb.append(",");
					sb.append(ddnsRecords.getOrganizationId());
					sb.append(",");
					sb.append(ddnsRecords.getWanId());
					sb.append(",");
					sb.append(ddnsRecords.getDdnsName());
					sb.append(",");
					sb.append(ddnsRecords.getWanIp());
					sb.append(",");
					if (ddnsRecords.getLastUpdated() != null){
						sb.append(formatter.format(ddnsRecords.getLastUpdated()));						
					}
					sb.append("\n");
				}
			}
			
			pWriter.write(sb.toString());		
			pWriter.close();

		} catch (Exception e){
			// DDNS20140402
			log.error("DDNS20140402 - DdnsServlet - doPost()", e);
		} finally {
			IOUtils.closeQuietly(pWriter);
		}
	}

} // end class
