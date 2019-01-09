package com.jimi.smt.eps_server.entity;

public class LineOperationResult {

	private Integer line;
	
	private Integer feedResult;

    private Integer changeResult;

    private Integer checkResult;

    private Integer checkAllResult;

    private Integer firstCheckAllResult;

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public Integer getFeedResult() {
		return feedResult;
	}

	public void setFeedResult(Integer feedResult) {
		this.feedResult = feedResult;
	}

	public Integer getChangeResult() {
		return changeResult;
	}

	public void setChangeResult(Integer changeResult) {
		this.changeResult = changeResult;
	}

	public Integer getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(Integer checkResult) {
		this.checkResult = checkResult;
	}

	public Integer getCheckAllResult() {
		return checkAllResult;
	}

	public void setCheckAllResult(Integer checkAllResult) {
		this.checkAllResult = checkAllResult;
	}

	public Integer getFirstCheckAllResult() {
		return firstCheckAllResult;
	}

	public void setFirstCheckAllResult(Integer firstCheckAllResult) {
		this.firstCheckAllResult = firstCheckAllResult;
	}
}
