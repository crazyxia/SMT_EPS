package com.jimi.smt.eps.printer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.printer.app.Main;
import com.jimi.smt.eps.printer.entity.RuleResultData;
import com.jimi.smt.eps.printer.entity.Rule;

import cc.darhao.dautils.api.ResourcesUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class ManageRuleController implements Initializable {

	private Logger logger = LogManager.getRootLogger();

	@FXML
	private TableColumn ruleNameCol;
	@FXML
	private TableColumn ruleExampleCol;
	@FXML
	private TableColumn ruleDetailsCol;
	@FXML
	private TableView<RuleResultData> ruleDataTv;
	@FXML
	private Label tipLb;
	@FXML
	private Label versionLb;
	@FXML
	private Button addRuleBt;
	@FXML
	private Button applyRuleBt;
	@FXML
	private Button deleteRuleBt;
	
	private Stage stage;

	// 存储规则文件名
	private static final String RULE_FILE_NAME = "rule.cfg";
	// 表格数据
	private ObservableList<RuleResultData> ruleTableLsit = null;
	// 存储所有规则
	private List<Rule> rules = new ArrayList<Rule>();
	// 存储当前应用的规则
	public static Rule currentRule;
	// 存储在表格中选中的规则
	private Rule selectedRule = new Rule();
	
	private MainController mainController;

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initVersionLb();
		initDatatTV();
		initFileAndRule();
		tableRowChangeListener();
	}

	
	/**
	 * @author HCJ 初始化表格
	 * @date 2018年10月29日 下午3:19:30
	 */
	public void initDatatTV() {
		ruleNameCol.setCellValueFactory(new PropertyValueFactory<RuleResultData, String>("name"));
		ruleExampleCol.setCellValueFactory(new PropertyValueFactory<RuleResultData, String>("example"));
		ruleDetailsCol.setCellValueFactory(new PropertyValueFactory<RuleResultData, String>("details"));
		initcell(ruleNameCol);
		initcell(ruleExampleCol);
		initcell(ruleDetailsCol);
	}

	
	/**
	 * @author HCJ 初始化文件和规则
	 * @date 2018年10月29日 下午3:25:25
	 */
	public void initFileAndRule() {
		// 检查rule.cfg存在与否，不存在则重新创建
		if (!new File(RULE_FILE_NAME).exists()) {
			try {
				new File(RULE_FILE_NAME).createNewFile();
				Rule rule = new Rule();
				rule.setName("默认规则");
				rule.setExample("默认规则例子");
				rule.setDetails("(默认规则：不对文本内容进行处理)");
				rules.add(rule);
				writeToFile();
			} catch (IOException e) {
				e.printStackTrace();
				error("创建配置文件时出现IO错误");
				logger.error("创建配置文件时出现IO错误");
			}
		}
		Object object = readFromFile();
		if (object != null) {
			rules = (List<Rule>) object;
		}	
		updateText();
		if(currentRule == null) {
			currentRule = new Rule();
			currentRule.setName("默认规则");
			currentRule.setExample("默认规则例子");
			currentRule.setDetails("(默认规则：不对文本内容进行处理)");
		}
	}

	
	/**
	 * @author HCJ 初始化表格数据
	 * @date 2018年10月29日 下午3:18:57
	 */
	public void initcell(TableColumn tableColumn) {

		tableColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
			public TableCell<RuleResultData, String> call(TableColumn pColumn) {
				return new TableCell<RuleResultData, String>() {

					@Override
					public void updateItem(String item, boolean empty) {

						super.updateItem(item, empty);
						this.setText(null);
						if (!empty) {
							this.setText(item);
							this.setTextFill(Color.BLACK);
						}
					}
				};
			}
		});
	}

	
	/**@author HCJ
	 * 新增规则
	 * @date 2018年10月31日 下午7:01:29
	 */
	public void onAddRuleBtClick() {
		showAddRuleWindow();
	}

	
	/**
	 * @author HCJ 应用规则
	 * @date 2018年10月29日 下午3:23:09
	 */
	public void onApplyRuleBtClick() {
		if (selectedRule.getName() != null && rules != null && rules.size() > 0) {
			currentRule = selectedRule;
			mainController.setCurrentRule();
			info("应用规则成功");
			stage.close();
		} else {
			error("请选择左边的规则");
			logger.error("请选择左边的规则");
		}
	}

	
	/**
	 * @author HCJ 删除规则
	 * @date 2018年10月29日 下午3:23:26
	 */
	public void onDeleteRuleBtClick() {
		info("");
		if (selectedRule.getName() != null && rules != null && rules.size() > 0) {
			if (selectedRule.getName().equals("默认规则")) {
				error("不能删除默认规则");
				logger.error("不能删除默认规则");
			} else {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("删除规则界面");
				alert.setContentText("确定删除这个规则?");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					rules.removeIf(r -> r.getName().equals(selectedRule.getName()));
					updateText();
					if (selectedRule.getName().equals(currentRule.getName())) {
						currentRule = null;
					}
					mainController.setCurrentRule();
					writeToFile();
				} else {
					alert.close();
				}
			}
		} else {
			error("请选择左边的规则");
			logger.error("请选择左边的规则");
		}
	}

	
	/**
	 * @author HCJ 表格选中行变化监听器
	 * @date 2018年10月29日 下午3:23:42
	 */
	public void tableRowChangeListener() {
		ruleDataTv.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue != null && newValue.intValue() >= 0) {
					if (rules != null && rules.size() > 0) {
						selectedRule = rules.get(newValue.intValue());
					}
				}
			}
		});
	}

	
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}
	
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	
	/**@author HCJ
	 * 将规则写入文件，更新表格的显示内容
	 * @date 2018年10月31日 下午7:02:16
	 */
	public void updateRules() {
		writeToFile();
		updateText();
	}

	
	public List<Rule> getRules() {
		return rules;
	}

	
	/**@author HCJ
	 * 显示新建规则窗口
	 * @date 2018年10月31日 下午7:04:32
	 */
	private void showAddRuleWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/addRule.fxml"));
			Parent root = loader.load();
			AddRuleController addRuleController = loader.getController();
			// 显示
			Stage stage = new Stage();
			stage.setAlwaysOnTop(true);
			addRuleController.setManageRuleController(ManageRuleController.this);
			addRuleController.onResetBtClick();
			addRuleController.setStage(stage);
			stage.setTitle("新建规则");
			stage.setScene(new Scene(root));
			stage.show();
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					addRuleController.onResetBtClick();	
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			error("加载添加规则窗口出错");
			logger.error("加载添加规则窗口出错");
		}
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


	/**
	 * @author HCJ 从文件中读取对象
	 * @date 2018年10月29日 下午3:27:59
	 */
	private Object readFromFile() {
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(RULE_FILE_NAME);
			if (fis.available() != 0) {
				ois = new ObjectInputStream(fis);
				Object object = ois.readObject();
				return object;
			} else {
				info("文件内容为空");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			error("读取文件时找不到对象");
			logger.error("读取文件时找不到对象");
		} catch (IOException e) {
			e.printStackTrace();
			error("读取文件时出现IO错误");
			logger.error("读取文件时出现IO错误");
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

	
	/**
	 * @author HCJ 将对象写入文件中
	 * @date 2018年10月29日 下午3:28:22
	 */
	private void writeToFile() {
		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(RULE_FILE_NAME));
			oos = new ObjectOutputStream(fos);
			oos.writeObject(rules);
		} catch (Exception e) {
			e.printStackTrace();
			error("写入数据时出现错误");
			logger.error("写入数据时出现错误");
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * @author HCJ 更新表格数据
	 * @date 2018年10月29日 下午3:28:44
	 */
	private synchronized void updateText() {
		if (rules != null && rules.size() > 0) {
			ruleTableLsit = FXCollections.observableArrayList(ruleToRuleResultData(rules));
			ruleDataTv.setItems(ruleTableLsit);
		}else {
			ruleDataTv.setItems(null);
		}
	}

	
	/**
	 * @author HCJ 将List<RuleResultData>对象转换为表格可以识别的RuleResultData
	 * @date 2018年10月29日 下午3:29:02
	 */
	private List<RuleResultData> ruleToRuleResultData(List<Rule> rules) {
		List<RuleResultData> ruleResultDatas = new ArrayList<>();
		for (Rule rule : rules) {
			RuleResultData ruleResultData = new RuleResultData();
			ruleResultData.setName(rule.getName());
			ruleResultData.setExample(rule.getExample());
			ruleResultData.setDetails(rule.getDetails());
			ruleResultDatas.add(ruleResultData);
		}
		return ruleResultDatas;
	}


	/**
	 * @author HCJ 版本信息
	 * @date 2018年10月29日 下午3:32:17
	 */
	private void initVersionLb() {
		versionLb.setText("V" + Main.getVersion() + " © 2019 几米物联技术有限公司  All rights reserved.");
	}

}
