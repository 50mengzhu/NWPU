package com.zyw.nwpu.xmz;

/**
 * 2016年4月1日
 * 
 * 项目制
 * 
 * 项目实体类
 * 
 * @author Rocket
 * 
 */
public class Project {

	private int id = -1;
	private int project_num = -1;
	private String name = "";
	private int limit_people = -1;
	private String startTime = "";
	private String endTime = "";
	private String phone = "";
	private String DWMC = "";// 主办学院
	private String project_location = "";

	// 一些细节
	private String detail = "";
	private String money_basis = "";
	private String basis = "";
	private String expection = "";

	// 项目类别
	private String platform_name = "";
	private String type_name_big = "";

	public Project() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProject_num() {
		return project_num;
	}

	public void setProject_num(int project_num) {
		this.project_num = project_num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLimit_people() {
		return limit_people;
	}

	public void setLimit_people(int limit_people) {
		this.limit_people = limit_people;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDWMC() {
		return DWMC;
	}

	public void setDWMC(String dWMC) {
		DWMC = dWMC;
	}

	public String getProject_location() {
		return project_location;
	}

	public void setProject_location(String project_location) {
		this.project_location = project_location;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getMoney_basis() {
		return money_basis;
	}

	public void setMoney_basis(String money_basis) {
		this.money_basis = money_basis;
	}

	public String getBasis() {
		return basis;
	}

	public void setBasis(String basis) {
		this.basis = basis;
	}

	public String getExpection() {
		return expection;
	}

	public void setExpection(String expection) {
		this.expection = expection;
	}

	public String getPlatform_name() {
		return platform_name;
	}

	public void setPlatform_name(String platform_name) {
		this.platform_name = platform_name;
	}

	public String getType_name_big() {
		return type_name_big;
	}

	public void setType_name_big(String type_name_big) {
		this.type_name_big = type_name_big;
	}

}