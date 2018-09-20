package com.littlecloud.helpers;

import com.littlecloud.control.entity.FirmwareVersions;

public class FirmwareVersionsHelper {
	public static String getVersion(FirmwareVersions firmwareVersion){
		String version = "";
		if (firmwareVersion != null && firmwareVersion.getVersion() != null){
			if (firmwareVersion.getVersion().equalsIgnoreCase(FirmwareVersions.VERSION_CUSTOM)){
				version = firmwareVersion.getCustom_Version();
			} else {
				version = firmwareVersion.getVersion();
			}
		}
		return version;
	}
}
