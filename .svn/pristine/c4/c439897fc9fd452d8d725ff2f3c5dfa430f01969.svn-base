package com.littlecloud.control.entity.root;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dummy")
public class RootBranchesTO extends RootBranches {

	private String organization_id;
	private Integer iana_id;
	private String sn;

	@Column(name = "organization_id")
	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}

	@Column(name = "iana_id")
	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}

	@Column(name = "sn")
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RootBranchesTO [organization_id=");
		builder.append(organization_id);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", id=");
		builder.append(id);
		builder.append(", url=");
		builder.append(url);
		builder.append(", api=");
		builder.append(api);
		builder.append(", ac1=");
		builder.append(ac1);
		builder.append(", ac1_port=");
		builder.append(ac1_port);
		builder.append(", ac2=");
		builder.append(ac2);
		builder.append(", ac2_port=");
		builder.append(ac2_port);
		builder.append(", name=");
		builder.append(name);
		builder.append(", status=");
		builder.append(status);
		builder.append(", tableName=");
		builder.append(tableName);
		builder.append(", mode=");
		builder.append(mode);
		builder.append("]");
		return builder.toString();
	}
}
