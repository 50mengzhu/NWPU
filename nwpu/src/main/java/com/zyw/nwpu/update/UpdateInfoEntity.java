package com.zyw.nwpu.update;

public class UpdateInfoEntity {
	private String version;
	private String description;
	private String apkurl;
	private String packagesize;
	private boolean isForceUpdate;

	public String getSize() {
		return packagesize;
	}

	public void setSize(String packagesize) {
		this.packagesize = packagesize;
	}

	public boolean getForceUpdate() {
		return isForceUpdate;
	}

	public void setForceUpdate(boolean isForceUpdate) {
		this.isForceUpdate = isForceUpdate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApkurl() {
		return apkurl;
	}

	public void setApkurl(String apkurl) {
		this.apkurl = apkurl;
	}

}
