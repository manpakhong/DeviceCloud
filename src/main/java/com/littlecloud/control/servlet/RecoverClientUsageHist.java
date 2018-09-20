package com.littlecloud.control.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.branch.SnsOrganizationsDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.peplink.api.db.util.DBUtil;

@WebServlet(name="recoverClientUsage", urlPatterns="/recoverClientUsage")
public class RecoverClientUsageHist extends HttpServlet
{
	private static Logger logger = Logger.getLogger(RecoverClientUsageHist.class);
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String flag = request.getParameter("flag");
		String interval = request.getParameter("interval");
		String deviceCount = request.getParameter("per");
		String cur_org = request.getParameter("curorg");
		String sn = request.getParameter("sn");
		String iana = request.getParameter("iana");
		int sec = 1000;
		int count = 0;
		int per = 5;
		int iana_id = Integer.parseInt(iana);
		File f = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try
		{
			f = new File("/mnt/fetchFailedDevice.log");
			if( f.exists() == false )
				f.createNewFile();
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
		}
		catch(IOException e)
		{
			logger.error("Open file err -" + e,e);
		}
		
		if( interval != null && !interval.isEmpty() )
		{
			sec = Integer.parseInt(interval) * 1000;
		}
		
		if( deviceCount != null && !deviceCount.isEmpty() )
		{
			per = Integer.parseInt(deviceCount);
		}
		
		if( flag != null && !flag.isEmpty())
		{
			DevicesDAO devicesDAO = null;
			if(flag.equals("all"))
			{
				try 
				{
					DBUtil dbUtil = DBUtil.getInstance();
					SnsOrganizationsDAO snsOrganizationDAO = new SnsOrganizationsDAO();
					List<String> orgIds = null; 
					if( cur_org != null && !cur_org.isEmpty() )
						orgIds = snsOrganizationDAO.getDistinctOrgListUndo(cur_org);
					else
						orgIds = snsOrganizationDAO.getDistinctOrgList();
					for(String orgId : orgIds)
					{
						try
						{
							dbUtil.startSession();
							
							logger.warn("DEBUG Recover client usage history data of org " + orgId);
							devicesDAO = new DevicesDAO(orgId);
							List<Devices> devices = devicesDAO.getAllDevices();
							if( devices != null )
							{
								for(Devices dev : devices)
								{
									count++;
									
									DevOnlineObject devOnlineO = new DevOnlineObject();
									devOnlineO.setSn(dev.getSn());
									devOnlineO.setIana_id(dev.getIanaId());
									
									try 
									{
										devOnlineO = ACUtil.getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
										if( devOnlineO == null || devOnlineO.isOnline() == false )
										{
											try 
											{
												bw.write(new Date()+" ");
												bw.write("Device "+ dev.getSn() +" is offline");
												bw.write("\n");
											} 
											catch (IOException e) 
											{
												logger.error("File write error"+e,e);
											}
											continue;
										}
									} 
									catch (InstantiationException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} 
									catch (IllegalAccessException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									
									
									logger.info("Recover client usage history data on device " + dev.getSn());
									String sid = "REC" + JsonUtils.genServerRef();
									ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_USAGE_HIST, sid, dev.getIanaId(), dev.getSn());
									if( count % per == 0 )
									{
										try 
										{
											Thread.sleep(sec);
										} 
										catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
						catch(SQLException e)
						{
							logger.error("Loop org error -"+e,e);
						}
						finally
						{
							if(dbUtil.isSessionStarted())
								dbUtil.endSession();
							bw.close();
							fw.close();
						}
					}
					
				} 
				catch (SQLException e) {
					logger.error("Run all error -"+e,e);
				}
			}
			else
			{
				try 
				{
					devicesDAO = new DevicesDAO(flag);
					DevOnlineObject devOnlineO = null;
					if(sn == null || sn.isEmpty())
					{
						List<Devices> devices = devicesDAO.getAllDevices();
						if( devices != null )
						{
							for(Devices dev : devices)
							{
								try 
								{
									devOnlineO = new DevOnlineObject();
									devOnlineO.setSn(dev.getSn());
									devOnlineO.setIana_id(dev.getIanaId());
									devOnlineO = ACUtil.getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
									if( devOnlineO == null || devOnlineO.isOnline() == false )
									{
										try 
										{
											bw.write(new Date()+" ");
											bw.write("Device "+ dev.getSn() +" is offline");
											bw.write("\n");
										} 
										catch (IOException e) 
										{
											logger.error("File write error"+e,e);
										}
										continue;
									}
								} 
								catch (InstantiationException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} 
								catch (IllegalAccessException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
								logger.warn("DEBUG Recover client usage history data on device " + dev.getSn());
								String sid = "REC" + JsonUtils.genServerRef();
								ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_USAGE_HIST, sid, dev.getIanaId(), dev.getSn());
								if( count % per == 0 )
								{
									try 
									{
										Thread.sleep(sec);
									} 
									catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
					else
					{
						try 
						{
							devOnlineO = new DevOnlineObject();
							devOnlineO.setSn(sn);
							devOnlineO.setIana_id(iana_id);
							devOnlineO = ACUtil.getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
							if( devOnlineO == null || devOnlineO.isOnline() == false )
							{
								try 
								{
									bw.write(new Date()+" ");
									bw.write("Device "+ sn +" is offline");
									bw.write("\n");
								} 
								catch (IOException e) 
								{
									logger.error("File write error"+e,e);
								}
							}
						} 
						catch (InstantiationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
						catch (IllegalAccessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						logger.warn("DEBUG Recover client usage history data on device " + sn);
						String sid = "REC" + JsonUtils.genServerRef();
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_STATION_USAGE_HIST, sid, iana_id, sn);
					}
				} 
				catch (SQLException e) 
				{
					logger.error("Run one org error -"+e, e);
				}
				finally
				{
					bw.close();
					fw.close();
				}
			}
		}
		PrintWriter out = response.getWriter();
		out.println("Fetch job has completed");
	}
}
