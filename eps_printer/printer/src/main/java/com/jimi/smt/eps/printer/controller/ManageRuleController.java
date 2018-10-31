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
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.printer.app.Main;
import com.jimi.smt.eps.printer.entity.ResultData;
import com.jimi.smt.eps.printer.entity.Rule;

import cc.darhao.dautils.api.ResourcesUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.util.Callback;

public class ManageRuleController implements Initializable {

	private Logger logger = LogManager.getRootLogger();

	@SuppressWarnings("rawtypes")
	@FXML
	private TableColumn ruleNameCol;
	@SuppressWarnings("rawtypes")
	@FXML
	private TableColumn ruleExampleCol;
	@SuppressWarnings("rawtypes")
	@FXML
	private TableColumn allRulesCol;
	@FXML
	private TableView<ResultData> DataTV;
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
	
	@SuppressWarnings("unused")
	private Stage stage;

	// 存储规则文件名
	private static final String RULE_FILE_NAME = "rule.cfg";
	// 表格数据
	private ObservableList<ResultData> tableLsit = null;
	// 存储所有规则
	private List<Rule> rules = new ArrayList<Rule>();
	// 刷新表格定时器
	private static Timer updateTimer = new Timer(true);
	// 刷新表格数据线程的启动延时时间
	private static final Integer TIME_DELAY = 0;
	// 刷线表格数据线程的启动间隔时间
	private static final Integer TIME_PERIOD = 3000;
	// 定时器是否更新数据
	private static boolean isUpdate = true;
	// 存储当前应用的规则
	public static Rule currentRule = new Rule();
	// 存储在表格中选中的规则
	private Rule selectedRule = new Rule();

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initVersionLb();
		initDatatTV();
		initFileAndRule();
		tableRowChangeListener();
		// 定时任务：刷新表单
		timeTask();
	}

	
	/**
	 * @author HCJ 初始化表格
	 * @date 2018年10月29日 下午3:19:30
	 */
	@SuppressWarnings({ "unchecked" })
	public void initDatatTV() {
		ruleNameCol.setCellValueFactory(new PropertyValueFactory<ResultData, String>("name"));
		ruleExampleCol.setCellValueFactory(new PropertyValueFactory<ResultData, String>("example"));
		allRulesCol.setCellValueFactory(new PropertyValueFactory<ResultData, String>("allRules"));
		initcell(ruleNameCol);
		initcell(ruleExampleCol);
		initcell(allRulesCol);
	}

	
	/**
	 * @author HCJ 初始化文件和规则
	 * @date 2018年10月29日 下午3:25:25
	 */
	@SuppressWarnings("unchecked")
	public void initFileAndRule() {
		// 检查rule.cfg存在与否，不存在则重新创建
		if (!new File(RULE_FILE_NAME).exists()) {
			try {
				new File(RULE_FILE_NAME).createNewFile();
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
	}

	
	/**
	 * @author HCJ 初始化表格数据
	 * @date 2018年10月29日 下午3:18:57
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initcell(TableColumn tableColumn) {

		tableColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
			public TableCell<ResultData, String> call(TableColumn pColumn) {
				return new TableCell<ResultData, String>() {

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

	
	/**
	 * @author HCJ 初始化定时器任务
	 * @date 2018年10月29日 下午3:19:52
	 */
	public void timeTask() {
		updateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (isUpdate) {
							updateText();
						}
					}
				});
			}
		}, TIME_DELAY, TIME_PERIOD);
	}

	
	public void onCallAddRule() {
		showAddRuleWindow();
	}

	
	/**
	 * @author HCJ 应用规则
	 * @date 2018年10月29日 下午3:23:09
	 */
	public void onApplyRuleBtClick() {
		if (selectedRule.getName() != null && rules != null && rules.size() > 0) {
			currentRule = selectedRule;
			info("应用规则成功");
			stage.close();
		} else {
			error("请选择左边的规则");
			logger.error("请选择左边的规则");
		}
		isUpdate = true;
	}

	
	/**
	 * @author HCJ 删除规则
	 * @date 2018年10月29日 下午3:23:26
	 */
	public void onDeleteRuleBtClick() {
		if (selectedRule.getName() != null && rules != null && rules.size() > 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("删除规则界面");
			alert.setContentText("确定删除这个规则?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				rules.removeIf(r -> r.getName().equals(selectedRule.getName()));
				currentRule = null;
				writeToFile();
			} else {
				alert.close();
			}
		} else {
			error("请选择左边的规则");
			logger.error("请选择左边的规则");
		}
		isUpdate = true;
	}

	
	/**
	 * @author HCJ 表格选中行变化监听器
	 * @date 2018年10月29日 下午3:23:42
	 */
	public void tableRowChangeListener() {
		DataTV.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue != null && newValue.intValue() >= 0) {
					isUpdate = false;
					if (rules != null && rules.size() > 0) {
						selectedRule = rules.get(newValue.intValue());
					}
				}
			}
		});
	}

	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	
	public void saveRules() {
		writeToFile();
	}

	
	public List<Rule> getRules() {
		return rules;
	}

	
	private void showAddRuleWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/addRule.fxml"));
			Parent root = loader.load();
			AddRuleController addRuleController = loader.getController();
			// 显示
			Stage stage = new Stage();
			stage.setAlwaysOnTop(true);
			addRuleController.setManageRuleController(ManageRuleController.this);
			addRuleController.setStage(stage);
			stage.setScene(new Scene(root));
			stage.show();
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
			tableLsit = FXCollections.observableArrayList(ruleToResultData(rules));
			DataTV.setItems(tableLsit);
		}
	}

	
	/**
	 * @author HCJ 将List对象转换为表格可以识别的ResultData
	 * @date 2018年10月29日 下午3:29:02
	 */
	private List<ResultData> ruleToResultData(List<Rule> rules) {
		List<ResultData> resultDatas = new ArrayList<>();
		for (Rule rule : rules) {
			ResultData resultData = new ResultData();
			resultData.setName(rule.getName());
			resultData.setExample(rule.getExample());
			resultData.setAllRules(rule.getAllRules());
			resultDatas.add(resultData);
		}
		return resultDatas;
	}


	/**
	 * @author HCJ 版本信息
	 * @date 2018年10月29日 下午3:32:17
	 */
	private void initVersionLb() {
		versionLb.setText("V" + Main.getVersion() + " © 2018 几米物联技术有限公司  All rights reserved.");
	}

}
