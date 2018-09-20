package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.littlecloud.control.entity.branch.Products;

public class ProductInfoObject extends PoolObject implements PoolObjectIf, Serializable {

	/* key */
	private String orgId = "Branch";
	private Integer iana_id = 9999;
	private ProductLst productLst;		
	
	private boolean isLoaded = false;
	
	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}
		
	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getOrgId() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String orgId) {
		this.iana_id = iana_id;
		this.orgId = orgId;
	}
	
	public ProductInfoObject() {
		super();
		productLst = new ProductLst();
	}
	
	public ProductLst getProductLst() {
		return productLst;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductInfoObject [orgId=");
		builder.append(orgId);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", productLst=");
		builder.append(productLst);
		builder.append(", isLoaded=");
		builder.append(isLoaded);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
	
	public ArrayList<Products> getProductInfoLst() {
		return productLst.getProductInfoLst();
	}

	public void setProductInfoLst(ArrayList<Products> productInfoLst) {
		productLst.setProductInfoLst(productInfoLst);
	}
		
	public Integer getIana_id() {
		return iana_id;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}
	
	public static class ProductLst implements Serializable
	{
		private ArrayList<Products> productInfoLst = new ArrayList<Products>();
		
		public ArrayList<Products> getProductInfoLst() {
			return productInfoLst;
		}

		public void setProductInfoLst(ArrayList<Products> productInfoLst) {
			this.productInfoLst = productInfoLst;
		}

		public boolean add(Products e) {
			return productInfoLst.add(e);
		}
	
		public void add(int index, Products element) {
			productInfoLst.add(index, element);
		}
	
		public boolean addAll(Collection<? extends Products> c) {
			return productInfoLst.addAll(c);
		}
	
		public boolean addAll(int index, Collection<? extends Products> c) {
			return productInfoLst.addAll(index, c);
		}
	
		public boolean contains(Object o) {
			return productInfoLst.contains(o);
		}
	
		public boolean containsAll(Collection<?> c) {
			return productInfoLst.containsAll(c);
		}
	
		public Products get(int index) {
			return productInfoLst.get(index);
		}
	
		public Products remove(int index) {
			return productInfoLst.remove(index);
		}
	
		public boolean remove(Object o) {
			return productInfoLst.remove(o);
		}
	
		public int size() {
			return productInfoLst.size();
		}
		
		public String toString() {
			return productInfoLst.toString();
		}
	}
	
	
}
