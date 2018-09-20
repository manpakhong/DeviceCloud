package com.littlecloud.pool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.jboss.logging.Logger;

import com.littlecloud.ac.DebugManager;
import com.littlecloud.control.dao.DeviceConfigurationsDAO;
import com.littlecloud.control.dao.DeviceUpdatesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.entity.DeviceConfigurations;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerNetworkTask;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.utils.BranchUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.opensymphony.xwork2.ActionSupport;

public class ConfigAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

	private static Logger log = Logger.getLogger(ConfigAction.class);

	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	public static String TEST_ORGID = null;
	public static Integer TEST_NETID = null;

	private static final ExecutorService startConfigUpdateExecutor = Executors.newSingleThreadExecutor();
	private static Future<?> lastConfigExecution;
	private static int skipCount = 0;

	// private final int totalNumOfDevicesPerBatch = 100000;
	private static final ExecutorService configPutRetryExecutor = Executors.newFixedThreadPool(5);	

	protected HttpServletRequest request;
	protected HttpServletResponse response;

	protected static final String RESULT_TRUE = "true";
	protected static final String RESULT_FALSE = "false";

	public HttpServletRequest getServletRequest() {
		return request;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getServletResponse() {
		return response;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@SkipValidation
	public String startConfigUpdate() throws IOException
	{
		log.warnf("INFO startConfigUpdate: enter");
		
		final Runnable queueTask = new Runnable() {
			@Override
			public void run() {
				
				log.warnf("INFO startConfigUpdate - queueTask start");
				
				try {					
					if (lastConfigExecution != null && !lastConfigExecution.isDone()) {
						skipCount++;
						log.warnf("INFO startConfigUpdate - skips %d times", skipCount);
						return;
					}
					else
					{
						skipCount = 0;
					}
										
					lastConfigExecution = startConfigUpdateExecutor.submit(new Runnable() {

						@Override
						public void run() {
							log.warnf("INFO startConfigUpdate - running configExecutor");
							
							Set<String> snsOrgIdSet = null;

							snsOrgIdSet = BranchUtils.getOrgIdSet();
							log.debugf("snsOrgLst.size = %d", snsOrgIdSet==null?0:snsOrgIdSet.size());
							if (snsOrgIdSet == null)
							{
								log.warn("startConfigUpdate - Empty organization list!");
								return;
							}

							/* for each organization, process a number of devices */
							if (!PROD_MODE)
							{
								snsOrgIdSet.clear();
								snsOrgIdSet.add(TEST_ORGID);
							}

							for (String orgId : snsOrgIdSet)
							{
								log.debugf("startConfigUpdate - doing orgId %s", orgId);
								List<Networks> networkList = null;
								DeviceUpdatesDAO duDAO = null;
								Boolean bRequireUpdate = false;								

								try {
									duDAO = new DeviceUpdatesDAO(orgId);
									bRequireUpdate = duDAO.isRequireConfigUpdate();
								} catch (SQLException e) {
									log.errorf("transaction is rollback - %s %s %s", e.getMessage(), "Fail to isRequireConfigUpdate", e);
								}
								if (bRequireUpdate)
								{
									if (log.isInfoEnabled()) log.infof("update is required for orgId %s", orgId);
									
									if (PROD_MODE)
									{
										networkList = OrgInfoUtils.getNetworkLst(orgId);
									}
									else
									{
										Networks network = new Networks();
										network.setId(TEST_NETID);
										networkList = new ArrayList<Networks>();
										networkList.add(network);
									}
									
									if (networkList == null || networkList.size() == 0)
									{
										if (log.isInfoEnabled()) log.infof("No networks in this organization %s", orgId);
									}
									else
									{
										if (log.isInfoEnabled()) log.infof("Update total of %d networks in this organization %s", networkList.size(), orgId);
										
										for (Networks net : networkList)
										{
											if (log.isInfoEnabled()) log.infof("updating orgId %s netId %d", orgId, net.getId());
											try {												
												new ConfigUpdatePerNetworkTask(configPutRetryExecutor, this.getClass().getSimpleName() + JsonUtils.genServerRef(), orgId, net.getId(), JsonConf_RadioSettings.CONFIG.NONE).call();
											} catch (Exception e) {
												log.error("Fail to update configuration for net " + net.getId(), e);
											}
										}
									}
								}
								else
								{
									if (log.isDebugEnabled()) log.debugf("update is not required for orgId %s", orgId);
								}
								if (log.isInfoEnabled()) log.infof("startConfigUpdate - done orgId %s", orgId);
							}
							log.warnf("INFO startConfigUpdate - exit configExecutor");
						}
					});

				} catch (Exception e)
				{
					log.error("persistQueue exception", e);
				}
				
				log.warnf("INFO startConfigUpdate - queueTask exit");
			}
		};

		new Thread(queueTask).start();
		

		if (PROD_MODE)
		{
			response.getWriter().write("SUCCESS");
			IOUtils.closeQuietly(response.getWriter());
		}

		log.warnf("INFO startConfigUpdate: exit");
		return SUCCESS;
	}

	@SkipValidation
	public String getDeviceConfigurationFile() throws IOException
	{
		final boolean bReadOnlyDb = true;
		// String json = request.getParameter("json"); organization_id=8k7BiJ&device_id=8&start=2013-10-15T00:00:00
		String param_org = request.getParameter("organization_id");
		String param_devId = request.getParameter("device_id");
		String param_id = request.getParameter("id");
		Integer devId = Integer.parseInt(param_devId);
		Integer Id = Integer.parseInt(param_id);
		String back_up, ser_nub;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		byte[] file_content = new byte[1048576];

		InputStream inputStream = null;
		try
		{
			DevicesDAO devDAO = new DevicesDAO(param_org, bReadOnlyDb);
			Devices dev = devDAO.findById(devId);
			DeviceConfigurationsDAO deviceConfigurationsDAO = new DeviceConfigurationsDAO(param_org, bReadOnlyDb);
			DeviceConfigurations device_Configuration = deviceConfigurationsDAO.getDeviceConfigurationFile(devId, Id);
			file_content = device_Configuration.getFileContent();
			Date d = new Date(device_Configuration.getBackupTime() * (long) 1000);
			back_up = format.format(d);
			ser_nub = dev.getSn();
		} catch (Exception e)
		{
			log.error(e, e);
			return ERROR;
		}

		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Disposition", "inline; filename=\"" + back_up + "_" + ser_nub + ".conf\"");
		response.setContentType("application/octet-stream");
		inputStream = new ByteArrayInputStream(file_content);
		ServletOutputStream out = response.getOutputStream();
		byte[] data = new byte[4096];
		int bytesread = 0;
		while ((bytesread = inputStream.read(data)) > 0)
		{
			out.write(data, 0, bytesread);
		}
		if (out != null)
		{
			out.close();
		}
		if (inputStream != null)
		{
			inputStream.close();
		}
		response.getWriter().write("SUCCESS");
		IOUtils.closeQuietly(response.getWriter());

		log.debug("getDeviceConfigurationFile: exit");
		return SUCCESS;
	}

}