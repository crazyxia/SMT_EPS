
package com.jimi.smt.eps.printer.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.ibatis.jdbc.Null;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.core.CloseReasons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.jimi.smt.eps.printer.app.Main;
import com.jimi.smt.eps.printer.callback.PrintTaskReceiveCallBack;
import com.jimi.smt.eps.printer.callback.PrinteTaskCloseCallBack;
import com.jimi.smt.eps.printer.callback.RemotePrintCallBack;
import com.jimi.smt.eps.printer.entity.Material;
import com.jimi.smt.eps.printer.entity.MaterialProperties;
import com.jimi.smt.eps.printer.entity.MaterialFileInfo;
import com.jimi.smt.eps.printer.entity.PrintTaskInfo;
import com.jimi.smt.eps.printer.entity.Rule;
import com.jimi.smt.eps.printer.entity.StockLog;
import com.jimi.smt.eps.printer.entity.WebSocketResult;
import com.jimi.smt.eps.printer.printtool.PrintFileJsonReader;
import com.jimi.smt.eps.printer.runnable.RemotePrintRunnable;
import com.jimi.smt.eps.printer.util.ExcelHelper;
import com.jimi.smt.eps.printer.util.HttpHelper;
import com.jimi.smt.eps.printer.util.IniReader;
import com.jimi.smt.eps.printer.websocket.RemotePrintTaskReceiver;

