package com.littlecloud.fusionhub;

import java.util.Date;

public class FusionHubLicenseInfo {

	/* - new table: device_pending_changes
	 * - id int auto_increment
	 * - sn varchar(30)
	 * - device_id int
	 * - message_type varchar(50) (PIPE_INFO_XXXX)
	 * - content text  (JSON file, license, config etc)
	 * - created_at datetime
	 * - last_attemp_time datetime
	 * - retry int
	 * - status varchar(10) pending/completed/cancelled/rejected
	 */
	
	
	private String product;
	private Json_FusionHubLicense licenses;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FusionHubLicenseInfo [product=");
		builder.append(product);
		builder.append(", licenses=");
		builder.append(licenses);
		builder.append("]");
		return builder.toString();
	}

	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public Json_FusionHubLicense getLicenses() {
		return licenses;
	}
	public void setLicenses(Json_FusionHubLicense licenses) {
		this.licenses = licenses;
	}

	
	
	
	public class License {
		private Integer period;
		private Integer no_of_session;		
		private Integer max_bandwidth;
		private String type;
		private String license_model;
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("License [period=");
			builder.append(period);
			builder.append(", no_of_session=");
			builder.append(no_of_session);
			builder.append(", max_bandwidth=");
			builder.append(max_bandwidth);
			builder.append(", type=");
			builder.append(type);
			builder.append(", license_model=");
			builder.append(license_model);
			builder.append("]");
			return builder.toString();
		}
		public Integer getPeriod() {
			return period;
		}
		public void setPeriod(Integer period) {
			this.period = period;
		}
		public Integer getNo_of_session() {
			return no_of_session;
		}
		public void setNo_of_session(Integer no_of_session) {
			this.no_of_session = no_of_session;
		}
		public Integer getMax_bandwidth() {
			return max_bandwidth;
		}
		public void setMax_bandwidth(Integer max_bandwidth) {
			this.max_bandwidth = max_bandwidth;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getLicense_model() {
			return license_model;
		}
		public void setLicense_model(String license_model) {
			this.license_model = license_model;
		}
	}
	
}
