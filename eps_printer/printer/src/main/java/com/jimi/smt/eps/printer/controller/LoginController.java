package com.jimi.smt.eps.printer.controller;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.Result;
import com.jimi.smt.eps.printer.app.Main;
import com.jimi.smt.eps.printer.entity.StockLog;
import com.jimi.smt.eps.printer.util.HttpHelper;

import cc.darhao.dautils.api.ResourcesUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;

public class LoginController implements Initializable{
	
	private static String LOGIN_ACTION = "/user/login";
	private HttpHelper httpHelper = HttpHelper.me;
	private MainController mainController;
	
	@FXML
	private TextField userIdTf;
	@FXML
	private PasswordField userPwdTf;
	@FXML
	private Button loginBt;
	
	private Stage primaryStage;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Init();
		loginButtonClickListener();
		loginBt.setDisable(false);
		userIdTf.setDisable(false);
		userPwdTf.setDisable(false);
	}

	private void Init() {
		userIdTf.setText("");
		userPwdTf.setText("");
	}
	
	
	public void loginButtonClickListener() {
		loginBt.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				String id = userIdTf.getText();
				String pwd = userPwdTf.getText();
				if (id == null || id.equals("")) {
					new Alert(AlertType.WARNING, "工号不能为空", ButtonType.OK).showAndWait();
					return;
				}
				if (pwd == null || pwd.equals("")) {
					new Alert(AlertType.WARNING, "密码不能为空", ButtonType.OK).showAndWait();
					return;
				}
				userIdTf.setDisable(true);
				userPwdTf.setDisable(true);
				loginBt.setDisable(true);
				login(id, pwd);
			}
		});
	}
	
	
	/**
	 * 登录功能
	 * @param id
	 * @param pwd
	 * @return
	 */
	private void login(String id, String pwd) {
		Map<String, String> map = new HashMap<>();
		map.put("id", id);
		map.put("password", pwd);
		try {
			httpHelper.requestHttp(LOGIN_ACTION, map, new Callback() {
				
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String responseString = response.body().string();
					JSONObject object = (JSONObject) JSONArray.parse(responseString);
					try {
						int resultCode = object.getInteger("result");
						if (resultCode == 200) {
							//成功登录
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									FXMLLoader loader;
									try {
										loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/app.fxml"));
										Parent root = loader.load();
										// 把Stage存入MainController
										mainController = loader.getController();
										Stage stage = new Stage();
										mainController.setPrimaryStage(stage);
										mainController.setLoginUserId(id);
										userIdTf.setDisable(false);
										userPwdTf.setDisable(false);
										loginBt.setDisable(false);
										stage.setTitle("防错料系统 - 条码打印器 " + Main.getVersion());
										stage.setScene(new Scene(root));
										stage.show();
										mainController.closeWindow(stage);
										getPrimaryStage().close();
									} catch (IOException e) {
										e.printStackTrace();
									}
									
								}
							});
							
						}else {
							showAlert("登录失败");
						}
					} catch (NumberFormatException e) {
						try {
							String result = object.getString("result");
							if (result.equals("failed_wrong_password")) {
								showAlert("用户名或密码错误");
							}else if(result.equals("failed_not_found")){
								showAlert("用户不存在");
							}else if(result.equals("failed_not_enabled")) {
								showAlert("用户已被禁用");
							}
						} catch (Exception e2) {
							showAlert("回复解析错误");
							e.printStackTrace();
						}
					}
					
				}
				
				@Override
				public void onFailure(Call call, IOException e) {
					showAlert("网络错误，请检查你的网络连接");
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			showAlert("网络错误，请检查你的网络连接");
			e.printStackTrace();
		}
	}

	
	/**
	 * 显示登录提示警告框
	 * @param message
	 */
	public void showAlert(final String message){
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				new Alert(AlertType.ERROR, message, ButtonType.OK).showAndWait();
				userIdTf.setDisable(false);
				userPwdTf.setDisable(false);
				loginBt.setDisable(false);
			}
		});
		
	}
	
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		// TODO Auto-generated method stub
		this.primaryStage = primaryStage;
	}
	
	
	/**
	 * 关闭登录界面事件，以保证快速关闭，不会因为http的异步调用而出现看似关闭实则还有线程在运行的状态
	 * @param stage
	 */
	public void colseWindows(Stage stage) {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
					System.exit(0);
				
			}
		});
	}
}
