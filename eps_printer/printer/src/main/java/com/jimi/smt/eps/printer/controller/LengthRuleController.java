package com.jimi.smt.eps.printer.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LengthRuleController implements Initializable {

	private Logger logger = LogManager.getRootLogger();
	@FXML
	private TextField startTf;
	@FXML
	private TextField endTf;
	@FXML
	private TextArea scanTa;
	@FXML
	private TextArea lengthResultTa;
	@FXML
	private Label tipLb;
	
	private Stage stage;
	
	private AddRuleController addRuleController;
	// 起始位置
	private int start;
	// 结束位置
	private int end;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (AddRuleController.resultTaString != null && !AddRuleController.resultTaString.equals("")) {
			scanTa.setText(AddRuleController.resultTaString);
		} else {
			scanTa.setText(AddRuleController.scanTaString);
		}
		startTf.requestFocus();
	}

	
	/**
	 * @author HCJ 确定起止位置的序号
	 * @date 2018年10月29日 下午3:21:17
	 */
	public void onConfirmPositionBtClick() {
		try {
			start = Integer.parseInt(startTf.getText());
			end = Integer.parseInt(endTf.getText());
			if (start < 1 || end <= 1 || start > end || end > scanTa.getText().length()) {
				startTf.setText("");
				endTf.setText("");
				lengthResultTa.setText("");
				error("请正确填写序号");
				logger.error("请正确填写序号");
			} else {
				lengthResultTa.setText(scanTa.getText().substring(start - 1, end));
			}
		} catch (NumberFormatException e) {
			error("只能填整数");
			logger.error("只能填整数");
			lengthResultTa.setText("");
		}
	}

	
	/**@author HCJ
	 * 保存条目
	 * @date 2018年10月31日 下午2:37:01
	 */
	public void onSaveItemBtClick() {
		addRuleController.setResultTa(lengthResultTa.getText());
		addRuleController.appendRuleItem("length:" + (start - 1) + "=" + end + ",");
		stage.close();
	}

	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	
	public void setAddRuleController(AddRuleController addRuleController) {
		this.addRuleController = addRuleController;
	}

	
	/**
	 * @author HCJ 提示错误信息
	 * @param message
	 * @date 2018年10月29日 下午3:24:22
	 */
	public void error(String message) {
		tipLb.setTextFill(Color.RED);
		tipLb.setText(message);
		logger.error(message);
	}

	
	/**
	 * @author HCJ 提示基本信息
	 * @param message
	 * @date 2018年10月29日 下午3:24:40
	 */
	public void info(String message) {
		tipLb.setTextFill(Color.BLACK);
		tipLb.setText(message);
		logger.info(message);
	}
}
