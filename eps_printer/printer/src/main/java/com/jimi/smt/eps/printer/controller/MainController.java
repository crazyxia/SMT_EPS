package com.jimi.smt.eps.printer.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.jimi.smt.eps.printer.entity.Material;
import com.jimi.smt.eps.printer.entity.MaterialProperties;
import com.jimi.smt.eps.printer.util.DateUtil;
import com.jimi.smt.eps.printer.util.ExcelHelper;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainController implements Initializable {

	private static final String CONFIG_FILE_NAME = "printer.cfg";
	
	private static final String CONFIG_KEY_FILE_PATH = "filePath";
	
	private static final String CONFIG_KEY_SHEET_NAME = "sheetName";
	
	private Logger logger = LogManager.getRootLogger();
	
	@FXML
	private TextField fileSelectTf;
	@FXML
	private TextField materialNoTf;
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
	private Button fileSelectBt;
	@FXML
	private Button printBt;
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
	private Label timeLb;
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
	private Label timeLb1;
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
	
	private Stage primaryStage;
	
	private ExcelHelper excel;
	
	private Properties properties;
	
	//Table的数据源
	private List<Material> materials;
	
	//打印控制socket
	private Socket printerSocket;
	
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		initTableSelectorCb();
		initTableCol();
		initMaterialNoTf();
		initMaterialPropertiesTfs();
		init();
	}


	public void onFileSelectBtClick() {
		//初始化文件选择器
		FileChooser chooser = new FileChooser();
		chooser.setTitle("选择供应商料号表文件");
		chooser.getExtensionFilters().add(new ExtensionFilter("供应商料号表文件", "*.xls" , "*.xlsx"));
		//尝试读取配置文件获取上次默认路径
		String filePath = new String();
		File materialFile = null;
		try {
			filePath = properties.getProperty(CONFIG_KEY_FILE_PATH);
			if(filePath != null && !filePath.equals("")) {
				File file = new File(filePath);
				if(file.getParentFile().exists()) {
					chooser.setInitialDirectory(file.getParentFile());
				}
			}
		} catch (NullPointerException e) {
			try {
				new File(CONFIG_FILE_NAME).createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
				error("创建配置文件时出现IO错误");
			}
		}
		//选择文件
		materialFile = chooser.showOpenDialog(primaryStage);
		if(materialFile != null) {
			//初始化Excel
			fileSelectTf.setText(materialFile.getAbsolutePath());
			try {
				excel = ExcelHelper.from(materialFile);
			} catch (IOException e1) {
				e1.printStackTrace();
				error("加载供应商料号表出现IO错误");
			}
			//加载表选择器数据
			loadTableSelectorData();
			//设置当前sheet
			excel.switchSheet(0);
			//设置当前下拉选项
			tableSelectCb.getSelectionModel().select(0);
			//设置料号为空
			materialNoTf.setText("");
			//存配置
			properties.setProperty(CONFIG_KEY_FILE_PATH, materialFile.getAbsolutePath());
			try {
				properties.store(new FileOutputStream(new File(CONFIG_FILE_NAME)), null);
			} catch (IOException e) {
				e.printStackTrace();
				error("配置文件时出现IO错误");
			}
		}
	}
		
	
	public void onPrintBtClick() {
		//准备操作
		printBt.setDisable(true);
		printBt.setText("打印中...");
		info("打印中...");
		//生成时间
		timeLb.setText(DateUtil.yyyyMMddHHmmss(new Date()));
		//生成二维码
		createCode();
		//生成截图
		createImage();
		//发送打印指令
	    callPrinter();
	}


	private void callPrinter() {
		try {
			if(printerSocket == null || !printerSocket.isConnected()) {
				throw new IOException();
			}
			printerSocket.getOutputStream().write(1);
			new Thread() {
				public void run() {
					try {
						if(printerSocket.getInputStream().read() == 1) {
							Platform.runLater(new Runnable() {
							    @Override
							    public void run() {
							        //更新JavaFX的主线程的代码放在此处
							    	info("打印成功");
							    }
							});
						}else {
							Platform.runLater(new Runnable() {
							    @Override
							    public void run() {
							        //更新JavaFX的主线程的代码放在此处
							    	error("打印失败");
							    }
							});
						}
					} catch (IOException e) {
						Platform.runLater(new Runnable() {
						    @Override
						    public void run() {
						        //更新JavaFX的主线程的代码放在此处
						    	error("无法接收打印结果");
						    }
						});
						e.printStackTrace();
					}finally {
						Platform.runLater(new Runnable() {
						    @Override
						    public void run() {
						        //更新JavaFX的主线程的代码放在此处
						    	//收尾操作
								printBt.setDisable(false);
								printBt.setText("打印");
						    }
						});
					}
				};
			}.start();
		} catch (IOException e) {
			 //收尾操作
			printBt.setDisable(false);
			printBt.setText("打印");
			error("调用打印机程序失败");
			e.printStackTrace();
		}
	}


	private void createCode() {
		//整合数据
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(materialNoLb.getText());
		stringBuffer.append("@");
		stringBuffer.append(quantityLb.getText().equals("") ? "0" : quantityLb.getText());
		stringBuffer.append("@");
		stringBuffer.append(timeLb.getText());
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();  
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");  
        try {
        	//生成矩阵  
        	BitMatrix bitMatrix = new MultiFormatWriter().encode(stringBuffer.toString(),BarcodeFormat.QR_CODE, 260, 260, hints);
            //输出并显示图像
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "gif", byteArrayOutputStream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            codeIv.setImage(SwingFXUtils.toFXImage(ImageIO.read(byteArrayInputStream), null));
        }catch (IOException e) {
			error("输出二维码图像时出错");
			e.printStackTrace();
		} catch (WriterException e) {
			error("生成二维码时出错");
			e.printStackTrace();
		}
        
	}


	private void createImage() {
		//拷贝到大图层
		materialNoLb1.setText(materialNoLb.getText());
		nameLb1.setText(nameLb.getText());
		descriptionLb1.setText(descriptionLb.getText());
		seatLb1.setText(seatLb.getText());
		quantityLb1.setText(quantityLb.getText());
		remarkLb1.setText(remarkLb.getText());
		timeLb1.setText(timeLb.getText());
		codeIv1.setImage(codeIv.getImage());
		previewAp1.setVisible(true);
	    Image image = previewAp1.snapshot(null, null);
	    try {
	        if(!ImageIO.write(SwingFXUtils.fromFXImage(image, null), "gif", new File("Picture.gif"))) {
	        	throw new IOException();
	        }
	    } catch (IOException e) {
	    	error("生成图片时出错");
	        e.printStackTrace();
	    }finally {
	    	previewAp1.setVisible(false);
	    }
	}
	
	
	/**
	 * 加载表选择器数据
	 */
	private void loadTableSelectorData() {
		ObservableList<String> list = FXCollections.observableArrayList();
		for (int i = 0; i < excel.getBook().getNumberOfSheets(); i++) {
			String name = excel.getBook().getSheetAt(i).getSheetName();
			list.add(name);
		}
		tableSelectCb.setItems(list);
		info("数据解析成功，请在上方选择表");
	}


	/**
	 * 加载表数据
	 */
	private void loadTableData() {
		try {
			materials = excel.unfill(Material.class, 1);
		} catch (Exception e) {
			error("数据解析失败，请按照模版表格编写供应商料号表文件");
			materialTb.setItems(null);
			return;
		}
		ObservableList<MaterialProperties> materialPropertiesList = FXCollections.observableArrayList();
		for (Material material : materials) {
			MaterialProperties materialProperties = new MaterialProperties(material);
			materialPropertiesList.add(materialProperties);
		}
		materialTb.setItems(materialPropertiesList);
		info("数据解析成功，请在右上方扫入或输入料号");
	}

	private void init() {
		//读取上次文件路径和表名
		properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(CONFIG_FILE_NAME)));
		} catch (FileNotFoundException | NullPointerException e) {
			e.printStackTrace();
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
		String filePath = properties.getProperty(CONFIG_KEY_FILE_PATH);
		String sheetName = properties.getProperty(CONFIG_KEY_SHEET_NAME);
		info("请选择供应商料号表文件");
		
		//如果没有配置则不做什么
		if(filePath == null || filePath.equals("")) {
			return;
		}
		//读取文件
		File materialFile = new File(filePath);
		try {
			excel = ExcelHelper.from(materialFile);
		} catch (IOException e) {
			e.printStackTrace();
			error("供应商料号表文件\""+ materialFile.getName() +"\"不存在");
		}
		//设置当前文件名
		fileSelectTf.setText(filePath);
		info("文件加载成功");
		
		
		//加载表选择下拉菜单数据
		loadTableSelectorData();
		
		//切换到指定sheet
		if(sheetName == null || sheetName.equals("")) {
			return;
		}
		if(!excel.switchSheet(sheetName)) {
			error("表\""+ sheetName +"\"不存在");
			return;
		}
		//设置当前下拉选项
		tableSelectCb.getSelectionModel().select(excel.getBook().getSheetIndex(sheetName));
		
		//加载表数据
		loadTableData();
		
		//初始化时间
		timeLb.setText(DateUtil.yyyyMMddHHmmss(new Date()));
		
		//初始化打印机
		try {
			if(!new File("printer.exe").exists()) {
				throw new IOException();
			}
			Runtime.getRuntime().exec("printer.exe");
			printerSocket = new Socket("10.10.11.110", 10101);
		} catch (IOException e) {
			error("启动打印机程序失败");
			e.printStackTrace();
		}
	}


	private void initTableCol() {
		//初始化表格列表
		materialNoCol.setCellValueFactory(new PropertyValueFactory<>("no"));
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		seatNoCol.setCellValueFactory(new PropertyValueFactory<>("seat"));
		quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));
	}


	private void initTableSelectorCb() {
		//初始化下拉框监听器
		tableSelectCb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if(newValue.intValue() == -1) {
					//存配置
					properties.setProperty(CONFIG_KEY_SHEET_NAME, "");
				}else {
					//切换到指定sheet
					if(!excel.switchSheet(newValue.intValue())) {
						error("表切换失败");
						return;
					}
					//更新数据
					loadTableData();
					//存配置
					properties.setProperty(CONFIG_KEY_SHEET_NAME, excel.getBook().getSheetName(newValue.intValue()));
					//设置料号为空
					materialNoTf.setText("");
				}
				try {
					properties.store(new FileOutputStream(new File(CONFIG_FILE_NAME)), null);
				} catch (IOException e) {
					e.printStackTrace();
					error("配置文件时出现IO错误");
				}
			}
		});
	}


	private void initMaterialNoTf() {
		//初始化物料编号文本域监听器
		materialNoTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				for (Material material : materials) {
					//如果存在该料号则显示属性，并且允许更改和打印，并且把料号显示为绿色，否则不显示属性，禁止更改和打印，且料号为黑色
					if(newValue != null && !newValue.equals("") && material.getNo().toUpperCase().equals(newValue.toUpperCase())) {
						nameTf.setDisable(false);
						descriptionTf.setDisable(false);
						seatNoTf.setDisable(false);
						quantityTf.setDisable(false);
						remarkTf.setDisable(false);
						
						nameTf.setText(material.getName());
						descriptionTf.setText(material.getDescription());
						seatNoTf.setText(material.getSeat());
						quantityTf.setText(material.getQuantity());
						remarkTf.setText(material.getRemark());
						
						materialNoLb.setText(material.getNo());
						nameLb.setText(material.getName());
						descriptionLb.setText(material.getDescription());
						seatLb.setText(material.getSeat());
						quantityLb.setText(material.getQuantity());
						remarkLb.setText(material.getRemark());
						
						materialNoTf.setStyle("-fx-text-fill: green;");
						
						printBt.setDisable(false);
						
						info("料号存在，打印已就绪");
						break;
					}
					nameTf.setDisable(true);
					descriptionTf.setDisable(true);
					seatNoTf.setDisable(true);
					quantityTf.setDisable(true);
					remarkTf.setDisable(true);
					
					nameTf.setText("");
					descriptionTf.setText("");
					seatNoTf.setText("");
					quantityTf.setText("");
					remarkTf.setText("");
					
					materialNoLb.setText("");
					nameLb.setText("");
					descriptionLb.setText("");
					seatLb.setText("");
					quantityLb.setText("");
					remarkLb.setText("");
					
					materialNoTf.setStyle("-fx-text-fill: black;");
					
					printBt.setDisable(true);
					
					info("请确认输入的料号是否存在");
				}
			}
		});
	}
	

	private void initMaterialPropertiesTfs() {
		nameTf.textProperty().addListener(new MaterialPropertiesTfChangeListener("name"));
		descriptionTf.textProperty().addListener(new MaterialPropertiesTfChangeListener("description"));
		seatNoTf.textProperty().addListener(new MaterialPropertiesTfChangeListener("seat"));
		quantityTf.textProperty().addListener(new MaterialPropertiesTfChangeListener("quantity"));
		remarkTf.textProperty().addListener(new MaterialPropertiesTfChangeListener("remark"));
	}

	
	/**
	 * 显示正常状态
	 * @param message
	 */
	private void info(String message) {
		stateLb.setTextFill(Color.BLACK);
		stateLb.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		stateLb.setText(DateUtil.HHmmss(new Date()) +" - "+ message);
	}


	/**
	 * 显示错误状态，并记录日志
	 * @param message
	 */
	private void error(String message) {
		stateLb.setTextFill(Color.WHITE);
		stateLb.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		stateLb.setText(DateUtil.HHmmss(new Date()) +" - "+ message);
		logger.error(message);
	}


	class MaterialPropertiesTfChangeListener implements ChangeListener<String>{

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
			default:
				break;
			}
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
	
	
}
