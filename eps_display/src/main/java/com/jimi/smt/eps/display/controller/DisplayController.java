package com.jimi.smt.eps.display.controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.CloseReason.CloseCodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.bude.finger.Finger;
import com.jimi.bude.finger.config.FingerConfig;
import com.jimi.bude.finger.inter.CallBackReplyCallBack;
import com.jimi.bude.finger.inter.ConnectCallBack;
import com.jimi.bude.finger.inter.ExceptionCallBack;
import com.jimi.bude.finger.inter.LoginReplyCallBack;
import com.jimi.bude.finger.inter.UpdateCallBack;
import com.jimi.bude.finger.pack.CallBackReplyPackage;
import com.jimi.bude.finger.pack.LoginPackage;
import com.jimi.bude.finger.pack.LoginReplyPackage;
import com.jimi.bude.finger.pack.UpdatePackage;
import com.jimi.smt.eps.display.app.Main;
import com.jimi.smt.eps.display.constant.BoardResetReson;
import com.jimi.smt.eps.display.entity.vo.ConfigVO;
import com.jimi.smt.eps.display.entity.BoardResetInfo;
import com.jimi.smt.eps.display.entity.Line;
import com.jimi.smt.eps.display.entity.ProgramItemVisit;
import com.jimi.smt.eps.display.entity.ResultData;
import com.jimi.smt.eps.display.util.HttpHelper;
import com.jimi.smt.eps.display.util.IniReader;
import com.jimi.smt.eps.display.util.IpHelper;

import cc.darhao.dautils.api.BytesParser;
import cc.darhao.dautils.api.TextFileUtil;

