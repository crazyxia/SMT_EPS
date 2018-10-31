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
	private TextArea separatorContentTa;
	@FXML
	private TextArea contentTa;
	@FXML
	private TextArea contentsTa;
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
			contentTa.setText(AddRuleController.resultTaString);
		} else {
			contentTa.setText(AddRuleController.contentTaString);
		}
		contentTa.requestFocus();
		info("初始化成功");
	}

	
	/**
	 * @author HCJ 确定分隔符
	 * @date 2018年10月29日 下午3:20:10
	 */
	public void onConfirmDelimiterBtClick() {
		if (checkContentIsExist() && separatorTf.getText() != null
				&& contentTa.getText().contains(separatorTf.getText())) {
			String[] contents = null;
			try {
				contents = removeSpace(contentTa.getText().split(separatorTf.getText()));
			} catch (Exception e) {
				contents = removeSpace(contentTa.getText().split("\\" + separatorTf.getText()));
			}
			globalContents = contents;
			StringBuilder contentLbString = new StringBuilder();
			for (int i = 0; i < contents.length; i++) {
				contentLbString.append("[" + (i + 1) + "]" + " : " + contents[i] + "\r\n");
			}
			contentsTa.setText(contentLbString.toString());
		} else {
			error("请输入有效的分割符");
			logger.error("请输入有效的分割符");
		}
	}

	
	/**
	 * @author HCJ 确定序号
	 * @date 2018年10月29日 下午3:20:49
	 */
	public void onConfirmBtClick() {
		try {
			orderNum = Integer.parseInt(orderNumTf.getText());
			if (orderNum < 1 || orderNum > globalContents.length) {
				orderNumTf.setText("");
				separatorContentTa.setText("");
				error("请正确填写序号");
				logger.error("请正确填写序号");
			} else {
				separatorContentTa.setText(globalContents[orderNum - 1]);
			}
		} catch (NumberFormatException e) {
			error("只能填整数");
			logger.error("只能填整数");
		}
	}

	
	public void onSaveSeparatorBtClick() {
		addRuleController.setStagingRules("separator:" + separatorTf.getText() + "=" + (orderNum - 1) + ",");
		addRuleController.setResultTa(separatorContentTa.getText());
		stage.close();
	}

	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	
	public void setAddRuleController(AddRuleController addRuleController) {
		this.addRuleController = addRuleController;
	}

	
	private String[] removeSpace(String[] array) {
		List<String> tmp = new ArrayList<String>();
		for (String str : array) {
			if (str != null && str.length() != 0) {
				tmp.add(str);
			}
		}
		return tmp.toArray(new String[0]);
	}

	
	/**
	 * @author HCJ 校验文本内容
	 * @date 2018年10月29日 下午3:29:40
	 */
	private Boolean checkContentIsExist() {
		if (contentTa.getText() != null && !contentTa.getText().equals("")) {
			return true;
		}
		error("请扫描二维码");
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
