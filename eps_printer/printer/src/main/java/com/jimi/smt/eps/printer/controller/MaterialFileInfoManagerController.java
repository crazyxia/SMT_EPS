
package com.jimi.smt.eps.printer.controller;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.naming.java.javaURLContextFactory;
import org.hamcrest.core.Every;

import com.jimi.smt.eps.printer.entity.MaterialFileInfo;
import com.jimi.smt.eps.printer.entity.MaterialFileInfoResultData;
import com.jimi.smt.eps.printer.entity.RuleResultData;
import com.jimi.smt.eps.printer.printtool.PrintFileJsonReader;

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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

/**
 * 供应商料号表管理控制器
 * 
 * @author Administrator
 *
 */
public class MaterialFileInfoManagerController implements Initializable {

	@FXML
	private Button fileSelectBt;
	@FXML
	private TextField fileSelectTf;
	@FXML
	private Button addFilePathBt;
	@FXML
	private Button deleteFilePathBt;
	@FXML
	private Button saveFilePathBt;
	@FXML
	private TableView<MaterialFileInfoResultData> materialTb;
	@FXML
	private TableColumn<MaterialFileInfoResultData, String> fileNameCl;
	@FXML
	private TableColumn<MaterialFileInfoResultData, String> fileSizeCl;
	@FXML
	private TableColumn<MaterialFileInfoResultData, String> filePathCl;

	private ObservableList<MaterialFileInfoResultData> materialFileInfoResultDataTbList;

	private Stage stage;

	private List<MaterialFileInfoResultData> materialTbResults;

	private Integer selectNo = null;

	private PrintFileJsonReader jsonFile;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		materialTbResults = new ArrayList<>();
		jsonFile = PrintFileJsonReader.getInstance();
		initMaterialFileTable();
		tableRowChangeListener();
	}


	/**
	 * 浏览按钮点击事件
	 */
	public void onFileSelectBtClick() {
		// 初始化文件选择器
		FileChooser chooser = new FileChooser();
		chooser.setTitle("选择供应商料号表文件");
		chooser.getExtensionFilters().add(new ExtensionFilter("供应商料号表文件", "*.xls", "*.xlsx"));
		File materialFile = null;
		// 选择文件
		materialFile = chooser.showOpenDialog(stage);
		if (materialFile != null) {
			fileSelectTf.setText(materialFile.getAbsolutePath());
		}
		// 获取文件路径
		String filePath = fileSelectTf.getText();
		File file = new File(filePath);
		if (!file.exists() || (!filePath.endsWith(".xls") && !filePath.endsWith(".xlsx"))) {
			new Alert(AlertType.ERROR, "请先选择正确的文件", ButtonType.OK).showAndWait();
			return;
		}
		// 获取文件名
		String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
		MaterialFileInfo materialFileInfo = new MaterialFileInfo();
		materialFileInfo.setFileName(fileName);
		materialFileInfo.setFilePath(filePath);
		// 将文件大小转化为KB，保留两位小数点
		DecimalFormat df = new DecimalFormat("#.00");
		materialFileInfo.setFileSize(String.valueOf(df.format(file.length() * 1.0 / (1024))) + " KB");
		System.err.println(fileName+filePath);
		MaterialFileInfoResultData mData = new MaterialFileInfoResultData(materialFileInfo);
		materialFileInfoResultDataTbList.add(mData);
		fileSelectTf.clear();
	}


	public void onSaveFilePathBtClick() {
		List<MaterialFileInfoResultData> materialTbResultDatas = materialFileInfoResultDataTbList.subList(0,
				materialFileInfoResultDataTbList.size());
		boolean result = jsonFile.writeJsonObject(materialTbResultDatas);
		if (result) {
			getStage().close();
		} else {
			new Alert(AlertType.INFORMATION, "保存失败", ButtonType.OK).showAndWait();
		}
	}


	public void initMaterialFileTable() {
		fileNameCl.setCellValueFactory(new PropertyValueFactory<MaterialFileInfoResultData, String>("fileName"));
		filePathCl.setCellValueFactory(new PropertyValueFactory<MaterialFileInfoResultData, String>("filePath"));
		fileSizeCl.setCellValueFactory(new PropertyValueFactory<MaterialFileInfoResultData, String>("fileSize"));
		initcell(fileNameCl);
		initcell(filePathCl);
		initcell(fileSizeCl);
		// 从Json文件里读取之前存储的料号表文件信息，将其加载仅表格内
		List<MaterialFileInfo> materialFileInfos = jsonFile.getJsonToObject();
		materialFileInfoResultDataTbList = FXCollections.observableArrayList();
		if (materialFileInfos != null && materialFileInfos.size() > 0) {
			materialTbResults = MaterialFileInfoResultData.materialTbToReusltData(materialFileInfos);
			materialFileInfoResultDataTbList = FXCollections.observableArrayList(materialTbResults);
		}
		materialTb.setItems(materialFileInfoResultDataTbList);
		
	}


	public void initcell(TableColumn<MaterialFileInfoResultData, String> tableColumn) {

		tableColumn.setCellFactory(
				new Callback<TableColumn<MaterialFileInfoResultData, String>, TableCell<MaterialFileInfoResultData, String>>() {

					public TableCell<MaterialFileInfoResultData, String> call(
							TableColumn<MaterialFileInfoResultData, String> pColumn) {
						return new TableCell<MaterialFileInfoResultData, String>() {

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


	public void onDeleteFilePathBtClick() {
		if (selectNo == null) {
			new Alert(AlertType.INFORMATION, "请先选中文件", ButtonType.OK).showAndWait();
			return;
		}
		String fileName = materialFileInfoResultDataTbList.get(selectNo.intValue()).getFileName();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("删除物料表路径");
		alert.setContentText("确定删除文件名：" + fileName + " 的物料表路径?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			materialFileInfoResultDataTbList.remove(selectNo.intValue());
		}

	}


	public void tableRowChangeListener() {
		materialTb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue != null && newValue.intValue() >= 0) {
					selectNo = newValue.intValue();
				}
			}
		});
	}


	public Stage getStage() {
		return stage;
	}


	public void setStage(Stage stage) {
		this.stage = stage;
	}


	public ObservableList<MaterialFileInfoResultData> getMaterialTbList() {
		return materialFileInfoResultDataTbList;
	}

}
