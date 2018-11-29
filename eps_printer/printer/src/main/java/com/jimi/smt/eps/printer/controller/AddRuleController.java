package com.jimi.smt.eps.printer.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.printer.entity.Rule;

import cc.darhao.dautils.api.ResourcesUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddRuleController implements Initializable {

	private Logger logger = LogManager.getRootLogger();
	@FXML
	private TextArea scanTa;
	@FXML
	private TextArea resultTa;
	@FXML
	private Label tipLb;
	@FXML
	private TextField ruleNameTf;

	private Stage stage;

	// 存储所有规则
	private List<Rule> rules = new ArrayList<Rule>();
	// 存储扫描得到的文本内容
	public static String scanTaString = null;
	// 存储分割后的文本内容
	public static String resultTaString = null;
	// 存储所有未保存的规则
	private StringBuilder ruleItems = new StringBuilder();
	
	private ManageRuleController manageRuleController;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		scanTa.requestFocus();
	}

	
	public void onLengthBtClick() {
		info("");
		rules = manageRuleController.getRules();
		if (setContentIfExist()) {
			scanTa.setDisable(true);
			showLengthWindow();
		}
	}

	
	public void onSeparatorBtClick() {
		info("");
		rules = manageRuleController.getRules();
		if (setContentIfExist()) {
			scanTa.setDisable(true);
			showSeparatorWindow();
		}
	}

	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	
	/**@author HCJ
	 * 添加所有暂存的规则
	 * @date 2018年10月31日 下午2:34:37
	 */
	public void appendRuleItem(String ruleItem) {
		ruleItems.append(ruleItem);
	}

	
	public void setResultTa(String result) {
		resultTa.setText(result);
	}

	
	public void setManageRuleController(ManageRuleController manageRuleController) {
		this.manageRuleController = manageRuleController;
	}

	
	/**
	 * @author HCJ 保存规则
	 * @date 2018年10月29日 下午3:22:10
	 */
	public void onSaveRuleBtClick() {
		boolean isResultTaContentAvailable = resultTa.getText() != null && !resultTa.getText().equals("");
		if (isResultTaContentAvailable && isRuleNameExist()) {
			Rule rule = new Rule();
			rule.setName(ruleNameTf.getText());
			rule.setExample(scanTa.getText());
			rule.setDetails(ruleItems.toString());
			rules.add(rule);
			manageRuleController.updateRules();
			ruleItems.delete(0, ruleItems.length());
			info("保存规则成功");
			stage.close();
		} else {
			error("保存失败，规则名称必须有效、不重复且结果必须存在");
			logger.error("保存失败，规则名称必须有效、不重复且结果必须存在");
		}
	}

	
	/**@author HCJ
	 * 将各文本内容、暂存的规则清除
	 * @date 2018年10月31日 下午2:23:50
	 */
	public void onResetBtClick() {
		scanTa.setText("");
		scanTa.setDisable(false);
		resultTa.setText("");
		ruleItems.delete(0, ruleItems.length());
		scanTaString = null;
		resultTaString =null;
	}
	

	/**
	 * @author HCJ 规则名称是否有效
	 * @date 2018年10月29日 下午3:32:00
	 */
	private boolean isRuleNameExist() {
		if (ruleNameTf.getText() != null && !ruleNameTf.getText().equals("")) {
			if (rules != null && rules.size() > 0) {
				for (Rule rule : rules) {
					if (rule.getName().equals(ruleNameTf.getText())) {
						return false;
					}
				}
				return true;
			} else {
				return true;
			}
		}
		return false;
	}

	
	private void showSeparatorWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/separatorRule.fxml"));
			Parent root = loader.load();
			SeparatorRuleController separatorRuleController = loader.getController();
			// 显示
			Stage stage = new Stage();
			stage.setAlwaysOnTop(true);
			separatorRuleController.setAddRuleController(AddRuleController.this);
			separatorRuleController.setStage(stage);
			stage.setScene(new Scene(root));
			stage.setTitle("添加分割符条目");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			error("加载添加分割符条目窗口出错");
			logger.error("加载添加分割符条目窗口出错");
		}
	}

	
	private void showLengthWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/lengthRule.fxml"));
			Parent root = loader.load();
			LengthRuleController lengthRuleController = loader.getController();
			// 显示
			Stage stage = new Stage();
			stage.setAlwaysOnTop(true);
			lengthRuleController.setAddRuleController(AddRuleController.this);
			lengthRuleController.setStage(stage);
			stage.setScene(new Scene(root));
			stage.setTitle("添加长度条目");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			error("加载添加长度条目窗口出错");
			logger.error("加载添加长度条目窗口出错");
		}
	}

	
	/**@author HCJ
	 * 判断文本内容是否有效，有效时保存文本
	 * @return true 扫描文本或者结果文本不为空
	 * @return false 扫描文本和结果文本都为空
	 * @date 2018年10月31日 下午6:39:01
	 */
	private Boolean setContentIfExist() {
		if (resultTa.getText() != null && !resultTa.getText().equals("")) {
			resultTaString = resultTa.getText();
			return true;
		} else {
			if (scanTa.getText() != null && !scanTa.getText().equals("")) {
				scanTaString = scanTa.getText();
				return true;
			}
		}
		error("请扫描条码");
		logger.error("请扫描条码");
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
