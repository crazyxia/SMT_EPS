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

import com.jimi.smt.eps.printer.entity.MaterialTb;
import com.jimi.smt.eps.printer.entity.MaterialTbResultData;
import com.jimi.smt.eps.printer.entity.RuleResultData;
import com.jimi.smt.eps.printer.util.JsonFile;

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

public class MaterialTbManagerController implements Initializable{

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
	private TableView<MaterialTbResultData> materialTb;
	@FXML
	private TableColumn<MaterialTbResultData, String> fileNameCl;
	@FXML
	private TableColumn<MaterialTbResultData, String> fileSizeCl;
	@FXML
	private TableColumn<MaterialTbResultData, String> filePathCl;
	
	private ObservableList<MaterialTbResultData> materialTbList;
	
	private Stage stage;
	
	private List<MaterialTbResultData> materialTbResults;
	
	private Integer selectNo = null;
	
	private JsonFile jsonFile;
	
	private static boolean IS_FIRST = true;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		materialTbResults = new ArrayList<>();
		jsonFile = JsonFile.getInstance();
		onFileSelectBtClick();
		initMaterialTb();
		initDeleteFilePathBt();
		tableRowChangeListener();
		initSaveFilePathBt();
	}
	
	
	public void onFileSelectBtClick() {
		fileSelectBt.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
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
				String filePath = fileSelectTf.getText();
				File file = new File(filePath);
				if (!file.exists() || (!filePath.endsWith(".xls") && !filePath.endsWith(".xlsx"))) {
					new Alert(AlertType.ERROR, "请先选择正确的文件", ButtonType.OK).showAndWait();
					return;
				}
				String fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
				MaterialTb materialTb = new MaterialTb();
				DecimalFormat df = new DecimalFormat("#.00");
				materialTb.setFileName(fileName);
				materialTb.setFilePath(filePath);
				materialTb.setFileSize(String.valueOf(df.format(file.length()*1.0/(1024)))+" KB");
				MaterialTbResultData mData = new MaterialTbResultData(materialTb);
				materialTbList.add(mData);
				fileSelectTf.clear();
			}
		});
		
	}
	
	
	public void initSaveFilePathBt() {
		saveFilePathBt.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				List<MaterialTbResultData> materialTbResultDatas = materialTbList.subList(0, materialTbList.size());
				boolean result = jsonFile.writeJsonObject(materialTbResultDatas);
				if (result) {
					getStage().close();
				}else {
					new Alert(AlertType.INFORMATION, "保存失败", ButtonType.OK).showAndWait();
				}
			}
		});
	}
	
	
	public void initMaterialTb() {
		fileNameCl.setCellValueFactory(new PropertyValueFactory<MaterialTbResultData, String>("fileName"));
		filePathCl.setCellValueFactory(new PropertyValueFactory<MaterialTbResultData, String>("filePath"));
		fileSizeCl.setCellValueFactory(new PropertyValueFactory<MaterialTbResultData, String>("fileSize"));
		initcell(fileNameCl);
		initcell(filePathCl);
		initcell(fileSizeCl);
		if (IS_FIRST) {
			List<MaterialTb> materialTbs = jsonFile.getJsonToObject();
			materialTbResults = MaterialTbResultData.materialTbToReusltData(materialTbs);
		}
		materialTbList = FXCollections.observableArrayList(materialTbResults);
		materialTb.setItems(materialTbList);
	}

	
	public void initcell(TableColumn<MaterialTbResultData, String> tableColumn) {

		tableColumn.setCellFactory(new Callback<TableColumn<MaterialTbResultData, String>, TableCell<MaterialTbResultData, String>>() {
			public TableCell<MaterialTbResultData, String> call(TableColumn<MaterialTbResultData, String> pColumn) {
				return new TableCell<MaterialTbResultData, String>() {

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
	
	public void initDeleteFilePathBt() {
		deleteFilePathBt.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (selectNo == null) {
					new Alert(AlertType.INFORMATION, "请先选中文件", ButtonType.OK).showAndWait();
					return;
				}
				String fileName = materialTbList.get(selectNo.intValue()).getFileName();
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("删除物料表路径");
				alert.setContentText("确定删除文件名："+fileName+" 的物料表路径?");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					materialTbList.remove(selectNo.intValue());
				}
			}
		});
	}
	
	
	public void tableRowChangeListener() {
		materialTb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue != null && newValue.intValue() >= 0) {
					selectNo =  newValue.intValue();
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

	public ObservableList<MaterialTbResultData> getMaterialTbList() {
		return materialTbList;
	}
	
	
}
