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
import javafx.stage.Stage;

public class AddRuleController implements Initializable {

	private Logger logger = LogManager.getRootLogger();
	@FXML
	private TextArea contentTa;
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
	public static String contentTaString = null;
	// 存储分割后的文本内容
	public static String resultTaString = null;
	// 存储所有未保存的规则
	private StringBuilder stagingRules = new StringBuilder();
	
	private ManageRuleController manageRuleController;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		contentTa.requestFocus();
	}

	
	public void onCallLength() {
		rules = manageRuleController.getRules();
		if (checkContentIsExist()) {
			contentTa.setDisable(true);
			showLengthWindow();
		}
	}

	
	public void onCallSeparator() {
		rules = manageRuleController.getRules();
		if (checkContentIsExist()) {
			contentTa.setDisable(true);
			showSeparatorWindow();
		}
	}

	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	
	public void setStagingRules(String stagingRule) {
		stagingRules.append(stagingRule);
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
		if (isResultTaContentAvailable && isRuleNameAvailable()) {
			Rule rule = new Rule();
			rule.setName(ruleNameTf.getText());
			rule.setExample(contentTa.getText());
			rule.setAllRules(stagingRules.toString());
			rules.add(rule);
			manageRuleController.saveRules();
			stagingRules.delete(0, stagingRules.length());
			info("保存规则成功");
			stage.close();
		} else {
			error("保存失败，规则名称必须有效、不重复且结果必须存在");
			logger.error("保存失败，规则名称必须有效、不重复且结果必须存在");
		}
	}

	
	public void onCancelBtClick() {
		contentTa.setText("");
		contentTa.setDisable(false);
		resultTa.setText("");
		stagingRules.delete(0, stagingRules.length());
	}
	

	/**
	 * @author HCJ 规则名称是否有效
	 * @date 2018年10月29日 下午3:32:00
	 */
	private boolean isRuleNameAvailable() {
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
			stage.setTitle("添加分隔符条目");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			error("加载添加分隔符规则窗口出错");
			logger.error("加载添加分隔符规则窗口出错");
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
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			error("加载添加长度规则窗口出错");
			logger.error("加载添加长度规则窗口出错");
		}
	}

	
	/**
	 * @author HCJ 校验文本内容
	 * @date 2018年10月29日 下午3:29:40
	 */
	private Boolean checkContentIsExist() {
		if (resultTa.getText() != null && !resultTa.getText().equals("")) {
			resultTaString = resultTa.getText();
			return true;
		} else {
			if (contentTa.getText() != null && !contentTa.getText().equals("")) {
				contentTaString = contentTa.getText();
				return true;
			}
		}
		error("请扫描二维码");
		logger.error("请扫描二维码");
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
