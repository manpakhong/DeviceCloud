package com.littlecloud.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.pool.Cluster;
import com.littlecloud.pool.Cluster.CACHE_ACTION;
import com.littlecloud.pool.Cluster.CacheException;
import com.littlecloud.pool.ClusterOption;
import com.littlecloud.pool.object.BranchInfoObject;
import com.littlecloud.pool.object.ProductInfoObject;
import com.littlecloud.pool.object.utils.OrgInfoUtils;

@WebServlet(name = "Update Cache Servlet", description = "This is a servlet to remove cache for reload", urlPatterns = "/updateCache")
public class UpdateCacheServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(UpdateCacheServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		String key = null;		
		
		String type = request.getParameter("type");
		String organization_id = request.getParameter("organization_id");
		String network_id = request.getParameter("network_id");
		type = type == null ? "" : type;
		PrintWriter out = response.getWriter();
		log.info("UpdateCacheServlet is called for " + type + " organization_id = " + organization_id + " network_id = " + network_id);
		if (type.isEmpty())
		{
			out.write("type is not given.");
			IOUtils.closeQuietly(out);
			return;
		}

		if (type.equals("sns_organizations"))
		{
			BranchInfoObject branchO = new BranchInfoObject();
			key = branchO.getKey();
		}
		else if (type.equals("products"))
		{
			ProductInfoObject productO = new ProductInfoObject();
			key = productO.getKey();
		}
		else if( type.equals("networks") )
		{
			int networkId = (network_id==null ? 0 : Integer.parseInt(network_id));
			try 
			{
				NetworksDAO networksDAO = new NetworksDAO(organization_id,true);
				Networks network = networksDAO.findById(networkId);
				if( network != null )
				{
					OrgInfoUtils.saveOrUpdateNetworks(organization_id, network);
					out.write("Update networks successfully!");					
				}
				else
				{
					log.info("Updated network is not found!");
					out.write("Updated network is not found!");
				}
			} 
			catch (SQLException e) 
			{
				log.error("Update networks Cache Exception ", e);
			}
			IOUtils.closeQuietly(out);
			return;
		}
		else
		{
			out.write("type not found.");
			IOUtils.closeQuietly(out);
			return;
		}

		try {
			if (Cluster.get(Cluster.CACHENAME.LittleCloudCache, key) == null)
			{
				out.write("key " + key + " is not found from grid.");
				IOUtils.closeQuietly(out);
				return;
			}
		} catch (CacheException e) {
			log.error("CacheException ", e);
			out.write(e.getMessage());
			IOUtils.closeQuietly(out);
			return;
		}

		try {
			ClusterOption opt = new ClusterOption();
			opt.setAction(CACHE_ACTION.remove);
			Cluster.remove(Cluster.CACHENAME.LittleCloudCache, key, opt);
			String msg = "key " + key + " is removed from grid.";
			System.out.println(msg);
			out.write(msg);
		} catch (CacheException e) {
			log.error("CacheException ", e);
			out.write(e.getMessage());
			IOUtils.closeQuietly(out);
			return;
		}

		IOUtils.closeQuietly(out);
	}
	
}