import com.jimi.smt.eps.display.websocket.DisplayClientSocket;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import okhttp3.Call;
import okhttp3.Response;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class DisplayController implements Initializable {

	// 刷新表格数据线程的启动延时时间
	private static final Integer TIME_DELAY = 0;
	// 刷新表格数据周期
	private static Integer timePeriod;
	// 默认结果
	private static final Integer DEFAULT_RESULT = 2;

	private static final boolean IS_NETWORK = true;

	private static final Integer HALF = 2;

	private static final Integer WORKORDERCB_MARGIN_LEFT = 85;

	private static final Integer DEFAULT_CB_TEXTSIZE = 20;

	private static final Integer DEFAULT_LB_TEXTSIZE = 22;

	private static final Integer DEFAULT_LB_WIDTH = 260;

	private static final Integer RESULT_MARGIN_RIGHT = 20;

	private static final Integer DEFAULT_LB_MARGIN_LEFT = 20;

	private static final Integer DEFAULT_LINECB_WIDTH = 115;

	private static final Integer DEFAULT_HALF_WIDTH = 400;

	private static final Integer DEAFULT_BOARDTYPECB_WIDTH = 125;

	private static final Integer DEFAULT_WORKORDERCB_WIDTH = 315;

	private static final Integer DEFAULT_LINECB_REMAIN = 300;

	private static final Integer DEFAULT_OPERATORLB_WIDTH = 110;

	private static final Integer DEFAULT_OPERATORNAMELB_WIDTH = 100;

	private static final Integer DEFAULE_BORDER_LB_MARGIN_LEFT = 130;

	private static final Integer COLUMN = 7;

	private static final Integer DEFAULT_TABLE_CELL_WIDTH = 111;

	private static final Integer RESETBT_AND_MARGIN_RIGTH = 140;

	private static final Integer TABLE_MARGIN = 18;

	private static final Integer DEFAULT_TABLE_REMAIN = 47;

	private static final Integer DEFAULT_CLOUMN_TEXTSIZE = 18;

	private static final Integer HALF_MIDDLE_INTERVAL = 10;

	private static final Integer FACTOR_1 = 10;

	private static final Integer FACTOR_2 = 20;

	private static final Integer FACTOR_3 = 40;

	private static final Integer FACTOR_4 = 4;

	private static final Integer FACTOR_5 = 50;

	private static final Integer FACTOR_6 = 200;

	private static final Integer FACTOR_7 = 12;

	private static final Integer ONE_SECOND_TO_MILLISECOND = 1000;

	private static final Integer CONSTANT_FOR_CHECK_RESULT = 30000;

	private static final Integer CONSTANT_FOR_CHECK_ALL_RESULT = 40000;
	// 定时器是否更新数据
	private static boolean isUpdate = false;
	// 不需要填充
	private static int senceWidth = 0;
	@FXML
	private Label lineLb;
	@FXML
	private Label operatorNameLb;
	@FXML
	private Label workOrderLb;
	@FXML
	private Label boardTybeLb;
	@FXML
	private Label lineseatNameLb;
	@FXML
	private Label scanLineseatNameLb;
	@FXML
	private Label materialNoNameLb;
	@FXML
	private Label scanMaterialNoNameLb;
	/// 需填充下拉框
	@FXML
	private ComboBox<String> lineCb;
	@FXML
	private ComboBox<String> workOrderCb;
	@FXML
	private ComboBox<String> boardTybeCb;
	// 需填充标签label
	@FXML
	private Label operatorLb;
	@FXML
	private Label lineseatLb;
	@FXML
	private Label scanLineseatLb;
	@FXML
	private Label materialNoLb;
	@FXML
	private Label scanMaterialNoLb;
	@FXML
	private Label typeLb;
	@FXML
	private Label resultLb;
	@FXML
	private Label versionLb;
	@FXML
	private Button resetBt;

	@FXML
	private TableView<ResultData> DataTv;
	@FXML
	private TableColumn lineseatCl;
	@FXML
	private TableColumn storeIssueResultCl;
	@FXML
	private TableColumn feedResultCl;
	@FXML
	private TableColumn changeResultCl;
	@FXML
	private TableColumn checkResultCl;
	@FXML
	private TableColumn checkAllResultCl;
	@FXML
	private TableColumn firstCheckAllResultCl;

	// http连接
	HttpHelper httpHelper = new HttpHelper();

	// 刷新表格定时器
	private static Timer updateTimer = new Timer(true);
	// 表格数据
	private ObservableList<ResultData> tableList = FXCollections.observableArrayList();
	// 日志记录
	private Logger logger = LogManager.getRootLogger();

	// 板子数量重置信息
	BoardResetInfo boardResetInfo = new BoardResetInfo();

	private List<String> lines = null;
	private List<Line> lineList = null;

	// http请求返回的结果
	private String result;

	private List<ProgramItemVisit> programItemVisits;
	// 操作员
	private String operator;

	// Config配置项
	private static final String CHECK_ALL_CYCLE_TIME = "check_all_cycle_time";
	private static final String CHECK_AFTER_CHANGE_TIME = "check_after_change_time";
	private static final String CONFIG_FILE = "config.ini";
	private static final String CONTROL_ID_RECORD_FILE = "controlIdRecord.txt";
	private static final String VERSION_FILE = "version.txt";

	// 重置请求
	private static final String RESET_ACTION = "program/reset";
	// 选择当前工单请求
	private static final String SWITCH_ACTION = "program/switch";
	// 查询所有产线名称请求
	private static final String GET_LINE_ACTION = "line/selectLine";
	// 查询所有产线请求
	private static final String GET_ALLLINE_ACTION = "line/selectAll";
	// 查询中控对象请求
	private static final String GET_CENTERLOGIN_ACTION = "login/selectById";
	// 查询工单请求
	private static final String GET_WORKORDER_ACTION = "program/selectWorkingOrder";
	// 查询版面类型请求
	private static final String GET_BOARDTYPE_ACTION = "program/selectWorkingBoardType";
	// 查询操作员请求
	private static final String GET_OPERATOR_ACTION = "program/selectLastOperatorByProgram";
	// 查询工单操作请求
	private static final String GET_ITEMVISIT_ACTION = "program/selectItemVisitByProgram";
	// 查询产线配置请求
	private static final String GET_CONFIG_ACTION = "config/list";
	// 查询服务器时间戳请求
	private static final String GET_TIMESTAMP_ACTION = "program/getTimesTamp";
	// 查询产线是否处于被监控状态
	private static final String GET_LINE_MONITORING_STATE = "program/isLineMonitored";
	
	// 包ID
	static short packageId = 0;
	// 更新成功
	private static final int UPDATE_FINGER_SUCCEED = 26;
	// 包ID默认值
	private static final int DEFAULT_PACKAGE_ID = 0;
	// 类型为设备端
	private static final int FINGER_TYPE = 1;

	private Finger finger;

	// 连接到websocket服务端线程
	private Runnable connectToServerRunnable = null;
	private Thread connectToServerThread = null;
	private Session session = null;

	/**
	 * ERROR_STATE : 错误状态
	 */
	private static final Integer ERROR_STATE = 0;
	/**
	 * SUCCESS_STATE : 成功状态
	 */
	private static final Integer SUCCESS_STATE = 1;
	/**
	 * INITIAL_STATE : 初始状态
	 */
	private static final Integer INITIAL_STATE = 2;
	/**
	 * OVERTIME_STATE : 超时状态
	 */
	private static final Integer OVERTIME_STATE = 3;
	/**
	 * WAIT_CHECK_STATE : 待核料状态
	 */
	private static final Integer WAIT_CHECK_STATE = 4;
	
	/**
	 * TIMESTAMP_PARAMETER : 时间戳参数
	 */
	private static final Integer TIMESTAMP_PARAMETER = 500000;

	/**
	 * dateFormat : 时间格式化
	 */
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");

	/**
	 * requestErrorAlert : 请求错误提示窗口
	 */
	private Alert requestErrorAlert = new Alert(AlertType.ERROR);

	/**
	 * FONT_SIZE : 中控断线提示信息字体大小
	 */
	private static final Integer FONT_SIZE = 26;
	/**
	 * TEXTAREA_MAX_WIDTH : 中控断线文本提示框的最大宽度
	 */
	private static final Integer TEXTAREA_MAX_WIDTH = 600;
	/**
	 * TEXTAREA_MAX_HEIGHT : 中控断线文本提示框的最大高度
	 */
	private static final Integer TEXTAREA_MAX_HEIGHT = 200;

	/**
	 * DEFAULT_TIMEPERIOD_AND_RECONNECT_CYCLE : 默认刷新表格数据周期和websocket重连睡眠时间
	 */
	private static final Integer DEFAULT_TIMEPERIOD_AND_RECONNECT_CYCLE = 5000;
	
	/**
	 * centerOfflineAlert : 中控下线提示弹窗
	 */
	private static Alert centerOfflineAlert = new Alert(AlertType.INFORMATION);

	/**
	 * selectedLine : 选中的产线名称
	 */
	private static String selectedLineName = "";


	public void initialize(URL arg0, ResourceBundle arg1) {
		initTimePeriod();
		initVersion();
		initVersionLb();
		initlineCb();
		initDatatTV();
		resetBtListener();
		lineCbChangeListener();
		workOrderChangeListener();
		boardTypeChangeListener();
		// 定时任务：刷新表单
		timeTask();
		// 初始化websocket客户端
		initWebsocketClient();
		// 设备端初始化
		/* initFinger(); */
	}


	/**@author HCJ
	 * 初始化线号选择框
	 * @date 2019年3月20日 上午10:07:16
	 */
	public void initlineCb() {
		lines = JSON.parseArray(sendRequest(GET_LINE_ACTION, null), String.class);
		lineList = JSON.parseArray(sendRequest(GET_ALLLINE_ACTION, null), Line.class);
		ObservableList<String> lineList = FXCollections.observableArrayList(lines);
		lineCb.getItems().clear();
		lineCb.setItems(lineList);
	}

	
	/**
	 * 初始化表格
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initDatatTV() {
		DataTv.setItems(tableList);
		lineseatCl.setCellValueFactory(new PropertyValueFactory<ResultData, String>("lineseat"));
		storeIssueResultCl.setCellValueFactory(new PropertyValueFactory<ResultData, Integer>("storeIssueResult"));
		feedResultCl.setCellValueFactory(new PropertyValueFactory<ResultData, Integer>("feedResult"));
		changeResultCl.setCellValueFactory(new PropertyValueFactory<ResultData, Integer>("changeResult"));
		checkResultCl.setCellValueFactory(new PropertyValueFactory<ResultData, Integer>("checkResult"));
		checkAllResultCl.setCellValueFactory(new PropertyValueFactory<ResultData, Integer>("checkAllResult"));
		firstCheckAllResultCl.setCellValueFactory(new PropertyValueFactory<ResultData, Integer>("firstCheckAllResult"));
		lineseatCl.setCellFactory(new Callback<TableColumn, TableCell>() {
			public TableCell<ResultData, String> call(TableColumn pColumn) {
				return new TableCell<ResultData, String>() {

					@Override
					public void updateItem(String item, boolean empty) {

						super.updateItem(item, empty);
						this.setStyle("-fx-font-size: " + ((((senceWidth - TABLE_MARGIN) / COLUMN - DEFAULT_TABLE_CELL_WIDTH) / FACTOR_7) + DEFAULT_LB_TEXTSIZE) + "px;" + "	-fx-alignment: center;" + "	-fx-font-family:'STXiHei';" + "	-fx-font-weight: bold;");
						this.setText(item);
					}
				};
			}
		});
		initcell(storeIssueResultCl);
		initcell(feedResultCl);
		initcell(changeResultCl);
		initcell(checkResultCl);
		initcell(checkAllResultCl);
		initcell(firstCheckAllResultCl);
	}

	
	/**
	 * 初始化表格中某一列的每个单元格
	 * 
	 * @param tableColumn 列
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initcell(TableColumn tableColumn) {

		tableColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
			public TableCell<ResultData, Integer> call(TableColumn pColumn) {
				return new TableCell<ResultData, Integer>() {

					@Override
					public void updateItem(Integer item, boolean empty) {

						super.updateItem(item, empty);
						this.setStyle("-fx-font-size:" + ((((senceWidth - TABLE_MARGIN) / COLUMN - DEFAULT_TABLE_CELL_WIDTH) / FACTOR_7) + DEFAULT_LB_TEXTSIZE) + "px;" + "	-fx-alignment: center;" + "	-fx-font-family:'STXiHei';" + "	-fx-font-weight: bold;");
						this.setText(null);
						if (!empty) {
							if (item.toString().equals("0")) {
								this.setText("×");
								this.setTextFill(Color.RED);
							} else if (item.toString().equals("1")) {
								this.setText("●");
								this.setTextFill(Color.GREEN);
							} else if (item.toString().equals("2")) {
								this.setText("○");
								this.setTextFill(Color.BLUE);
							} else if (item.toString().equals("3")) {
								this.setText("×");
								this.setTextFill(Color.ORANGE);
							} else if (item.toString().equals("4")) {
								this.setText("◎");
								this.setTextFill(Color.PURPLE);
							} else if ((CONSTANT_FOR_CHECK_RESULT < item.intValue() || CONSTANT_FOR_CHECK_RESULT.equals(item)) && item.intValue() < CONSTANT_FOR_CHECK_ALL_RESULT) {
								this.setText(getCountDownTime(item - CONSTANT_FOR_CHECK_RESULT));
								if ((item.intValue() < CONSTANT_FOR_CHECK_RESULT + FACTOR_1) || item.equals(CONSTANT_FOR_CHECK_RESULT + FACTOR_1)) {
									this.setTextFill(Color.RED);
								} else {
									this.setTextFill(Color.GREEN);
								}
							} else if ((CONSTANT_FOR_CHECK_ALL_RESULT < item.intValue() || item.equals(CONSTANT_FOR_CHECK_ALL_RESULT)) && item.intValue() < TIMESTAMP_PARAMETER) {
								this.setText(getCountDownTime(item - CONSTANT_FOR_CHECK_ALL_RESULT));
								if (item.intValue() < CONSTANT_FOR_CHECK_ALL_RESULT + FACTOR_1 || item.equals(CONSTANT_FOR_CHECK_ALL_RESULT + FACTOR_1)) {
									this.setTextFill(Color.RED);
								} else {
									this.setTextFill(Color.GREEN);
								}
							} else {
								// 显示上一次换料时间
								long timeStamp = item.intValue();
								this.setText(dateFormat.format(new Date(timeStamp * ONE_SECOND_TO_MILLISECOND)));
								this.setTextFill(Color.GREEN);
							}
						}
					}
				};
			}
		});
	}

	
	/**
	 * 初始化定时器任务
	 */
	public void timeTask() {
		updateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				String line = lineCb.getSelectionModel().getSelectedItem() == null ? "" : lineCb.getSelectionModel().getSelectedItem().toString();
				String workOrder = workOrderCb.getSelectionModel().getSelectedItem() == null ? "" : workOrderCb.getSelectionModel().getSelectedItem().toString();
				String boardType = boardTybeCb.getSelectionModel().getSelectedItem() == null ? "" : boardTybeCb.getSelectionModel().getSelectedItem().toString();
				if (!line.equals("") && !workOrder.equals("") && !boardType.equals("")) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							if (isUpdate) {
								updateText();
							}
						}
					});
				}
			}
		}, TIME_DELAY, timePeriod);
	}

	
	/**
	 * 线号选择框文本内容变更监听器
	 */
	public void lineCbChangeListener() {
		lineCb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue != null && newValue.intValue() >= 0) {
					Map<String, String> map = new HashMap<>();
					map.put("lineId", lineList.get(newValue.intValue()).getId().toString());
					try {
						String result = httpHelper.requestHttp(GET_LINE_MONITORING_STATE, map);
						if (result != null && !"".equals(result) && result.equals("{\"result\":\"succeed\"}")) {
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									isUpdate = false;
									new Alert(AlertType.INFORMATION, "该线号已经被使用，请选择其他产线进行查看", ButtonType.OK).showAndWait();
									lineCb.getSelectionModel().clearSelection();
									workOrderCb.getItems().clear();
									boardTybeCb.getItems().clear();
									clearText();
								}
							});
						} else {
							selectedLineName = lineCb.getItems().get(newValue.intValue());
							resetWorkOrderCb(selectedLineName);
							// 根据产线设置板子数量重置信息
							boardResetInfo.setLine(lineList.get(newValue.intValue()).getId());
							boardResetInfo.setBoardResetReson(BoardResetReson.WORK_ORDER_RESTART);
							logger.info(" 产线名称: " + selectedLineName);
							clearText();
						}
					} catch (IOException e) {
						httpFail(GET_LINE_MONITORING_STATE, IS_NETWORK, null);
						e.printStackTrace();
					}

				}
			}
		});
	}

	
	/**
	 * 工单选择框文本内容变更监听器
	 */
	public void workOrderChangeListener() {

		workOrderCb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue != null && newValue.intValue() >= 0) {
					String line = lineCb.getSelectionModel().getSelectedItem() == null ? "" : lineCb.getSelectionModel().getSelectedItem().toString();
					String workOrder = workOrderCb.getItems().get(newValue.intValue());
					if (!line.equals("") && !workOrder.equals("")) {
						Map<String, String> map = new HashMap<>();
						map.put("line", line);
						map.put("workOrder", workOrder);
						String response = sendRequest(GET_BOARDTYPE_ACTION, map);
						if (!response.equals("[]")) {
							List<String> boardTypes = JSON.parseArray(response, String.class);
							ObservableList<String> boardTybeList = FXCollections.observableArrayList(getBoardType(boardTypes));
							boardTybeCb.getItems().clear();
							boardTybeCb.setItems(boardTybeList);
							clearText();
						} else {
							httpFail(GET_BOARDTYPE_ACTION, !IS_NETWORK, null);
						}
					}
				}
			}
		});

	}

	
	/**
	 * 板面类型选择框文本内容变更监听器
	 */
	public void boardTypeChangeListener() {
		boardTybeCb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue != null && newValue.intValue() >= 0) {
					String line = lineCb.getSelectionModel().getSelectedItem() == null ? "" : lineCb.getSelectionModel().getSelectedItem().toString();
					String workOrder = workOrderCb.getSelectionModel().getSelectedItem() == null ? "" : workOrderCb.getSelectionModel().getSelectedItem().toString();
					String boardType = boardTybeCb.getItems().get(newValue.intValue());
					if (!line.equals("") && !workOrder.equals("") && boardType != null && !boardType.equals("")) {
						Integer boardTypeNo = getBoardTypeNo(boardType);
						setDisableCb(true);
						resetBt.setDisable(true);
						isUpdate = false;
						// 发送板子数量重置信息
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								if (session != null && session.isOpen()) {
									try {
										session.getBasicRemote().sendText(JSONObject.toJSONString(boardResetInfo));
										// logger.info("发送重置信息：" + JSONObject.toJSONString(boardResetInfo));
									} catch (IOException e) {
										logger.error("发送板子数量重置信息失败" + " | " + e.getMessage());
										new Alert(AlertType.ERROR, "发送板子数量重置信息失败，请查看网络，并重启软件", ButtonType.OK).showAndWait();
									}
								}
							}
						});
						// 发送选择工单请求
						Map<String, String> map = new HashMap<>();
						map.put("line", line);
						map.put("workOrder", workOrder);
						map.put("boardType", boardTypeNo.toString());
						try {
							httpHelper.requestHttp(SWITCH_ACTION, map, new okhttp3.Callback() {

								@Override
								public void onResponse(Call call, Response response) throws IOException {
									if (response.body().string().equals("{\"result\":\"succeed\"}")) {
										Platform.runLater(new Runnable() {
											@Override
											public void run() {
												isUpdate = true;
												setDisableCb(false);
												resetBt.setDisable(false);
												updateText();
											}
										});
									} else {
										httpFail(SWITCH_ACTION, !IS_NETWORK, line);
									}
								}

								@Override
								public void onFailure(Call call, IOException e) {
									httpFail(SWITCH_ACTION, IS_NETWORK, line);
									e.printStackTrace();
								}
							});
						} catch (IOException e) {
							httpFail(SWITCH_ACTION, IS_NETWORK, line);
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	
	/**
	 * 重置按钮监听器
	 */
	public void resetBtListener() {
		resetBt.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String boardType = boardTybeCb.getSelectionModel().getSelectedItem() == null ? "" : boardTybeCb.getSelectionModel().getSelectedItem().toString();
				if (boardType.equals("") || !isUpdate) {
					new Alert(AlertType.INFORMATION, "你还没有选定工单信息", ButtonType.OK).show();
				} else {
					Optional<ButtonType> optional = new Alert(AlertType.WARNING, "你确定要重置该工单的状态吗？\n这会初始化除发料以外的所有状态", ButtonType.YES, ButtonType.CANCEL).showAndWait();
					if (optional != null && optional.get().equals(ButtonType.YES)) {
						try {
							Map<String, String> map = new HashMap<>();
							String line = lineCb.getSelectionModel().getSelectedItem().toString();
							String workOrder = workOrderCb.getSelectionModel().getSelectedItem().toString();
							Integer boardTypeNo = getBoardTypeNo(boardType);
							map.put("line", line);
							map.put("workOrder", workOrder);
							map.put("boardType", boardTypeNo.toString());
							setDisableCb(true);
							resetBt.setDisable(true);
							httpHelper.requestHttp(RESET_ACTION, map, new okhttp3.Callback() {

								@Override
								public void onResponse(Call call, Response response) throws IOException {
									if (response != null && response.body().string().equals("{\"result\":\"succeed\"}")) {
										Platform.runLater(new Runnable() {
											@Override
											public void run() {
												setDisableCb(false);
												resetBt.setDisable(false);
												updateText();
											}
										});
									} else {
										httpFail(RESET_ACTION, !IS_NETWORK, null);
									}
								}

								@Override
								public void onFailure(Call call, IOException e) {
									httpFail(RESET_ACTION, IS_NETWORK, null);
									e.printStackTrace();
								}
							});

						} catch (Exception e) {
							httpFail(RESET_ACTION, IS_NETWORK, null);
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	
	/**
	 * 关闭程序事件
	 * 
	 * @param primaryStage
	 */
	public void closeWindow(Stage primaryStage) {
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				/*finger.stop();*/
				String line = lineCb.getSelectionModel().getSelectedItem() == null ? "" : lineCb.getSelectionModel().getSelectedItem().toString();
				if (!line.equals("")) {
					if (session != null && session.isOpen()) {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								try {
									session.getBasicRemote().sendText(JSONObject.toJSONString(boardResetInfo));
									// logger.info("发送重置信息：" + JSONObject.toJSONString(boardResetInfo));
									session.close();
								} catch (IOException e) {
									logger.error("关闭时发送重置信息失败：" + e.getMessage());
								}
							}
						});
					}
					sendHttpCloseRequest(line);
				} else {
					if (session != null && session.isOpen()) {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								try {
									session.close();
								} catch (IOException e) {
									logger.error("关闭session时出错：" + e.getMessage());
								}
							}
						});
					}
				}
			}
		});
	}

	
	/**
	 * 根据窗口大小调整界面控件大小
	 * 
	 * @param primarystage
	 */
	public void scenceChangeListener(Stage primarystage) {
		primarystage.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				senceWidth = newValue.intValue();
				// 改变工单下拉框和板面类型下拉框的宽度和字体大小
				changeCbSize(senceWidth);
				// 调整文本框控件的大小和字体大小
				changeLbSize(senceWidth);
				// 调整重置工单按钮的位置
				resetBt.setLayoutX(senceWidth / HALF - RESETBT_AND_MARGIN_RIGTH);
				// 调整操作员文本框的大小和位置
				changeOperatorSize(senceWidth);
				// 调整表格的每一列的大小和字体大小
				ObservableList<TableColumn<ResultData, ?>> Columns = DataTv.getColumns();
				for (TableColumn<ResultData, ?> tableColumn : Columns) {
					tableColumn.setMinWidth((senceWidth - DEFAULT_TABLE_REMAIN) / COLUMN);
					tableColumn.setMaxWidth((senceWidth - DEFAULT_TABLE_REMAIN) / COLUMN);
					tableColumn.setStyle("-fx-font-size:" + ((((senceWidth - TABLE_MARGIN) / COLUMN - DEFAULT_TABLE_CELL_WIDTH) / FACTOR_2) + DEFAULT_CLOUMN_TEXTSIZE) + "px;" + "	-fx-alignment: center;" + "	-fx-font-family:'Microsoft YaHei';" + "	-fx-font-weight: bolder;");
				}
				// 调整单元格的字体大小
				if (isUpdate) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							updateText();

						}
					});
				}
			}
		});
	}


	/**
	 * @author HCJ 初始化设备端，开启连接
	 * @date 2019年1月18日 下午3:02:43
	 */
	public void initFinger() {
		Map<String, String> fingerSetting = IniReader.getItem(CONFIG_FILE, "finger");
		String remoteIp = fingerSetting.get("remoteIp");
		Integer port = Integer.parseInt(fingerSetting.get("port"));
		String armName = fingerSetting.get("armName");
		String fingerName = fingerSetting.get("fingerName");
		String downloadPath = System.getProperty("user.dir");
		String downloadURL = fingerSetting.get("downloadURL");
		String version = "";
		try {
			version = TextFileUtil.readFromFile(VERSION_FILE);
		} catch (IOException e1) {
			logger.error("版本文件读写出现错误");
			e1.printStackTrace();
		}
		FingerConfig config = new FingerConfig(fingerName, version, downloadURL, downloadPath);
		finger = new Finger(remoteIp, port, 20000, config);
		config.setConnectCallBack(new ConnectCallBack() {

			public void onConnect(Finger session) {
				LoginPackage login = new LoginPackage();
				login.setArmName(armName);
				// 发送前需将ip转化成16进制
				String ip = BytesParser.parseBytesToHexString((BytesParser.parseXRadixStringToBytes(IpHelper.getWindowsLocalIp().replace(".", " "), 10)));
				login.setFingerIp(ip);
				login.setFingerName(fingerName);
				login.setType(1);
				login.setPackageId(0);
				// 发送登录包
				session.sendLoginPackage(login);
			}
		});
		config.setLoginReplyCallBack(new LoginReplyCallBack() {

			public void onReplyArrived(LoginReplyPackage r, Finger session) {
				if (r.getResultCode() != 20) {
					logger.error("--------------登录失败------------------");
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						logger.error("登陆失败时休眠线程出现异常");
						e.printStackTrace();
					}
					LoginPackage login = new LoginPackage();
					login.setArmName(armName);
					String ip = BytesParser.parseBytesToHexString((BytesParser.parseXRadixStringToBytes(IpHelper.getWindowsLocalIp().replace(".", " "), 10)));
					login.setFingerIp(ip);
					login.setFingerName(fingerName);
					login.setType(1);
					login.setPackageId(genPackageId());
					// 发送登录包
					session.sendLoginPackage(login);
				} else {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							new Alert(AlertType.INFORMATION, "登录中转端成功", ButtonType.OK).show();
							try {
								File controlIdRecord = new File(CONTROL_ID_RECORD_FILE);
								if (!controlIdRecord.exists()) {
									controlIdRecord.createNewFile();
								}
								String controlId = TextFileUtil.readFromFile(CONTROL_ID_RECORD_FILE);
								if (controlId != null && !"".equals(controlId)) {
									finger.sendCallBackPackage(Integer.parseInt(controlId), DEFAULT_PACKAGE_ID, UPDATE_FINGER_SUCCEED, fingerName, FINGER_TYPE);
									TextFileUtil.writeToFile(CONTROL_ID_RECORD_FILE, "");
								}
							} catch (IOException e) {
								logger.error("读写controlIdRecord配置文件出错");
								e.printStackTrace();
							}
						}
					});
				}
			}
		});
		// 配置接收到更新CallBack回复包后执行的方法
		config.setUpdateCallBack(new UpdateCallBack() {

			@Override
			public void onPackageArrvied(UpdatePackage r, Finger session) {
				logger.info("接收到更新包, " + "信息序列号：" + session.getSerialNo() + "MD5 : " + r.getMd5());
				try {
					Integer ControllId = r.getControllId();
					TextFileUtil.writeToFile("controlIdRecord.txt", ControllId.toString());
					if (!new File("update.bat").exists()) {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								new Alert(AlertType.ERROR, "远程更新时找不到update.bat文件，请联系工程师", ButtonType.OK).show();
							}
						});
					} else {
						Thread.sleep(5000);
						finger.stop();
						Runtime.getRuntime().exec("cmd /k start .\\update.bat");
						System.exit(0);
					}
				} catch (IOException e) {
					logger.error("版本文件写入出错");
					e.printStackTrace();
				} catch (InterruptedException e) {
					logger.error("更新时线程休眠出错");
					e.printStackTrace();
				}
			}
		});
		// 配置接收到CallBack回复包后执行的方法
		config.setCallBackReplyCallBack(new CallBackReplyCallBack() {

			public void onReplyArrived(CallBackReplyPackage r, Finger session) {
				logger.info("收到CallBack回复包，设备端名称：" + r.getFingerName());
			}
		});
		// 配置捕获到异常后执行的方法
		config.setExceptionCallBack(new ExceptionCallBack() {

			@Override
			public void onCathcException(Exception e, Finger session) {
				e.printStackTrace();
				logger.error("与中转端连接过程出现异常 | " + e.getMessage());
			}
		});
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				finger.start();
			}
		});
	}


	/**@author HCJ
	 * 生成packageId
	 * @date 2019年1月21日 上午11:14:42
	 */
	public synchronized static Short genPackageId() {
		packageId %= 65536;
		packageId++;
		return packageId;
	}

	
	private void initVersionLb() {
		versionLb.setText("V" + Main.getVersion() + " © 2019 几米物联技术有限公司  All rights reserved.");
	}


	/**
	 * 发送http请求失败
	 * 
	 * @param action    http请求类型
	 * @param isNetwork 是否是网络问题
	 * @param line      线号
	 */
	private void httpFail(String action, boolean isNetwork, String line) {

		if (action.equals(SWITCH_ACTION)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {

					if (isNetwork && !requestErrorAlert.isShowing()) {
						logger.error("选定工单失败，switch请求失败，网络连接出错");
						requestErrorAlert.setContentText("选定工单失败，请检查你的网络连接");
						requestErrorAlert.showAndWait();
					} else if (!requestErrorAlert.isShowing()) {
						logger.error("选定工单失败，服务器内部错误");
						requestErrorAlert.setContentText("选定工单失败，服务器内部错误");
						requestErrorAlert.showAndWait();
					}
					setDisableCb(false);
					resetBt.setDisable(false);
					resetWorkOrderCb(line);

				}
			});
			return;
		}
		if (action.equals(RESET_ACTION)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (isNetwork && !requestErrorAlert.isShowing()) {
						logger.error("重置工单失败，reset请求失败，网络连接出错");
						requestErrorAlert.setContentText("重置工单失败，请检查你的网络连接");
						requestErrorAlert.showAndWait();
					} else if (!requestErrorAlert.isShowing()) {
						logger.error("重置工单失败，服务器内部原因");
						requestErrorAlert.setContentText("重置工单失败，服务器内部错误");
						requestErrorAlert.showAndWait();
					}
					setDisableCb(false);
					resetBt.setDisable(false);

				}
			});
			return;
		}
		if (action.equals(GET_LINE_ACTION)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (isNetwork && !requestErrorAlert.isShowing()) {
						logger.error("查询所有产线名称失败，select_line请求失败，网络连接出错");
						requestErrorAlert.setContentText("查询所有产线名称失败，请检查你的网络连接");
						requestErrorAlert.showAndWait();
					} else if (!requestErrorAlert.isShowing()) {
						logger.error("查询所有产线名称失败，服务器内部原因");
						requestErrorAlert.setContentText("查询所有产线名称失败，服务器内部错误");
						requestErrorAlert.showAndWait();
					}
					setDisableCb(false);
					resetBt.setDisable(false);

				}
			});
			return;
		}
		if (action.equals(GET_ALLLINE_ACTION)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (isNetwork && !requestErrorAlert.isShowing()) {
						logger.error("查询所有产线失败，select_all请求失败，网络连接出错");
						requestErrorAlert.setContentText("查询所有产线失败，请检查你的网络连接");
						requestErrorAlert.showAndWait();
					} else if (!requestErrorAlert.isShowing()) {
						logger.error("查询所有产线失败，服务器内部原因");
						requestErrorAlert.setContentText("查询所有产线失败，服务器内部错误");
						requestErrorAlert.showAndWait();
					}
					setDisableCb(false);
					resetBt.setDisable(false);

				}
			});
			return;
		}
		if (action.equals(GET_CENTERLOGIN_ACTION)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (isNetwork && !requestErrorAlert.isShowing()) {
						logger.error("查询登录对象失败，select_id请求失败，网络连接出错");
						requestErrorAlert.setContentText("查询登录对象失败，请检查你的网络连接");
						requestErrorAlert.showAndWait();
					} else if (!requestErrorAlert.isShowing()) {
						logger.error("查询登录对象失败，服务器内部原因");
						requestErrorAlert.setContentText("查询登录对象失败，服务器内部错误");
						requestErrorAlert.showAndWait();
					}
					setDisableCb(false);
					resetBt.setDisable(false);

				}
			});
			return;
		}
		if (action.equals(GET_WORKORDER_ACTION)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (isNetwork && !requestErrorAlert.isShowing()) {
						logger.error("查询工单失败，select_workorder请求失败，网络连接出错");
						requestErrorAlert.setContentText("查询工单失败，请检查你的网络连接");
						requestErrorAlert.showAndWait();
					} else if (!requestErrorAlert.isShowing()) {
						logger.error("查询工单失败，服务器内部原因");
						requestErrorAlert.setContentText("查询工单失败，服务器内部错误");
						requestErrorAlert.showAndWait();
					}
					setDisableCb(false);
					resetBt.setDisable(false);

				}
			});
			return;
		}
		if (action.equals(GET_BOARDTYPE_ACTION)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (isNetwork && !requestErrorAlert.isShowing()) {
						logger.error("查询版面类型失败，select_boardtype请求失败，网络连接出错");
						requestErrorAlert.setContentText("查询版面类型失败，请检查你的网络连接");
						requestErrorAlert.showAndWait();
					} else if (!requestErrorAlert.isShowing()) {
						logger.error("查询版面类型失败，服务器内部原因");
						requestErrorAlert.setContentText("查询版面类型失败，服务器内部错误");
						requestErrorAlert.showAndWait();
					}
					setDisableCb(false);
					resetBt.setDisable(false);

				}
			});
			return;
		}
		if (action.equals(GET_OPERATOR_ACTION)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (isNetwork && !requestErrorAlert.isShowing()) {
						logger.error("查询操作员失败，select_operator请求失败，网络连接出错");
						requestErrorAlert.setContentText("查询操作员失败，请检查你的网络连接");
						requestErrorAlert.showAndWait();
					} else if (!requestErrorAlert.isShowing()) {
						logger.error("查询操作员失败，服务器内部原因");
						requestErrorAlert.setContentText("查询操作员失败，服务器内部错误");
						requestErrorAlert.showAndWait();
					}
					setDisableCb(false);
					resetBt.setDisable(false);

				}
			});
			return;
		}
		if (action.equals(GET_ITEMVISIT_ACTION)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (isNetwork && !requestErrorAlert.isShowing()) {
						logger.error("查询工单操作失败，select_itemvisit请求失败，网络连接出错");
						requestErrorAlert.setContentText("查询工单操作失败，请检查你的网络连接");
						requestErrorAlert.showAndWait();
					} else if (!requestErrorAlert.isShowing()) {
						logger.error("查询工单操作失败，服务器内部原因");
						requestErrorAlert.setContentText("查询工单操作失败，服务器内部错误");
						requestErrorAlert.showAndWait();
					}
					setDisableCb(false);
					resetBt.setDisable(false);

				}
			});
		}
	}

	
	/**
	 * 设置下拉框的状态
	 * 
	 * @param status
	 */
	private void setDisableCb(boolean status) {
		lineCb.setDisable(status);
		workOrderCb.setDisable(status);
		boardTybeCb.setDisable(status);
	}

	
	/**
	 * 更新站位，扫描站位，物料号，扫描物料号，操作员，操作结果以及表格数据
	 * 
	 * @param program
	 * @throws IOException
	 */
	private synchronized void updateText() {
		String line = lineCb.getSelectionModel().getSelectedItem() == null ? "" : lineCb.getSelectionModel().getSelectedItem().toString();
		String workOrder = workOrderCb.getSelectionModel().getSelectedItem() == null ? "" : workOrderCb.getSelectionModel().getSelectedItem().toString();
		String boardType = boardTybeCb.getSelectionModel().getSelectedItem() == null ? "" : boardTybeCb.getSelectionModel().getSelectedItem().toString();
		if (!line.equals("") && !workOrder.equals("") && !boardType.equals("")) {
			Integer boardTypeNo = getBoardTypeNo(boardType);
			Map<String, String> map = new HashMap<>();
			map.put("line", line);
			map.put("workOrder", workOrder);
			map.put("boardType", boardTypeNo.toString());
			String response = sendRequest(GET_OPERATOR_ACTION, map);
			if (response == null || "".equals(response)) {
				operator = "unknown";
			} else {
				operator = response;
			}
			try {
				String itemVisits = httpHelper.requestHttp(GET_ITEMVISIT_ACTION, map);
				if (!itemVisits.equals("[]")) {
					programItemVisits = JSON.parseArray(itemVisits, ProgramItemVisit.class);
				} else {
					httpFail(GET_ITEMVISIT_ACTION, !IS_NETWORK, null);
				}
				/*httpHelper.requestHttp(GET_ITEMVISIT_ACTION, map, new okhttp3.Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						String itemVisits = response.body().string();
						if (!itemVisits.equals("[]")) {
							programItemVisits = JSON.parseArray(itemVisits, ProgramItemVisit.class);
						} else {
							httpFail(GET_ITEMVISIT_ACTION, !IS_NETWORK, null);
						}
					}

					@Override
					public void onFailure(Call call, IOException e) {
						httpFail(GET_ITEMVISIT_ACTION, IS_NETWORK, null);

					}
				});*/

			} catch (IOException e) {
				httpFail(GET_ITEMVISIT_ACTION, IS_NETWORK, null);
				e.printStackTrace();
			}
			if (programItemVisits != null && programItemVisits.size() > 0) {
				ProgramItemVisit programItemVisit = programItemVisits.get(0);
				lineseatLb.setText(programItemVisit.getLineseat());
				scanLineseatLb.setText(programItemVisit.getScanLineseat());
				materialNoLb.setText(programItemVisit.getMaterialNo());
				scanMaterialNoLb.setText(programItemVisit.getScanMaterialNo());
				operatorLb.setText(operator);
				setTypeAndResult(programItemVisit);
				tableList.clear();
				tableList.addAll(programItemVisitToResultData(programItemVisits));
			}
		}
	}

	
	/**
	 * 设置typeLb和resultLb文本值，更改操作结果
	 * 
	 * @param programItemVisit
	 */
	private void setTypeAndResult(ProgramItemVisit programItemVisit) {
		if (programItemVisit.getLastOperationType() != null) {
			switch (programItemVisit.getLastOperationType()) {
			case 0:
				typeLb.setText("上  料");
				showResult(programItemVisit.getFeedResult());
				break;
			case 1:
				typeLb.setText("换  料");
				showResult(programItemVisit.getChangeResult());
				break;
			case 2:
				typeLb.setText("核  料");
				showResult(programItemVisit.getCheckResult());
				break;
			case 3:
				typeLb.setText("全  检");
				showResult(programItemVisit.getCheckAllResult());
				break;
			case 4:
				typeLb.setText("发  料");
				showResult(programItemVisit.getStoreIssueResult());
				break;
			case 5:
				typeLb.setText("首  检");
				showResult(programItemVisit.getFirstCheckAllResult());
				break;
			default:
				showResult(DEFAULT_RESULT);
				break;
			}
		} else {
			showResult(DEFAULT_RESULT);
		}
	}

	
	/**
	 * 根据操作结果进行显示
	 * 
	 * @param result
	 */
	private void showResult(Integer result) {
		String style = "-fx-alignment:center;-fx-text-fill:white;-fx-font-family:'Microsoft YaHei';-fx-font-weight:bold;";
		String textsize = "-fx-font-size:140px;";
		switch (result) {
		case 0:
			typeLb.setStyle(style + textsize + "-fx-background-color:red;");
			resultLb.setStyle(style + textsize + "-fx-background-color:red;");
			resultLb.setText("FAIL");
			break;
		case 1:
			typeLb.setStyle(style + textsize + "-fx-background-color:green;");
			resultLb.setStyle(style + textsize + "-fx-background-color:green;");
			resultLb.setText("PASS");
			break;
		case 2:
			typeLb.setStyle(style + textsize + "-fx-background-color:green;");
			resultLb.setStyle(style + textsize + "-fx-background-color:green;");
			typeLb.setText("操  作");
			resultLb.setText("结  果");
			break;
		case 3:
			typeLb.setStyle(style + textsize + "-fx-background-color:red;");
			resultLb.setStyle(style + textsize + "-fx-background-color:red;");
			resultLb.setText("已超时");
			break;
		case 4:
			typeLb.setStyle(style + textsize + "-fx-background-color:purple;");
			resultLb.setStyle(style + textsize + "-fx-background-color:purple;");
			typeLb.setText("已换料");
			resultLb.setText("请核料");
			break;
		default:
			break;
		}
	}

	
	/**@author HCJ
	 * 将ProgramItemVisit转化为可被表格识别的ResultData
	 * @date 2019年1月16日 下午4:56:57
	 */
	private List<ResultData> programItemVisitToResultData(List<ProgramItemVisit> programItemVisits) {
		List<ResultData> resultDatas = new ArrayList<>();
		int firstCheckAllResults = 1;
		int checkAllResults = 1;
		for (ProgramItemVisit programItemVisit : programItemVisits) {
			if (!programItemVisit.getFirstCheckAllResult().equals(1)) {
				firstCheckAllResults = 0;
				break;
			}
		}
		// 获取服务器时间戳
		long currentTimeStamp = Long.parseLong(sendRequest(GET_TIMESTAMP_ACTION, null));
		Date minCheckAllTime = new Date(currentTimeStamp);
		if (firstCheckAllResults == 1) {
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (programItemVisit.getCheckAllTime().getTime() < minCheckAllTime.getTime()) {
					// 获取最小全检时间
					minCheckAllTime = programItemVisit.getCheckAllTime();
				}
			}
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				if (!programItemVisit.getCheckAllResult().equals(SUCCESS_STATE)) {
					// 全检结果不全都为1时
					checkAllResults = 0;
					break;
				}
			}
		}
		// 获取核料和全检超时时间配置
		Integer checkAllCycleTime = getConfigValue(CHECK_ALL_CYCLE_TIME);
		Integer checkAfterChangeTime = getConfigValue(CHECK_AFTER_CHANGE_TIME);
		// 距离全检超时剩余时间
		Integer checkAllRestTime = (int) (checkAllCycleTime + (minCheckAllTime.getTime()) / ONE_SECOND_TO_MILLISECOND - (currentTimeStamp) / ONE_SECOND_TO_MILLISECOND);
		for (ProgramItemVisit programItemVisit : programItemVisits) {
			ResultData resultData = new ResultData();
			resultData.setLineseat(programItemVisit.getLineseat());
			resultData.setStoreIssueResult(programItemVisit.getStoreIssueResult());
			resultData.setFeedResult(programItemVisit.getFeedResult());
			if (programItemVisit.getChangeResult() == SUCCESS_STATE) {
				resultData.setChangeResult((int) ((programItemVisit.getChangeTime().getTime()) / ONE_SECOND_TO_MILLISECOND));
			} else {
				resultData.setChangeResult(programItemVisit.getChangeResult());
			}
			// 是否处于待核料状态
			boolean hasChangeButNeedCheck = programItemVisit.getChangeResult() == WAIT_CHECK_STATE;
			boolean isResultCorrect = (programItemVisit.getCheckResult() != ERROR_STATE && programItemVisit.getCheckResult() != OVERTIME_STATE);
			boolean notYetCheck = programItemVisit.getChangeTime().getTime() > programItemVisit.getCheckTime().getTime();
			if (hasChangeButNeedCheck && isResultCorrect && notYetCheck) {
				// 距离核料超时剩余时间
				Integer checkRestTime = (int) (checkAfterChangeTime + (programItemVisit.getChangeTime().getTime()) / ONE_SECOND_TO_MILLISECOND - currentTimeStamp / ONE_SECOND_TO_MILLISECOND);
				if (0 < checkRestTime && checkRestTime < checkAfterChangeTime) {
					resultData.setCheckResult(checkRestTime + CONSTANT_FOR_CHECK_RESULT);
				} else if (checkRestTime <= 0) {
					resultData.setCheckResult(CONSTANT_FOR_CHECK_RESULT);
				}
			} else {
				resultData.setCheckResult(programItemVisit.getCheckResult());
			}
			// 是否首检完成
			if (firstCheckAllResults == 1) {
				boolean isInitialState = programItemVisit.getCheckAllResult().equals(INITIAL_STATE);
				boolean isCheckTimeout = ((programItemVisit.getCheckAllTime().getTime()) + checkAllCycleTime * ONE_SECOND_TO_MILLISECOND) > currentTimeStamp;
				if (isInitialState && isCheckTimeout) {
					if (0 < checkAllRestTime && checkAllRestTime < checkAllCycleTime) {
						resultData.setCheckAllResult(checkAllRestTime + CONSTANT_FOR_CHECK_ALL_RESULT);
					} else if (checkAllRestTime <= 0) {
						resultData.setCheckAllResult(CONSTANT_FOR_CHECK_ALL_RESULT);
					}
				} else if (checkAllResults == 1) {
					if (0 < checkAllRestTime && checkAllRestTime < checkAllCycleTime) {
						resultData.setCheckAllResult(checkAllRestTime + CONSTANT_FOR_CHECK_ALL_RESULT);
					} else if (checkAllRestTime <= 0) {
						resultData.setCheckAllResult(CONSTANT_FOR_CHECK_ALL_RESULT);
					}
				} else {
					resultData.setCheckAllResult(programItemVisit.getCheckAllResult());
				}
			} else {
				resultData.setCheckAllResult(programItemVisit.getCheckAllResult());
			}
			resultData.setFirstCheckAllResult(programItemVisit.getFirstCheckAllResult());
			resultDatas.add(resultData);
		}
		return resultDatas;
	}

	
	/**
	 * 清除文本框文本，表格数据
	 */
	private void clearText() {
		lineseatLb.setText("");
		scanLineseatLb.setText("");
		materialNoLb.setText("");
		scanMaterialNoLb.setText("");
		operatorLb.setText("");
		showResult(DEFAULT_RESULT);
		tableList.clear();
	}

	
	/**
	 * 清空板面类型下拉框，重置工单下拉框内容
	 * 
	 * @param line 线号
	 */
	private void resetWorkOrderCb(String line) {
		workOrderCb.getItems().clear();
		boardTybeCb.getItems().clear();
		Map<String, String> map = new HashMap<>();
		map.put("line", line);
		String response = sendRequest(GET_WORKORDER_ACTION, map);
		if (!response.equals("[]")) {
			List<String> workorders = JSON.parseArray(response, String.class);
			ObservableList<String> workOrderList = FXCollections.observableArrayList(workorders);
			workOrderCb.setItems(workOrderList);
		} else {
			logger.error("获取工单失败，此产线不存在进行中的工单");
			new Alert(AlertType.ERROR, "获取工单失败，此产线不存在进行中的工单", ButtonType.OK).showAndWait();
		}
	}

	
	/**
	 * 将板面类型数字转化为中文
	 * 
	 * @param boardTybes 板面类型数字字符数组
	 * @return
	 */
	private List<String> getBoardType(List<String> boardTybes) {
		List<String> boardTybeList = new ArrayList<>();
		for (String boardTybe : boardTybes) {
			if (boardTybe.equals("0")) {
				boardTybeList.add("默认");
			} else if (boardTybe.equals("1")) {
				boardTybeList.add("AB面");
			} else if (boardTybe.equals("2")) {
				boardTybeList.add("A面");
			} else if (boardTybe.equals("3")) {
				boardTybeList.add("B面");
			}
		}
		return boardTybeList;
	}

	
	/**
	 * 将板面类型中文转化为数字
	 * 
	 * @param boardType
	 * @return
	 */
	private Integer getBoardTypeNo(String boardType) {
		Integer boardTypeNo = null;
		if (boardType.equals("默认")) {
			boardTypeNo = 0;
		} else if (boardType.equals("AB面")) {
			boardTypeNo = 1;
		} else if (boardType.equals("A面")) {
			boardTypeNo = 2;
		} else if (boardType.equals("B面")) {
			boardTypeNo = 3;
		}
		return boardTypeNo;
	}

	
	/**
	 * http关闭请求取消工单
	 * 
	 * @param line
	 */
	private void sendHttpCloseRequest(String line) {
		Map<String, String> map = new HashMap<>();
		map.put("line", line);
		try {
			httpHelper.requestHttp(SWITCH_ACTION, map, new okhttp3.Callback() {
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if (response.body().string().equals("{\"result\":\"succeed\"}")) {
						logger.info("关闭时取消工单成功，线号：" + line);
					} else {
						logger.error("关闭时取消工单失败，服务器内部错误，线号：" + line);
					}
					System.exit(0);
				}

				@Override
				public void onFailure(Call call, IOException e) {
					logger.error("关闭时取消工单失败，网络连接出错，线号：" + line);
					System.exit(0);
				}
			});
		} catch (IOException httpe) {
			logger.error("关闭时取消工单失败，网络连接出错，线号：" + line);
			System.exit(0);
		}
	}

	
	/**
	 * 改变工单下拉框和板面类型下拉框的宽度和字体大小
	 * 
	 * @param senceWidth
	 */
	private void changeCbSize(int senceWidth) {

		workOrderCb.setMinWidth((senceWidth / HALF - WORKORDERCB_MARGIN_LEFT));
		workOrderCb.setMaxWidth((senceWidth / HALF - WORKORDERCB_MARGIN_LEFT));
		workOrderCb.setStyle(workOrderCb.getStyle() + "-fx-font-size:" + (DEFAULT_CB_TEXTSIZE + (workOrderCb.getMinWidth() - DEFAULT_WORKORDERCB_WIDTH) / FACTOR_6) + ";");
		boardTybeCb.setMinWidth(DEAFULT_BOARDTYPECB_WIDTH + (senceWidth / HALF - DEFAULT_HALF_WIDTH) / FACTOR_1);
		boardTybeCb.setMaxWidth(DEAFULT_BOARDTYPECB_WIDTH + (senceWidth / HALF - DEFAULT_HALF_WIDTH) / FACTOR_1);
		boardTybeCb.setStyle(boardTybeCb.getStyle() + "-fx-font-size:" + (DEFAULT_CB_TEXTSIZE + (boardTybeCb.getMinWidth() - DEAFULT_BOARDTYPECB_WIDTH) / FACTOR_2) + ";");
	}

	
	/**
	 * 改变操作员文本框和线号下拉框的宽度，位置和字体
	 * 
	 * @param senceWidth
	 */
	private void changeOperatorSize(int senceWidth) {
		// 操作员工号文本框
		operatorLb.setMinWidth(DEFAULT_OPERATORLB_WIDTH + (senceWidth / HALF - DEFAULT_HALF_WIDTH) / FACTOR_1);
		operatorLb.setLayoutX(senceWidth / HALF - operatorLb.getMinWidth() - HALF_MIDDLE_INTERVAL);
		operatorNameLb.setLayoutX(senceWidth / HALF - operatorLb.getMinWidth() - DEFAULT_OPERATORNAMELB_WIDTH);
		operatorLb.setStyle(operatorLb.getStyle() + "-fx-font-size:" + (DEFAULT_LB_TEXTSIZE + (operatorLb.getMinWidth() - DEFAULT_OPERATORLB_WIDTH) / FACTOR_1) + ";");
		// 线号下拉框
		lineCb.setMinWidth(DEFAULT_LINECB_WIDTH + ((senceWidth / HALF - operatorLb.getMinWidth() - DEFAULT_LINECB_REMAIN) / FACTOR_5));
		lineCb.setMaxWidth(DEFAULT_LINECB_WIDTH + ((senceWidth / HALF - operatorLb.getMinWidth() - DEFAULT_LINECB_REMAIN) / FACTOR_5));
		lineCb.setStyle(lineCb.getStyle() + "-fx-font-size:" + (DEFAULT_CB_TEXTSIZE + (lineCb.getMinWidth() - DEFAULT_LINECB_WIDTH) / FACTOR_4) + ";");
	}

	
	/**
	 * 改变文本框的宽度和字体大小
	 * 
	 * @param senceWidth
	 */
	private void changeLbSize(int senceWidth) {
		String textStyle = "-fx-alignment:center;-fx-font-family:'Microsoft YaHei';" + "-fx-background-radius:4;-fx-border-radius:4;-fx-border-color:gray;-fx-font-weight:bold;";
		double LbSize = ((senceWidth - DEFAULT_LB_MARGIN_LEFT) / HALF) - DEFAULE_BORDER_LB_MARGIN_LEFT;

		lineseatLb.setMinWidth(LbSize);
		scanLineseatLb.setMinWidth(LbSize);
		materialNoLb.setMinWidth(LbSize);
		scanMaterialNoLb.setMinWidth(LbSize);
		double textsize = (DEFAULT_LB_TEXTSIZE + ((lineseatLb.getMinWidth() - DEFAULT_LB_WIDTH) / FACTOR_3));
		lineseatLb.setStyle(textStyle + ";-fx-font-size:" + textsize + "px;");
		scanLineseatLb.setStyle(textStyle + ";-fx-font-size:" + textsize + "px;");
		materialNoLb.setStyle(textStyle + ";-fx-font-size:" + textsize + "px;");
		scanMaterialNoLb.setStyle(textStyle + ";-fx-font-size:" + textsize + "px;");

		typeLb.setMinWidth(((senceWidth - RESULT_MARGIN_RIGHT) / HALF) - HALF_MIDDLE_INTERVAL);
		resultLb.setMinWidth(((senceWidth - RESULT_MARGIN_RIGHT) / HALF) - HALF_MIDDLE_INTERVAL);
		typeLb.setStyle(typeLb.getStyle() + "-fx-font-size:140px;-fx-font-weight:bold;");
		resultLb.setStyle(resultLb.getStyle() + "-fx-font-size:140px;-fx-font-weight:bold;");

	}

	
	/**
	 * @author HCJ 发送http请求
	 * @method sendRequest
	 * @param action
	 * @param args
	 * @return
	 * @return String
	 * @date 2018年9月25日 下午5:52:32
	 */
	private String sendRequest(String action, Map<String, String> args) {
		try {
			String response = httpHelper.requestHttp(action, args);
			result = response;
		} catch (IOException e) {
			httpFail(action, IS_NETWORK, null);
			e.printStackTrace();
		}
		return result;
	}

	
	/**
	 * @author HCJ 根据产线配置类型返回配置时间的秒数
	 * @param configName 配置项名称
	 * @date 2018年10月22日 上午9:49:27
	 */
	private Integer getConfigValue(String configName) {
		List<ConfigVO> configVOs = JSON.parseArray(sendRequest(GET_CONFIG_ACTION, null), ConfigVO.class);
		for (ConfigVO configVO : configVOs) {
			if (configVO.getLineName().equals(lineCb.getSelectionModel().getSelectedItem()) && configVO.getName().equals(configName)) {
				return Integer.parseInt(configVO.getValue()) * 60;
			}
		}
		return null;
	}

	
	/**
	 * @author HCJ 根据秒数返回时分秒的字符串
	 * @param time
	 * @date 2018年10月22日 上午9:50:09
	 */
	private String getCountDownTime(long time) {
		StringBuffer result = new StringBuffer();
		String hour = (time / 3600 % 24) + "";
		String minute = (time / 60 % 60) + "";
		String second = (time % 60) + "";
		return result.append(hour.length() < 2 ? "0" + hour : hour).append(":").append(minute.length() < 2 ? "0" + minute : minute).append(":").append(second.length() < 2 ? "0" + second : second).toString();
	}


	/**@author HCJ
	 * 初始化与服务端连接的websocket客户端
	 * @date 2019年3月3日 上午10:11:20
	 */
	private void initWebsocketClient() {
		DisplayClientSocket clientSocket = new DisplayClientSocket();
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		Map<String, String> websocket = IniReader.getItem(System.getProperty("user.dir") + "/" + CONFIG_FILE, "websocket");
		String url = websocket.get("url");
		connectToServerRunnable = new Runnable() {

			@Override
			public void run() {
				try {
					// logger.info("Display开始使用websocket连接服务端");
					session = container.connectToServer(clientSocket, URI.create(url));
					logger.info("Display使用websocket连接服务端成功");
					while (!Thread.currentThread().isInterrupted()) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
							session.close();
							return;
						}
					}
					session.close();
				} catch (DeploymentException | IOException e) {
					logger.error(e.getMessage());
				}
			}
		};
		connectToServerThread = new Thread(connectToServerRunnable);
		connectToServerThread.setDaemon(true);
		connectToServerThread.setName("connectToServerThread");
		clientSocket.setReceiveCallBack((message) -> {
			if (message != null && !"".equals(message) && message.contains("已断开") && !"".equals(selectedLineName) && message.contains(selectedLineName) && !centerOfflineAlert.isShowing()) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						TextArea textArea = new TextArea(message);
						textArea.setEditable(false);
						textArea.setWrapText(true);
						textArea.setFont(Font.font("Timer New Roman", FontWeight.BOLD, FONT_SIZE));
						textArea.setMaxSize(TEXTAREA_MAX_WIDTH, TEXTAREA_MAX_HEIGHT);

						GridPane content = new GridPane();
						content.add(textArea, 0, 1);

						centerOfflineAlert.getDialogPane().setContent(content);
						centerOfflineAlert.setHeaderText("接收到服务端信息：");
						centerOfflineAlert.showAndWait();
					}
				});
			} else if (message != null && !"".equals(message) && message.contains("已连接") && !"".equals(selectedLineName) && message.contains(selectedLineName) && centerOfflineAlert.isShowing()) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						centerOfflineAlert.close();

					}
				});
			}
		});
		clientSocket.setCloseCallBack((closeReason) -> {
			if (closeReason.getCloseCode().equals(CloseCodes.NORMAL_CLOSURE)) {
				logger.info("webSocekt关闭，Display与服务端断开连接");
			} else {
				logger.error("webSocekt出错关闭，Display与服务端断开连接");
				reConnect();
			}
		});
		connectToServerThread.start();
	}
	
	
	/**@author HCJ
	 * 记录当前版本
	 * @date 2019年1月21日 上午11:24:24
	 */
	private void initVersion() {
		try {
			String path = System.getProperty("java.class.path");
			String jarName = path.substring(path.lastIndexOf("\\") + 1, path.lastIndexOf("."));
			TextFileUtil.writeToFile(VERSION_FILE, jarName);
		} catch (IOException e) {
			logger.error("版本文件写入出现错误");
			e.printStackTrace();
		}
	}
	
	
	/**@author HCJ
	 * websocket连接错误关闭时，重新连接服务端
	 * @date 2019年3月3日 上午10:12:29
	 */
	private void reConnect() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(DEFAULT_TIMEPERIOD_AND_RECONNECT_CYCLE);
					if (!(session != null && session.isOpen() && connectToServerThread.isAlive())) {
						if (session != null && session.isOpen() && !connectToServerThread.isAlive()) {
							session.close();
						}
						if (connectToServerThread.isAlive()) {
							connectToServerThread.interrupt();
						}
						connectToServerThread = new Thread(connectToServerRunnable);
						connectToServerThread.start();
					}
				} catch (InterruptedException | IOException e) {
					logger.error(e.getMessage());
				}
			}
		});
	}
	
	
	/**@author HCJ
	 * 初始化更新时间周期
	 * @date 2019年3月11日 上午11:50:19
	 */
	private void initTimePeriod() {
		Map<String, String> cycleMap = IniReader.getItem(System.getProperty("user.dir") + "/" + CONFIG_FILE, "cycle");
		Integer updateCycle;
		try {
			updateCycle = Integer.parseInt(cycleMap.get("updateCycle"));
			if (updateCycle != null) {
				timePeriod = updateCycle;
			}
		} catch (NumberFormatException e) {
			timePeriod = DEFAULT_TIMEPERIOD_AND_RECONNECT_CYCLE;
		}
	}

}