import cc.darhao.dautils.api.DateUtil;
import cc.darhao.dautils.api.ResourcesUtil;
import cc.darhao.dautils.api.TextFileUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * 主页控制器
 * 
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class MainController implements Initializable {

	/**
	 * 配置文件名
	 */
	private static final String CONFIG_FILE_NAME = "printer.cfg";

	/**
	 * 配置：所选料号表Sheet名
	 */
	private static final String CONFIG_KEY_SHEET_NAME = "sheetName";

	/**
	 * 配置：打印目标（二维码 or RFID or 两者）
	 */
	private static final String CONFIG_KEY_PRINT_TARGET = "printTarget";
	private static final String INSERT_STOCK_ACTION = "stock/insert";
	private static final String CONFIG_FILE = "/config.ini";

	private static Logger logger = LogManager.getRootLogger();
	// 供应商名与物料表的映射表
	private static Map<String, List<Material>> supplierAndMatrialsMap = new ConcurrentHashMap<>();
	private static String selectedSheet;

	@FXML
	private AnchorPane parentAp;
	@FXML
	private TextField materialNoTf;
	@FXML
	private TextField scanMaterialNoTf;
	@FXML
	private TextField descriptionTf;
	@FXML
	private TextField nameTf;
	@FXML
	private TextField quantityTf;
	@FXML
	private TextField seatNoTf;
	@FXML
	private TextField remarkTf;
	@FXML
	private TextField dateTf;
	@FXML
	private Button printBt;
	@FXML
	private Label loginIdLb;
	@FXML
	private Label stateLb;
	@FXML
	private Label materialNoLb;
	@FXML
	private Label descriptionLb;
	@FXML
	private Label nameLb;
	@FXML
	private Label quantityLb;
	@FXML
	private Label seatLb;
	@FXML
	private Label dateLb;
	@FXML
	private Label remarkLb;
	@FXML
	private ImageView codeIv;
	@FXML
	private AnchorPane previewAp;
	@FXML
	private Label materialNoLb1;
	@FXML
	private Label descriptionLb1;
	@FXML
	private Label nameLb1;
	@FXML
	private Label quantityLb1;
	@FXML
	private Label seatLb1;
	@FXML
	private Label dateLb1;
	@FXML
	private Label remarkLb1;
	@FXML
	private ImageView codeIv1;
	@FXML
	private AnchorPane previewAp1;
	@FXML
	private ChoiceBox tableSelectCb;
	@FXML
	private TableView materialTb;
	@FXML
	private TableColumn materialNoCol;
	@FXML
	private TableColumn nameCol;
	@FXML
	private TableColumn descriptionCol;
	@FXML
	private TableColumn seatNoCol;
	@FXML
	private TableColumn quantityCol;
	@FXML
	private TableColumn remarkCol;
	@FXML
	private Button configBt;
	@FXML
	private Button changePwdBt;
	@FXML
	private Button materialMangerBt;
	@FXML
	private TextField copyTf;
	@FXML
	private CheckBox ignoreCb;
	@FXML
	private RadioButton bothRb;
	@FXML
	private RadioButton codeRb;
	@FXML
	private RadioButton rfidRb;
	@FXML
	private Label currentRuleLb;

	private Stage primaryStage;
	private Properties properties;

	// 打印者id
	private String userId;

	/**
	 * 二维码和RFID内的数据<br>
	 * 格式：料号@数量@时间戳@工号
	 */
	private static String data;

	// Table的数据源
	private List<Material> materials;

	// 打印标签纸控制socket
	private Socket printerSocket;

	private RemotePrintTaskReceiver remoteReceiver;

	// 写入RFID标签控制socket
	private static Socket rfidSocket;

	// 打印任务的份数
	private int copies;

	// RFID的COM口号
	private int port;

	// 一次完整任务的打印结果
	public static boolean rfidResult, codeResult;

	// 流水号
	private static int serialNo = 0;

	// Socket_IP
	private final static String ip = "localhost";

	// 入库数据消息队列
	private List<String> stockLogList = new ArrayList<String>();
	private HttpHelper httpHelper = HttpHelper.me;
	private ObservableList<String> suppliers = FXCollections.observableArrayList();
	private volatile int isOpenRemotePrint = 0;
	private Thread remotePrintThread = null;
	private Runnable remotePrintRunnable = null;
	private Session session = null;


	public void initialize(URL arg0, ResourceBundle arg1) {
		initTableCol();
		initChangePwdBt();
		initMaterialNoTfListener();
		initTableSelectorCbListener();
		initMaterialPropertiesTfsListener();
		initScanMaterialNoTfListener();
		setCurrentRule();
		initHotKey();
		initDataFromConfig();
		initMaterialTable();
		loadTableSelectorData();
		initPrinter();
		initRFID();
		initRemoteSocket();
		showLogoutAlert();
	}


	/**
	 * 提示打印结束后退出账号
	 */
	public void showLogoutAlert() {
		Alert alert = new Alert(AlertType.WARNING, "请在打印结束后点击关闭按钮以退出当前账号，防止个人账号被其他人员使用", ButtonType.OK);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				alert.showAndWait();
				scanMaterialNoTf.requestFocus();
			}
		});
	}


	/**
	 * 初始化料号表管理按钮
	 */
	public void onMaterialMangerBtClick() {
		materialMangerBt.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FXMLLoader loader;
				try {
					loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/tableManager.fxml"));
					Parent root = loader.load();
					Stage stage = new Stage();
					// 显示
					stage.setResizable(false);
					stage.setTitle("供应商物料表管理");
					stage.setScene(new Scene(root));
					stage.setResizable(false);
					MaterialFileInfoManagerController managerController = loader.getController();
					managerController.setStage(stage);
					stage.initModality(Modality.APPLICATION_MODAL);
					stage.showAndWait();
					// 清空料号表下拉框
					if (suppliers != null && suppliers.size() > 0) {
						suppliers.clear();
					}
					// 清空当前料号表
					if (materials != null && materials.size() > 0) {
						materials.clear();
					}
					// 清空供应商和料号表之间的映射关系
					if (supplierAndMatrialsMap != null && supplierAndMatrialsMap.size() > 0) {
						supplierAndMatrialsMap.clear();
					}
					// 重新读取料号表路径文件,并加载其中内容
					initMaterialTable();
					loadTableSelectorData();
				} catch (IOException e) {
					error("加载供应商物料表管理窗口失败");
					e.printStackTrace();
				}

			}
		});
	}


	/**
	 * 初始化远程打印
	 */
	public void initRemoteSocket() {
		remoteReceiver = new RemotePrintTaskReceiver();
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		Map<String, String> map_url = IniReader.getItem(System.getProperty("user.dir") + CONFIG_FILE, "websocketurl");
		String webSocketURL = map_url.get("websocketurl");
		// 连接webSocket
		remotePrintRunnable = new Runnable() {

			@Override
			public void run() {
				try {
					infoRunLater("开始启用远程打印功能，连接WebSocekt...");
					String ip = Inet4Address.getLocalHost().getHostAddress();
					session = container.connectToServer(remoteReceiver, URI.create(webSocketURL + ip));
					infoRunLater("连接WebSocekt成功，启动远程打印成功");
					while (!Thread.currentThread().isInterrupted()) {
					}
					session.close();
				} catch (DeploymentException | IOException e) {
					errorRunLater("启动远程打印失败，请检查网络连接");
					e.printStackTrace();
				}
			}
		};
		remotePrintThread = new Thread(remotePrintRunnable);
		// 接收到打印请求时执行
		remoteReceiver.setReceiveCallBack((session, info) -> {
			CountDownLatch countDownLatch = new CountDownLatch(1);
			WebSocketResult result = new WebSocketResult();
			result.fail(info.getId(), "打印指令下达失败");
			try {
				List<Material> materialsList = supplierAndMatrialsMap.get(info.getSupplier());
				// 判断加载的所有料号表中是否包含远程打印料号信息中的供应商
				if (materialsList == null) {
					result.fail(info.getId(), "请在打印软件上添加该物料所在的物料表");
					return;
				}
				// 判断对应料号表中是否包含远程打印信息中的料号
				for (Material material : materialsList) {
					if (material.getNo().equals(info.getMaterialNo())) {
						// 启动打印
						Platform.runLater(new RemotePrintRunnable(result, info.getId(), countDownLatch, new RemotePrintCallBack() {
							
							@Override
							public boolean remotePrintCallBack() {
								return remoteCallPrint(info, material);
							}
						}));
						try {
							countDownLatch.await();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return ;
					}
				}
				result.fail(info.getId(), "查无此物料，请确定该供应商是否存在此物料");
			} finally {
				try {
					session.getBasicRemote().sendText(JSONObject.toJSONString(result));
				} catch (IOException e) {
					errorRunLater("发送信息失败，远程打印失效，请检查网络连接，请重新启用该功能");
					try {
						session.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

		});

		// webSocket连接关闭时执行
		remoteReceiver.setCloseCallBack((closeReason) -> {
			if (closeReason.getCloseCode().equals(CloseCodes.NORMAL_CLOSURE)) {
				infoRunLater("webSocekt关闭，远程打印功能失效");
			} else {
				errorRunLater("webSocekt出错关闭，远程打印功能失效");
			}

		});
		// 读取配置,确定是否开启远程打印
		try {
			isOpenRemotePrint = Integer.parseInt(TextFileUtil.readFromFile("e.cfg").split(",")[3]);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		if (isOpenRemotePrint == 1) {
			remotePrintThread.start();
		}
	}


	/**
	 * 初始话物料表格,从json文件中读取全部文件路径并进行加载
	 */
	public void initMaterialTable() {
		// 读取料号表路径Json文件
		PrintFileJsonReader jsonReader = PrintFileJsonReader.getInstance();
		// 获取物料表文件信息
		List<MaterialFileInfo> materialFileInfos = jsonReader.getJsonToObject();
		if (materialFileInfos == null || materialFileInfos.size() <= 0) {
			loadTableData(null);
			return;
		}
		for (MaterialFileInfo materialFile : materialFileInfos) {
			// 获取物料表路径
			File file = new File(materialFile.getFilePath());
			if (!file.exists()) {
				continue;
			}
			try {
				ExcelHelper excel = ExcelHelper.from(file);
				for (int i = 0; i < excel.getBook().getNumberOfSheets(); i++) {
					// 获取料号表名
					String name = excel.getBook().getSheetAt(i).getSheetName();
					excel.switchSheet(i);
					try {
						// 解析物料表
						List<Material> materialList = excel.unfill(Material.class, 1);
						if (supplierAndMatrialsMap.get(name) != null) {
							error("存在相同表名，解析失败");
							continue;
						}
						// 将料号表名与解析好的料号表内容添加进映射
						supplierAndMatrialsMap.put(name, materialList);
						// 将料号表名添加仅料号表选择下拉框
						suppliers.add(name);
					} catch (Exception e) {
						e.printStackTrace();
						error("加载供应商料号表出现IO错误");
						continue;
					}
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}


	public void onPrintBtClick() {
		// 信息完整性校验
		String quantity = quantityTf.getText();
		String seat = seatNoTf.getText();
		String date = dateTf.getText();
		if (quantity == null || quantity.equals("") || seat == null || seat.equals("") || date == null
				|| date.equals("")) {
			error("请填写数量、位置和生产日期信息");
			return;
		}
		// 日期类型校验
		if (!isDate(date)) {
			error("请正确填写生产日期YY-MM-DD");
			return;
		}
		// 数字类型校验
		String copy = copyTf.getText();
		try {
			int quantityInt = Integer.parseInt(quantity);
			int copyInt = Integer.parseInt(copy);
			if (copyInt < 1 || quantityInt < 1) {
				throw new NumberFormatException();
			}
			copies = copyInt;
			try {
				print(null, false, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (NumberFormatException e) {
			error("请输入正整数");
		}
	}


	public void onConfigBtClick() {
		showConfigWindow();
		// 之前是否开启远程打印
		try {
			isOpenRemotePrint = Integer.parseInt(TextFileUtil.readFromFile("e.cfg").split(",")[3]);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		// 现在是否开启远程打印
		if (isOpenRemotePrint == 0) {
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (!remotePrintThread.isAlive()) {
				remotePrintThread.interrupt();
			}
			return;
		} else {
			if (!session.isOpen()) {
				if (!remotePrintThread.isAlive()) {
					remotePrintThread.interrupt();
				}
				remotePrintThread = new Thread(remotePrintRunnable);
				remotePrintThread.start();
			}
		}
	}


	public void onIgnoreClick() {
		if (ignoreCb.isSelected()) {
			nameTf.setDisable(false);
			descriptionTf.setDisable(false);
			seatNoTf.setDisable(false);
			quantityTf.setDisable(false);
			remarkTf.setDisable(true);
			dateTf.setDisable(false);
			remarkTf.setText("未校验");
			remarkLb.setText("未校验");
			printBt.setDisable(false);
			info("已开启忽略校验模式，请确保正确输入料号，并且尽早完善料号表格式并退出该模式");
		} else {
			resetControllers();
		}
	}


	/**
	 * 远程调用打印
	 * 
	 * @param info
	 *            接收到的打印数据
	 * @param material
	 *            与接收到打印数据中料号相对应的物料数据
	 * @return
	 */
	public synchronized boolean remoteCallPrint(PrintTaskInfo info, Material material) {
		boolean result;
		// 封锁UI
		materialNoTf.setDisable(true);
		nameTf.setDisable(true);
		descriptionTf.setDisable(true);
		seatNoTf.setDisable(true);
		quantityTf.setDisable(true);
		remarkTf.setDisable(true);
		dateTf.setDisable(true);

		// 备份UI的值
		Map<String, String> backup = new HashMap<>();
		backup.put("materialNo", materialNoTf.getText());
		backup.put("materialNoLb", materialNoLb.getText());
		backup.put("quantity", quantityTf.getText());
		backup.put("seatNo", seatNoTf.getText());
		backup.put("description", descriptionTf.getText());
		backup.put("name", nameTf.getText());
		backup.put("remark", remarkTf.getText());
		backup.put("date", dateTf.getText());
		backup.put("userId", userId);
		// 调用打印
		copies = 1;
		// 解锁UI
		try {
			materialNoLb.setText(material.getNo());
			System.err.println(materialNoTf.getText());
			quantityTf.setText(info.getRemainingQuantity());
			seatNoTf.setText(material.getSeat());
			userId = info.getUser();
			descriptionTf.setText(material.getDescription());
			nameTf.setText(material.getName());
			remarkTf.setText("UW");
			dateTf.setText(info.getProductDate());
			print(info.getMaterialId(), true, info);
			info("接收到远程打印请求，物料号：" + material.getNo());
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			stockLogList.remove(stockLogList.size()-1);
			error("发生错误：" + e.getMessage());
			result = false;
		} finally {
			// 还原UI的值
			materialNoLb.setText(backup.get("materialNoLb"));
			materialNoTf.setText(backup.get("materialNo"));
			quantityTf.setText(backup.get("quantity"));
			seatNoTf.setText(backup.get("seatNo"));
			descriptionTf.setText(backup.get("description"));
			nameTf.setText(backup.get("name"));
			remarkTf.setText(backup.get("remark"));
			dateTf.setText(backup.get("date"));
			// 解锁UI
			materialNoTf.setText("");
			materialNoTf.requestFocus();
			printBt.setText("打印");
			userId = backup.get("userId");
		}
		return result;
	}


	/**
	 * 打印任务
	 * 
	 * @throws Exception
	 * @param materialId
	 *            料盘时间戳,可空,如果为空,表示采用当前时间。否则采用给定值
	 * @param isRemote
	 *            是否是远程打印
	 * @param info
	 *            远程打印信息,仅当为远程打印时有效
	 */
	public void print(String materialId, boolean isRemote, PrintTaskInfo info) throws Exception {
		rfidResult = codeResult = true;
		if (!isRemote) {
			userId = loginIdLb.getText();
		}
		// 准备操作
		printBt.setDisable(true);
		printBt.setText("打印中...");
		info("打印中...");
		try {
			for (int i = 0; i < copies; i++) {
				subPrint(materialId, isRemote, info);
				if (!rfidResult) {
					throw new Exception("未成功写入RFID数据！");
				} else if (!codeResult) {
					throw new Exception("未成功打印二维码标签！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			stockLogList.remove(stockLogList.size());
			error("发生错误：" + e.getMessage());
			throw e;
		}
		materialNoTf.setText("");
		materialNoTf.requestFocus();
		printBt.setText("打印");
		// 提交数据库
		commitDataBase();
	}


	/**
	 * 打印子任务,此方法会被print调用若干次,次数为份数
	 * 
	 * @para materialId 料盘时间戳,可空,如果为空,表示采用当前时间。否则采用给定值
	 * @param isRemote
	 *            是否是远程打印
	 * @param info
	 *            远程打印信息,仅当为远程打印时有效
	 * @throws Exception
	 */
	public void subPrint(String materialId, boolean isRemote, PrintTaskInfo info) throws Exception {
		// 生成数据
		createData(materialId, isRemote, info);
		// 记录入库日志
		try {
			stockLogList.add(data);
			// 判断打印目标
			if (codeRb.isSelected()) {
				// 生成二维码
				createCode();
				// 生成截图
				createImage();
				// 发送打印指令
				callPrinter();
			} else if (rfidRb.isSelected()) {
				// 弹出RFID对话框
				showRfidAlert();
			} else if (bothRb.isSelected()) {
				// 弹出RFID对话框
				showRfidAlert();
				// 生成二维码
				createCode();
				// 生成截图
				createImage();
				// 发送打印指令
				callPrinter();
			}
		} catch (Exception e) {
			throw e;
		}
	}


	/**
	 * @author HCJ 显示规则管理窗口
	 * @date 2018年10月29日 下午3:35:01
	 */
	public void onManageRuleBtClick() {
		showManageRuleWindow();
	}


	/**
	 * 调用打印机
	 */
	private void callPrinter() {
		codeResult = false;
		try {
			if (printerSocket == null || !printerSocket.isConnected()) {
				throw new IOException();
			}
			printerSocket.getOutputStream().write(1);
			if (printerSocket.getInputStream().read() == 1) {
				codeResult = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 生成数据
	 * 
	 * @para materialId 料盘时间戳,可空,如果为空,表示采用当前时间。否则采用给定值
	 * @param isRemote
	 *            是否是远程打印
	 * @param info
	 *            远程打印信息,仅当为远程打印时有效
	 */
	private void createData(String materialId, boolean isRemote, PrintTaskInfo info) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(materialNoLb.getText().trim());
		stringBuffer.append("@");
		stringBuffer.append(quantityLb.getText().trim().equals("") ? "0" : quantityLb.getText().trim());
		stringBuffer.append("@");
		if (materialId == null) {
			stringBuffer.append(System.currentTimeMillis());
		} else {
			stringBuffer.append(materialId);
		}
		stringBuffer.append("@");
		stringBuffer.append(userId);
		stringBuffer.append("@");
		stringBuffer.append(tableSelectCb.getSelectionModel().getSelectedItem().toString().trim());
		stringBuffer.append("@");
		stringBuffer.append(seatLb.getText().trim());
		stringBuffer.append("@");
		stringBuffer.append(serialNo++);
		stringBuffer.append("@");
		stringBuffer.append(dateTf.getText().trim());
		stringBuffer.append("@");
		data = stringBuffer.toString();
	}


	/**
	 * 尝试提交入库记录,若失败将会记录到文件,将在下次调用时一并提交
	 */
	private void commitDataBase() {
		// 插入数据库
		new Thread(() -> {
			File file = new File("stock_log_backup.dat");
			try {
				// 从硬盘读取备份文件拼接到队列
				if (file.exists()) {
					String[] datas = TextFileUtil.readFromFile("stock_log_backup.dat").split("\\|");
					for (String string : datas) {
						stockLogList.add(string);
					}
				}
				// 插入库
				System.out.println(stockLogList);
				List<StockLog> stockList = new ArrayList<>();
				for (String data : stockLogList) {
					String[] datas = data.split("@");
					StockLog log = new StockLog();
					log.setMaterialNo(datas[0] == null ? "" : datas[0]);
					log.setQuantity(Integer.valueOf(datas[1]));
					log.setTimestamp(datas[2] == null ? "" : datas[2]);
					log.setOperator(datas[3] == null ? "" : datas[3]);
					log.setCustom(datas[4] == null ? "" : datas[4]);
					log.setPosition(datas[5] == null ? "" : datas[5]);
					log.setProductionDate(new SimpleDateFormat("yyyy-MM-dd").parse(datas[7]));
					log.setOperationTime(new Date());
					stockList.add(log);
				}
				// 提交并双清
				Map<String, String> map = new HashMap<>();
				map.put("stockList", JSON.toJSONString(stockList));
				httpHelper.requestHttp(INSERT_STOCK_ACTION, map);
				stockLogList.clear();
				file.delete();
			} catch (Exception e) {
				// 队列记录备份硬盘文件并清除队列
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				StringBuffer sb = new StringBuffer();
				for (String data : stockLogList) {
					sb.append(data);
					sb.append("|");
				}
				sb.deleteCharAt(sb.length() - 1);
				try {
					TextFileUtil.writeToFile("stock_log_backup.dat", sb.toString());
					stockLogList.clear();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}).start();
	}


	private void createCode() {
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		try {
			// 生成矩阵
			BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 260, 260, hints);
			// 输出并显示图像
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "gif", byteArrayOutputStream);
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			codeIv.setImage(SwingFXUtils.toFXImage(ImageIO.read(byteArrayInputStream), null));
		} catch (IOException e) {
			error("输出二维码图像时出错");
			e.printStackTrace();
		} catch (WriterException e) {
			error("生成二维码时出错");
			e.printStackTrace();
		}

	}


	private void createImage() {
		// 拷贝到大图层
		materialNoLb1.setText(materialNoLb.getText());
		nameLb1.setText(nameLb.getText());
		descriptionLb1.setText(descriptionLb.getText());
		seatLb1.setText(seatLb.getText());
		quantityLb1.setText(quantityLb.getText());
		remarkLb1.setText(userId + " / " + remarkLb.getText());
		dateLb1.setText(dateLb.getText());
		codeIv1.setImage(codeIv.getImage());
		previewAp1.setVisible(true);
		Image image = previewAp1.snapshot(null, null);
		try {
			// 根据分辨率设置尺寸
			int resolution = Integer.parseInt(TextFileUtil.readFromFile("e.cfg").split(",")[2]);
			BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
			if (resolution != 300) {
				int width = (int) (bi.getWidth() / 300.0 * resolution);
				int height = (int) (bi.getHeight() / 300.0 * resolution);
				BufferedImage newBi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
				newBi.getGraphics().drawImage(bi, 0, 0, width, height, null);
				bi = newBi;
			}
			if (!ImageIO.write(bi, "gif", new File("Picture.gif"))) {
				throw new IOException();
			}
		} catch (IOException e) {
			error("生成图片时出错");
			e.printStackTrace();
		} finally {
			previewAp1.setVisible(false);
		}
	}


	/**
	 * 加载表选择器数据
	 */
	@SuppressWarnings("unchecked")
	private void loadTableSelectorData() {
		tableSelectCb.setItems(suppliers);
		if (suppliers.contains(selectedSheet)) {
			tableSelectCb.getSelectionModel().select(suppliers.indexOf(selectedSheet));
		} else if (suppliers != null && suppliers.size() > 0) {
			tableSelectCb.getSelectionModel().select(0);
		}
		info("数据解析成功，请在上方选择表");
	}


	/**
	 * 加载表数据
	 */
	@SuppressWarnings("unchecked")
	private void loadTableData(List<Material> materials) {
		if (materials != null && materials.size() > 0) {
			ObservableList<MaterialProperties> materialPropertiesList = FXCollections.observableArrayList();
			for (Material material : materials) {
				MaterialProperties materialProperties = new MaterialProperties(material);
				materialPropertiesList.add(materialProperties);
			}
			materialTb.setItems(materialPropertiesList);
		} else {
			materialTb.setItems(null);
		}
		// 料号表选择后,将焦点移动到料号输入框
		materialNoTf.requestFocus();
	}


	private void initDataFromConfig() {
		// 检查e.cfg存在与否,不存在则重新创建
		if (!new File("e.cfg").exists()) {
			try {
				// 默认设置，左边距:0,顶边距:0,分辨率:300,远程打印:0
				TextFileUtil.writeToFile("e.cfg", "0,0,300,0");
			} catch (IOException e1) {
				logger.error("e.cfg文件创建失败");
			}
		}
		properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(CONFIG_FILE_NAME)));
		} catch (FileNotFoundException | NullPointerException e) {
			try {
				new File(CONFIG_FILE_NAME).createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
				error("创建配置文件时出现IO错误");
			}
		} catch (IOException e) {
			e.printStackTrace();
			error("读取配置文件时出现IO错误");
		}
		selectedSheet = properties.getProperty(CONFIG_KEY_SHEET_NAME);
		// 初始化时间
		dateLb.setText(DateUtil.yyyyMMdd(new Date()));
		dateTf.setText(DateUtil.yyyyMMdd(new Date()));
	}


	private void initHotKey() {
		// 初始化打印热键
		parentAp.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().compareTo(KeyCode.ENTER) == 0) {
					// 控制焦点转移或打印
					if (materialNoTf.isFocused()) {
						quantityTf.requestFocus();
					} else if (quantityTf.isFocused()) {
						if (!printBt.isDisable()) {
							onPrintBtClick();

						}
					}
				}
			}
		});
		// ChangeListener<String> listener = new ChangeListener<String>() {
		//
		// @Override
		// public void changed(ObservableValue<? extends String> observable, String
		// oldValue, String newValue) {
		// if(newValue.equals("")) {
		// return;
		// }
		// char c = newValue.charAt(newValue.length() - 1);
		// if(c == '\n' || c == '\r') {
		// //控制焦点转移或打印
		// if(materialNoTf.isFocused()) {
		// quantityTf.requestFocus();
		// }else if(quantityTf.isFocused()){
		// if(!printBt.isDisable()) {
		// onPrintBtClick();
		// }
		// }
		// }
		// }
		// };
		// materialNoTf.textProperty().addListener(listener);
		// quantityTf.textProperty().addListener(listener);
	}


	private void initPrinter() {
		new Thread(() -> {
			// 初始化打印机
			try {
				if (!new File("printer.exe").exists()) {
					System.out.println("1111111111");
					throw new IOException();
				}
				Runtime.getRuntime().exec("printer.exe");
				printerSocket = new Socket();
				printerSocket.setSoTimeout(1000);
				printerSocket.connect(new InetSocketAddress(ip, 10101), 3000);
			} catch (IOException e) {
				Platform.runLater(() -> {
					error("启动打印机程序失败，请检查打印机是否在工作并重启程序");
				});
				e.printStackTrace();
			}
		}).start();
	}


	private void initRFID() {
		new Thread(() -> {
			// 初始化RFID
			try {
				if (!new File("SMT_EPS_RFID_WRITER.exe").exists()) {
					throw new IOException();
				}
				Runtime.getRuntime().exec("SMT_EPS_RFID_WRITER.exe");
				rfidSocket = new Socket();
				rfidSocket.connect(new InetSocketAddress(ip, 10102), 3000);
				// 获取初始化结果
				rfidSocket.setSoTimeout(30000);
				int result = rfidSocket.getInputStream().read();
				if (result == '0') {
					// 更改COM号再重启RFID程序
					if (port == 31) {
						throw new IOException("已尝试0~31的端口，依然无法找到RFID设备");
					}
					port++;
					try {
						TextFileUtil.writeToFile("RFIDcomm.cfg", "Port=" + port);
						initRFID();
					} catch (IOException e) {
						Platform.runLater(() -> {
							error("启动RFID程序失败，缺少配置文件RFIDComm.cfg");
							initPrintTargetRbs();
							codeRb.setSelected(true);
						});
						e.printStackTrace();
					}
				} else {
					Platform.runLater(() -> {
						info("RFID程序加载完毕");
						rfidRb.setDisable(false);
						bothRb.setDisable(false);
						initPrintTargetRbs();
					});
				}
			} catch (IOException e) {
				Platform.runLater(() -> {
					error("启动RFID程序失败，请检查RFID读写器是否在工作并重启程序");
					initPrintTargetRbs();
					codeRb.setSelected(true);
				});
				e.printStackTrace();
			}
		}).start();
		if (Thread.currentThread().getName().equals("JavaFX Application Thread")) {
			info("加载RFID程序中...");
		}
	}


	private void initChangePwdBt() {
		changePwdBt.setOnAction((event) -> {

			FXMLLoader loader;
			try {
				loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/changePwd.fxml"));
				Parent root = loader.load();
				Stage stage = new Stage();
				// 显示
				ChangePwdController changePwdController = loader.getController();
				changePwdController.setLoginUserId(loginIdLb.getText());
				changePwdController.setStage(stage);
				stage.setResizable(false);
				stage.setTitle("修改密码");
				stage.setScene(new Scene(root));
				stage.setResizable(false);
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.showAndWait();
			} catch (IOException e) {
				e.printStackTrace();
				error("加载窗口出错");
			}

		});
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initPrintTargetRbs() {
		ChangeListener listener = new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// 保存到配置文件中
				String value = "0";
				if (codeRb.isSelected()) {
					value = "0";
				} else if (rfidRb.isSelected()) {
					value = "1";
				} else if (bothRb.isSelected()) {
					value = "2";
				}
				// 存配置
				properties.setProperty(CONFIG_KEY_PRINT_TARGET, value);
				try {
					properties.store(new FileOutputStream(new File(CONFIG_FILE_NAME)), null);
				} catch (IOException e) {
					e.printStackTrace();
					error("保存配置文件时出现IO错误");
				}
			}

		};
		codeRb.selectedProperty().addListener(listener);
		rfidRb.selectedProperty().addListener(listener);
		bothRb.selectedProperty().addListener(listener);
		// 读配置
		String value = properties.getProperty(CONFIG_KEY_PRINT_TARGET, "0");
		switch (value) {
		case "0":
			codeRb.setSelected(true);
			break;
		case "1":
			rfidRb.setSelected(true);
			break;
		case "2":
			bothRb.setSelected(true);
			break;
		default:
			codeRb.setSelected(true);
			break;
		}
	}


	@SuppressWarnings("unchecked")
	private void initTableCol() {
		// 初始化表格列表
		materialNoCol.setCellValueFactory(new PropertyValueFactory<>("no"));
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		seatNoCol.setCellValueFactory(new PropertyValueFactory<>("seat"));
		quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));
	}


	private void initTableSelectorCbListener() {
		// 初始化下拉框监听器
		tableSelectCb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue.intValue() > -1) {
					// 切换到指定sheet
					String name = tableSelectCb.getItems().get(newValue.intValue()).toString();
					materials = supplierAndMatrialsMap.get(name);
					if (materials != null) {
						loadTableData(materials);
						selectedSheet = name;
					} else {
						loadTableData(null);
					}
					// 设置料号为空
					materialNoTf.setText("");
					scanMaterialNoTf.requestFocus();
				}
			}
		});
	}


	private void initMaterialNoTfListener() {
		// 初始化物料编号文本域监听器
		materialNoTf.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (materials == null) {
					printBt.setDisable(true);
					return;
				}
				if (ignoreCb.isSelected()) {
					materialNoLb.setText(materialNoTf.getText());
					printBt.setDisable(false);
					return;
				}
				for (Material material : materials) {
					// 如果存在该料号则显示属性,并且允许更改和打印,并且把料号显示为绿色,否则不显示属性,禁止更改和打印,且料号为黑色
					if (newValue != null && !newValue.equals("")
							&& material.getNo().toUpperCase().equals(newValue.toUpperCase())) {
						nameTf.setDisable(false);
						descriptionTf.setDisable(false);
						seatNoTf.setDisable(false);
						quantityTf.setDisable(false);
						remarkTf.setDisable(false);
						dateTf.setDisable(false);

						nameTf.setText(material.getName());
						descriptionTf.setText(material.getDescription());
						seatNoTf.setText(material.getSeat());
						quantityTf.setText(material.getQuantity());
						remarkTf.setText(material.getRemark());
						dateTf.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

						materialNoLb.setText(material.getNo());
						nameLb.setText(material.getName());
						descriptionLb.setText(material.getDescription());
						seatLb.setText(material.getSeat());
						quantityLb.setText(material.getQuantity());
						remarkLb.setText(material.getRemark());

						materialNoTf.setStyle("-fx-text-fill: green;");

						printBt.setDisable(false);

						// 焦点移至数量输入框
						quantityTf.requestFocus();

						info("料号存在，打印已就绪（热键：回车）");
						break;
					}
					resetControllers();

					materialNoTf.setStyle("-fx-text-fill: black;");

					// info("请确认输入的料号是否存在");
				}
			}

		});
	}


	private void initMaterialPropertiesTfsListener() {
		nameTf.textProperty().addListener(new MaterialPropertiesTfChangeListener("name"));
		descriptionTf.textProperty().addListener(new MaterialPropertiesTfChangeListener("description"));
		seatNoTf.textProperty().addListener(new MaterialPropertiesTfChangeListener("seat"));
		quantityTf.textProperty().addListener(new MaterialPropertiesTfChangeListener("quantity"));
		remarkTf.textProperty().addListener(new MaterialPropertiesTfChangeListener("remark"));
		dateTf.textProperty().addListener(new MaterialPropertiesTfChangeListener("date"));
	}


	/**
	 * 显示正常状态
	 * 
	 * @param message
	 */
	private void info(String message) {
		stateLb.setTextFill(Color.BLACK);
		stateLb.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		stateLb.setText(DateUtil.HHmmss(new Date()) + " - " + message);
	}


	/**
	 * 显示正常状态,应用于非主线程中控制控件
	 * 
	 * @param message
	 */
	private void infoRunLater(String message) {
		Platform.runLater(() -> {
			stateLb.setTextFill(Color.BLACK);
			stateLb.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
			stateLb.setText(DateUtil.HHmmss(new Date()) + " - " + message);
		});
	}


	/**
	 * 显示错误状态,并记录日志
	 * 
	 * @param message
	 */
	private void error(String message) {
		stateLb.setTextFill(Color.WHITE);
		stateLb.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		stateLb.setText(DateUtil.HHmmss(new Date()) + " - " + message);
		logger.error(message);
	}


	/**
	 * 显示错误状态,并记录日志,应用于非主线程中控制控件
	 * 
	 * @param message
	 */
	private void errorRunLater(String message) {
		Platform.runLater(() -> {
			stateLb.setTextFill(Color.WHITE);
			stateLb.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
			stateLb.setText(DateUtil.HHmmss(new Date()) + " - " + message);
			logger.error(message);
		});
	}

	class MaterialPropertiesTfChangeListener implements ChangeListener<String> {

		private String materialPropertyName;


		public MaterialPropertiesTfChangeListener(String materialPropertyName) {
			this.materialPropertyName = materialPropertyName;
		}


		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			switch (materialPropertyName) {
			case "name":
				nameLb.setText(nameTf.getText());
				break;
			case "description":
				descriptionLb.setText(descriptionTf.getText());
				break;
			case "seat":
				seatLb.setText(seatNoTf.getText());
				break;
			case "quantity":
				quantityLb.setText(quantityTf.getText());
				break;
			case "remark":
				remarkLb.setText(remarkTf.getText());
				break;
			case "date":
				dateLb.setText(dateTf.getText());
			default:
				break;
			}
		}

	}


	private void showConfigWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/config.fxml"));
			Parent root = loader.load();
			ConfigController configController = loader.getController();
			// 显示
			Stage stage = new Stage();
			stage.setAlwaysOnTop(true);
			configController.setStage(stage);
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			error("加载窗口时出错");
		}

	}


	private void showRfidAlert() {
		try {
			FXMLLoader loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/rfid.fxml"));
			Parent root = loader.load();
			RfidController rfidController = loader.getController();
			// 显示
			Alert alert = new Alert(AlertType.NONE);
			alert.setTitle("写入数据");
			alert.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
			alert.initOwner(primaryStage);
			alert.setGraphic(root);
			rfidController.setAlert(alert);
			alert.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			error("加载窗口时出错");
		}
	}


	/**
	 * @author HCJ 打开规则管理窗口
	 * @date 2018年10月29日 下午3:35:51
	 */
	private void showManageRuleWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/manageRule.fxml"));
			Parent root = loader.load();
			ManageRuleController manageRuleController = loader.getController();
			// 显示
			Stage stage = new Stage();
			stage.setTitle("规则管理");
			manageRuleController.setStage(stage);
			manageRuleController.setMainController(MainController.this);
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			error("加载窗口时出错");
		}
	}


	private void resetControllers() {
		nameTf.setDisable(true);
		descriptionTf.setDisable(true);
		seatNoTf.setDisable(true);
		quantityTf.setDisable(true);
		remarkTf.setDisable(true);
		dateTf.setDisable(true);

		nameTf.setText("");
		descriptionTf.setText("");
		seatNoTf.setText("");
		quantityTf.setText("");
		remarkTf.setText("");
		dateTf.setText(DateUtil.yyyyMMdd(new Date()));

		materialNoLb.setText("");
		nameLb.setText("");
		descriptionLb.setText("");
		seatLb.setText("");
		quantityLb.setText("");
		remarkLb.setText("");

		printBt.setDisable(true);
	}


	/**
	 * 日期类型校验
	 * 
	 * @param date
	 * @return
	 */
	private static Boolean isDate(String date) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(date);
			return true;
		} catch (Exception e) {
			return false;
		}

	}


	/**
	 * @author HCJ 扫描料号监听器
	 * @date 2018年10月31日 下午5:04:46
	 */
	private void initScanMaterialNoTfListener() {
		// 初始化得到的物料编号文本域监听器
		scanMaterialNoTf.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Rule rule = ManageRuleController.currentRule;
				if (rule.getDetails() == null) {
					rule.setName("默认规则");
					rule.setDetails("默认规则:" + ",");
				}
				if (newValue == null || newValue.equals("") || newValue.length() < 0) {
					materialNoTf.setText("");
				}
				try {
					if (newValue != null && !newValue.equals("") && newValue.length() > 0) {
						String[] ruleArray = rule.getDetails().split(",");
						for (String ruleString : ruleArray) {
							newValue = getMaterialNo(ruleString, newValue);
						}
						materialNoTf.setText(newValue);
					}
				} catch (Exception e) {
					error("请应用规则并且扫描有效二维码");
					logger.error("请应用规则并且扫描有效二维码");
				}
			}
		});
	}


	/**
	 * @author HCJ 根据料号规则解析得到料号
	 * @date 2018年10月31日 下午5:05:10
	 */
	private String getMaterialNo(String ruleString, String materialNoString) {
		if (ruleString.contains("分隔符")) {
			String[] materialNoArray;
			try {
				materialNoArray = removeSpace(materialNoString
						.split(ruleString.substring(ruleString.indexOf(":") + 1, ruleString.indexOf("="))));
			} catch (Exception e) {
				materialNoArray = removeSpace(materialNoString
						.split("\\" + ruleString.substring(ruleString.indexOf(":") + 1, ruleString.indexOf("="))));
			}
			return materialNoArray[Integer
					.parseInt(ruleString.substring(ruleString.indexOf("=") + 1, ruleString.length()))];
		} else if (ruleString.contains("长度")) {
			int start = Integer.parseInt(ruleString.substring(ruleString.indexOf(":") + 1, ruleString.indexOf("->")));
			int end = Integer.parseInt(ruleString.substring(ruleString.indexOf("->") + 1, ruleString.length()));
			return materialNoString.substring(start, end);
		} else if (ruleString.contains("默认规则")) {
			return materialNoString;
		}
		return null;
	}


	public void initCloseEvent(Stage primaryStage) {
		primaryStage.setOnCloseRequest((event) -> {
			FXMLLoader loader;
			try {
				loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/login.fxml"));
				Parent root = loader.load();
				// 把Stage存入MainController
				LoginController loginController = loader.getController();
				Stage stage = new Stage();
				loginController.setPrimaryStage(stage);
				// 显示
				stage.setResizable(false);
				stage.setTitle("防错料系统 - 条码打印器 " + Main.getVersion());
				stage.setScene(new Scene(root));
				stage.show();

				properties.setProperty(CONFIG_KEY_SHEET_NAME, selectedSheet);
				try {
					properties.store(new FileOutputStream(new File(CONFIG_FILE_NAME)), null);
				} catch (IOException e) {
					e.printStackTrace();
					error("保存配置文件时出现IO错误");
				}
				// 清空料号表下拉框
				if (suppliers != null && suppliers.size() > 0) {
					suppliers.clear();
				}
				// 清空当前料号表
				if (materials != null && materials.size() > 0) {
					materials.clear();
				}
				// 清空供应商和料号表之间的映射关系
				if (supplierAndMatrialsMap != null && supplierAndMatrialsMap.size() > 0) {
					supplierAndMatrialsMap.clear();
				}
				rfidSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("开启登录界面失败");
			}

		});
	}


	/**
	 * @author HCJ 设置当前规则
	 * @date 2018年10月31日 下午5:05:36
	 */
	public void setCurrentRule() {
		if (ManageRuleController.currentRule != null && ManageRuleController.currentRule.getName() != null) {
			currentRuleLb.setText("当前料号规则：" + ManageRuleController.currentRule.getName());
		} else {
			currentRuleLb.setText("当前料号规则：默认规则");
		}
	}


	public Stage getPrimaryStage() {
		return primaryStage;
	}


	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}


	public Socket getPrinterSocket() {
		return printerSocket;
	}


	/**
	 * 二维码和RFID内的数据<br>
	 * 格式：料号@数量@时间戳@工号
	 */
	public static String getData() {
		return data;
	}


	public static Socket getRfidSocket() {
		return rfidSocket;
	}


	public void setLoginIdLbText(String loginUserId) {
		loginIdLb.setText(loginUserId);
	}


	public String[] removeSpace(String[] array) {
		List<String> tmp = new ArrayList<String>();
		for (String str : array) {
			if (str != null && str.length() != 0) {
				tmp.add(str);
			}
		}
		return tmp.toArray(new String[0]);
	}

}
