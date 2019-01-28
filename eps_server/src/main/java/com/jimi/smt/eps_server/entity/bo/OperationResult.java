package com.jimi.smt.eps_server.entity.bo;

/**
 * 存储所有操作是否完成的结果
 * @author HCJ
 * @date 2019年1月26日 下午3:50:13
 */
public class OperationResult {

	/**
	 * store : 发料
	 */
	private String store;
	/**
	 * feed : 上料
	 */
	private String feed;
	/**
	 * change : 换料
	 */
	private String change;
	/**
	 * check : 核料
	 */
	private String check;
	/**
	 * checkAll : 全检
	 */
	private String checkAll;
	/**
	 * firstCheckAll : 首检
	 */
	private String firstCheckAll;

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getFeed() {
		return feed;
	}

	public void setFeed(String feed) {
		this.feed = feed;
	}

	public String getChange() {
		return change;
	}

	public void setChange(String change) {
		this.change = change;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getCheckAll() {
		return checkAll;
	}

	public void setCheckAll(String checkAll) {
		this.checkAll = checkAll;
	}

	public String getFirstCheckAll() {
		return firstCheckAll;
	}

	public void setFirstCheckAll(String firstCheckAll) {
		this.firstCheckAll = firstCheckAll;
	}

	/**
	 * 构造函数
	 * @param store 所有上料结果是否为1
	 * @param feed 所有发料结果是否为1
	 * @param change 所有换料结果是否为1
	 * @param check 所有核料结果是否为1
	 * @param checkAll 所有全检结果是否为1
	 * @param firstCheckAll 所有首检结果是否为1
	 */
	public OperationResult(String store, String feed, String change, String check, String checkAll,
			String firstCheckAll) {
		super();
		this.store = store;
		this.feed = feed;
		this.change = change;
		this.check = check;
		this.checkAll = checkAll;
		this.firstCheckAll = firstCheckAll;
	}
}
