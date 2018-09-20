package com.littlecloud.pool.object.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.branch.ProductsDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.object.ProductInfoObject;

public class ProductUtils {

	private static Logger log = Logger.getLogger(ProductUtils.class);

	private static final Integer RELOAD_IN_SEC = 3600;
	// private final static Integer dummyIanaId = 9999;
	// private final static String dummySn = "Branch";

	private enum OP {
		SAVE, DELETE, SAVEORUPDATE
	};

	private static final Lock lock = new ReentrantLock();
	private static AtomicInteger lockCount = new AtomicInteger();

	public static String getDeviceType(String orgId, Integer devId) {
		Devices dev = NetUtils.getDevicesWithoutNetId(orgId, devId);
		if (dev == null)
		{
			log.warnf("%s dev %d not found", orgId, devId);
			return null;
		}

		Products product = ProductUtils.getProducts(dev.getProductId());
		return product.getDeviceType();
	}

//	private static ProductsTO convertProductsTO(Products product)
//	{
//		ProductsTO pto = new ProductsTO();
//		
//		try {
//			BeanUtils.copyProperties(pto, product);
//			for (int i=0; i<ProductsTO.MAX_RADIO;i++)
//			{
//				Radio radio = pto.new Radio();
//			}
//			
//		} catch (Exception e) {
//			log.error("getProductsTO - copyProperties", e);
//		}
//	}
	
	public static Products getProducts(Integer productId)
	{
		ProductInfoObject productO = getInfoObject();

		int index = 0;
		int indexFound = -1;
		
		if (productO==null)
		{
			return null;
		}
		
		for (Products prod : productO.getProductInfoLst())
		{
			if (prod == null)
			{
				log.warnf("prod in loop is null!!");
				continue;
			}

			//log.debugf("matching prod %d with productId %d", prod.getId(), productId);
			if (prod!=null)
			{
				if (prod.getId() == productId)
				{
					//log.debugf("prod %d is found!", productId);
					indexFound = index;
					break;
				}
			}

			index++;
		}

		if (indexFound != -1)
			return productO.getProductLst().get(indexFound);
		else
		{
			log.debugf("prod %d not found!", productId);
			return null;
		}
	}

	public static List<Products> getProductLst()
	{
		List<Products> prodLst = null;

		ProductInfoObject productO = getInfoObject();
		prodLst = productO.getProductInfoLst();

		if (prodLst == null)
			prodLst = new ArrayList<Products>();

		return prodLst;
	}

	private synchronized static void loadObjectToCache(ProductInfoObject infoObj) throws SQLException {
		ProductsDAO productDAO = new ProductsDAO();
		List<Products> productLst = null;

		if (log.isInfoEnabled())
			log.infof("ProductInfoObject - Loading branch products");
		productLst = productDAO.getAllProducts();
		if (log.isInfoEnabled())
			log.infof("ProductInfoObject - Loaded branch productLst.size %d", productLst.size());
		infoObj.getProductInfoLst().clear();
		infoObj.getProductInfoLst().addAll(productLst);
		infoObj.setLoaded(true);
		putProductInfoObject(infoObj);
		if (log.isInfoEnabled())
			log.info("ProductInfoObject - updated");
	}

	public static ProductInfoObject getInfoObject()
	{
		boolean reload = false;
		
		ProductInfoObject productO = new ProductInfoObject();

		try {
			productO = ACUtil.<ProductInfoObject> getPoolObjectBySn(productO, ProductInfoObject.class);
			Long now = DateUtils.getUtcDate().getTime();

			if (productO!=null && productO.getCreateTime()!=null && (now - productO.getCreateTime() > RELOAD_IN_SEC * 1000L))
			{
				log.warnf("ProductInfo reach time to reload createtime %d now %d reload %d", productO.getCreateTime(), now, RELOAD_IN_SEC * 1000L);
				reload = true;
			}

			if (reload || productO == null || !productO.isLoaded() || productO.getProductInfoLst()==null || productO.getProductInfoLst().isEmpty())
			{
				if (lock.tryLock())
				{
					try {
						log.warnf("ProductUtils.locked %d", lockCount.incrementAndGet());
						productO = new ProductInfoObject();
						productO = ACUtil.<ProductInfoObject> getPoolObjectBySn(productO, ProductInfoObject.class);
						if (reload || productO == null || !productO.isLoaded())
						{
							productO = new ProductInfoObject();
							loadObjectToCache(productO);
						}
					} catch (Exception e) {
						log.error("ProductUtils - get productinfo object exception", e);
					} finally {
						lock.unlock();
						log.warnf("ProductUtils.release %d", lockCount.decrementAndGet());
					}					
				}
			}

//			if (!productO.isLoaded())
//			{
//				loadObjectToCache(productO);
//			}

		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
			return null;
		} catch (Exception e) {
			log.error("Exception ", e);
			return null;
		}
		return productO;
	}

