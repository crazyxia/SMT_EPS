package com.jimi.smt.eps.display.entity;

import com.jimi.smt.eps.display.constant.BoardResetReson;


/**板子数量重置信息类
 * @author   HCJ
 * @date     2019年3月4日 下午3:16:12
 */
public class BoardResetInfo {

	private Integer line;

	private BoardResetReson boardResetReson;

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public BoardResetReson getBoardResetReson() {
		return boardResetReson;
	}

	public void setBoardResetReson(BoardResetReson boardResetReson) {
		this.boardResetReson = boardResetReson;
	}

}
