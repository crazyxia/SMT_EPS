package com.jimi.smt.eps.ghost.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 抛料报表的数据响应JSON
 * <br>
 * <b>2019年3月6日</b>
 * @author 几米物联自动化部-洪达浩
 */
public class PartUsageReport {

	private PartUsage baseInfo;
	
	private List<PartUsageItem> details = new ArrayList<PartUsageItem>();

	
	public PartUsage getBaseInfo() {
		return baseInfo;
	}

	public void setBaseInfo(PartUsage baseInfo) {
		this.baseInfo = baseInfo;
	}

	public List<PartUsageItem> getDetails() {
		return details;
	}

	public void setDetails(List<PartUsageItem> details) {
		this.details = details;
	}
	
}