	public static void putProductInfoObject(ProductInfoObject productInfo)
	{
		try {
			ACUtil.<ProductInfoObject> cachePoolObjectBySn(productInfo, ProductInfoObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void removeProductInfoObject(ProductInfoObject productInfo)
	{
		try {
			ACUtil.<ProductInfoObject> removePoolObjectBySn(productInfo, ProductInfoObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static boolean saveProducts(Integer productId, Products prod)
	{
		if (productId == null)
		{
			log.error("Given productId is null.");
			return false;
		}

		if (prod == null)
		{
			log.error("Given prod is null.");
			return false;
		}

		ProductInfoObject productO = getInfoObject();
		if (productO == null) {
			log.error("Given orgId does not exist");
			return false;
		}

		return doProducts(productO, prod, OP.SAVE);
	}

	public static boolean saveOrUpdateProducts(Integer productId, Products prod)
	{
		if (productId == null)
		{
			log.error("Given productId is invalid.");
			return false;
		}

		if (prod == null || prod.getId() == 0)
		{
			log.error("Given product info is incomplete.");
			return false;
		}

		ProductInfoObject productO = getInfoObject();
		if (productO == null) {
			log.error("Given orgId does not exist");
			return false;
		}

		if (saveOrUpdateProducts(productO, prod))
		{

			log.debugf("prod %d is added/update", prod.getId());
			return true;
		}
		else
		{
			log.debugf("prod %d is add/update failure", prod.getId());
			return false;
		}
	}

	public static boolean deleteProducts(Integer productId, Products prod)
	{
		if (productId == null)
		{
			log.error("Given id is invalid.");
			return false;
		}

		if (prod == null || prod.getId() == 0)
		{
			log.error("Given prod info is incomplete.");
			return false;
		}

		ProductInfoObject productO = getInfoObject();
		if (productO == null) {
			log.error("Given netId does not exist");
			return false;
		}

		if (!deleteProducts(productId, prod))
			log.warnf("device %d does not exist", prod.getId());

		return true;
	}

	private static boolean saveOrUpdateProducts(ProductInfoObject productO, Products prod)
	{
		return doProducts(productO, prod, OP.SAVEORUPDATE);
	}

	private static boolean deleteProducts(ProductInfoObject productO, String orgId, Products prod)
	{
		return doProducts(productO, prod, OP.DELETE);
	}

	private static boolean doProducts(ProductInfoObject productO, Products prod, OP op)
	{
		log.debugf("ProductUtils doProducts op %s %s %s", op, productO, prod);

		boolean result = false;
		int index = 0;

		ArrayList<Products> prodLst = productO.getProductInfoLst();

		switch (op)
		{
		// case SAVE:
		// devtLst.add(prod);
		// result = true;
		// break;
		case SAVE: // for safety
		case SAVEORUPDATE:
			log.debugf("ProductUtils - prodLst.size=%d", prodLst.size());
			for (Products prodFind : prodLst)
			{
				//log.debugf("ProductUtils - matching prodFind %d prod %d", prodFind.getId(), prod.getId());
				if (prodFind.getId() == prod.getId())
				{
					log.debugf("SAVEORUPDATE - removing prod %s (prod from index %d - %s)", prod.getId(), index, prodLst.get(index));
					prodLst.remove(index);
				}
				else
				{
					index++;
				}
			}
			prodLst.add(prod);
			result = true;
			break;
		case DELETE:
			log.debugf("ProductUtils - devtLst.size=%d", prodLst.size());
			for (Products prodFind : prodLst)
			{
				//log.debugf("ProductUtils - matching prodFind %d prod %d", prodFind.getId(), prod.getId());
				if (prodFind.getId() == prod.getId())
				{
					log.debugf("DELETE - removing active prod %s (prod from index %d - %s)", prod.getId(), index, prodLst.get(index));
					prodLst.remove(index);
					result = true;
					log.info("find and delete " + prod.getId());
				}
				else
				{
					index++;
				}
			}
			break;
		default:
			log.error("unknown operation!");
			break;
		}

		productO.setProductInfoLst(prodLst);
		putProductInfoObject(productO);

		return result;
	}
}
