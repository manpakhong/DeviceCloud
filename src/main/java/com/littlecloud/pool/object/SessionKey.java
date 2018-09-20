package com.littlecloud.pool.object;

import java.io.Serializable;

import com.littlecloud.pool.utils.Utils;

public class SessionKey implements Serializable {

	private static final long serialVersionUID = 2398401738355338078L;
	
	private String custId = null;
	private String type = null;
	
	public SessionKey(String custId, String type) {
		super();
		this.custId = Utils.ToNullOrLowercase(custId);
		this.type = Utils.ToNullOrLowercase(type);
	}

	public String getCustId() {
		return custId;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "SessionKey [custId=" + custId + ", type=" + type + "]";
	}

	public boolean equals(Object o) {
		if (o instanceof SessionKey) {		
			SessionKey sess = (SessionKey) o;
			if (this.hashCode()==sess.hashCode())
				return true;
		}
		return false;
	}
	
	public int hashCode()
	{
		if (this.custId==null || this.type==null)
			return 0;
		
		return (this.custId + this.type).hashCode();
	}
}
