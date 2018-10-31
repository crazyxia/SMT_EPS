package com.jimi.smt.eps.printer.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

public class SeparatorRuleController implements Initializable {

	private Logger logger = LogManager.getRootLogger();
	@FXML
	private Label tipLb;
	@FXML
	private TextArea separatorResultTa;
	@FXML
	private TextArea scanTa;
	@FXML
	private TextArea scanContentsTa;
	@FXML
	private TextField separatorTf;
	@FXML
	private TextField orderNumTf;
	
	private Stage stage;
	// 存储分割后的文本数组
	private String[] globalContents;
	// 存储序号
	private int orderNum;

	private AddRuleController addRuleController;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (AddRuleController.resultTaString != null && !AddRuleController.resultTaString.equals("")) {
			scanTa.setText(AddRuleController.resultTaString);
		} else {
			scanTa.setText(AddRuleController.scanTaString);
		}
		scanTa.requestFocus();
		info("初始化成功");
	}

	
	/**
	 * @author HCJ 确定分割符
	 * @date 2018年10月29日 下午3:20:10
	 */
	public void onConfirmDelimiterBtClick() {
		if (checkScanContentIsExist() && separatorTf.getText() != null && scanTa.getText().contains(separatorTf.getText())) {
			String[] contents = null;
			try {
				contents = removeSpace(scanTa.getText().split(separatorTf.getText()));
			} catch (Exception e) {
				contents = removeSpace(scanTa.getText().split("\\" + separatorTf.getText()));
			}
			globalContents = contents;
			StringBuilder contentLbString = new StringBuilder();
			for (int i = 0; i < contents.length; i++) {
				contentLbString.append("[" + (i + 1) + "]" + " : " + contents[i] + "\r\n");
			}
			scanContentsTa.setText(contentLbString.toString());
		} else {
			error("请输入有效的分割符");
			logger.error("请输入有效的分割符");
		}
	}

	
	/**
	 * @author HCJ 确定序号
	 * @date 2018年10月29日 下午3:20:49
	 */
	public void onConfirmIdBtClick() {
		try {
			orderNum = Integer.parseInt(orderNumTf.getText());
			if (orderNum < 1 || orderNum > globalContents.length) {
				orderNumTf.setText("");
				separatorResultTa.setText("");
				error("请正确填写序号");
				logger.error("请正确填写序号");
			} else {
				separatorResultTa.setText(globalContents[orderNum - 1]);
			}
		} catch (NumberFormatException e) {
			error("只能填整数");
			logger.error("只能填整数");
		}
	}

	
	/**@author HCJ
	 * 保存条目
	 * @date 2018年10月31日 下午2:39:44
	 */
	public void onSaveItemBtClick() {
		addRuleController.appendRuleItem("separator:" + separatorTf.getText() + "=" + (orderNum - 1) + ",");
		addRuleController.setResultTa(separatorResultTa.getText());
		stage.close();
	}

	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	
	public void setAddRuleController(AddRuleController addRuleController) {
		this.addRuleController = addRuleController;
	}

	
	/**@author HCJ
	 * 移除数组中的空字符，如["","aaa","bbb"]会返回["aaa","bbb"]
	 * @date 2018年10月31日 下午2:37:29
	 */
	private String[] removeSpace(String[] array) {
		List<String> tmp = new ArrayList<String>();
		for (String str : array) {
			if (str != null && str.length() != 0) {
				tmp.add(str);
			}
		}
		return tmp.toArray(new String[0]);
	}

	
	/**@author HCJ
	 * 校验文本内容
	 * @return true 文本内容不为空
	 * @return false 文本内容为空
	 * @date 2018年10月31日 下午7:07:00
	 */
	private Boolean checkScanContentIsExist() {
		if (scanTa.getText() != null && !scanTa.getText().equals("")) {
			return true;
		}
		error("请扫描条码");
		return false;
	}

	
	/**
	 * @author HCJ 提示错误信息
	 * @param message
	 * @date 2018年10月29日 下午3:24:22
	 */
	private void error(String message) {
		tipLb.setTextFill(Color.RED);
		tipLb.setText(message);
		logger.error(message);
	}

	
	/**
	 * @author HCJ 提示基本信息
	 * @param message
	 * @date 2018年10月29日 下午3:24:40
	 */
	private void info(String message) {
		tipLb.setTextFill(Color.BLACK);
		tipLb.setText(message);
		logger.info(message);
	}
}
