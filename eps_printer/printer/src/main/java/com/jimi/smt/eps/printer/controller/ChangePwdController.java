package com.jimi.smt.eps.printer.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jimi.smt.eps.printer.util.HttpHelper;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ChangePwdController  implements Initializable {
	
	@FXML
	private TextField originPwdTf;
	@FXML
	private PasswordField modifyPwdTf;
	@FXML
	private PasswordField comfirmPwdTf;
	@FXML
	private Button modifyPwdBt;
	
	private String loginUserId;
	
	private HttpHelper httpHelper = HttpHelper.me;
	private static String CHANGE_PWD_ACTION = "/user/updatePassword";
	
	private Stage stage;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		init();
		modifyPwdBtListener();
	}
	
	private void init() {
		originPwdTf.setText("");
		modifyPwdTf.setText("");
		comfirmPwdTf.setText("");
	}
	
	private void modifyPwdBtListener() {
		modifyPwdBt.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				String oldPassword = originPwdTf.getText();
				String newPassword = modifyPwdTf.getText();
				String comfirmPassword = comfirmPwdTf.getText();
				if (oldPassword == null || oldPassword.equals("")) {
					new Alert(AlertType.WARNING, "原本密码不能为空", ButtonType.OK).showAndWait();
					return ;
				}
				if (newPassword == null || newPassword.equals("")) {
					new Alert(AlertType.WARNING, "修改密码不能为空", ButtonType.OK).showAndWait();
					return ;
				}
				if (comfirmPassword == null || comfirmPassword.equals("")) {
					new Alert(AlertType.WARNING, "确认密码不能为空", ButtonType.OK).showAndWait();
					return ;
				}
				if (!newPassword.equals(comfirmPassword)) {
					new Alert(AlertType.WARNING, "修改密码必须和确认密码相一致", ButtonType.OK).showAndWait();
					return ;
				}
				modifyPwd(loginUserId, oldPassword, newPassword);
				init();
			}
		});
	}
	
	private void modifyPwd(String id, String originPwd, String password) {
		originPwdTf.setDisable(true);
		modifyPwdTf.setDisable(true);
		comfirmPwdTf.setDisable(true);
		modifyPwdBt.setDisable(true);
		Map<String, String> map = new HashMap<>();
		map.put("id", id);
		map.put("oldPassword", originPwd);
		map.put("newPassword", password);
		try {
			httpHelper.requestHttp(CHANGE_PWD_ACTION, map, new Callback() {
				
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					JSONObject object = (JSONObject) JSONArray.parse(response.body().string());
					try {
						int resultCode = object.getInteger("code");
						if (resultCode == 200) {
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									originPwdTf.setDisable(false);
									modifyPwdTf.setDisable(false);
									comfirmPwdTf.setDisable(false);
									modifyPwdBt.setDisable(false);
									getStage().close();
								}
							});
						}else {
							String result = object.getString("msg");
							showAlert(result);
						}
					} catch (Exception e) {
						showAlert("回复解析错误");
						e.printStackTrace();
					}
				}
				
				@Override
				public void onFailure(Call call, IOException e) {
					// TODO Auto-generated method stub
					
				}
			});
			
		} catch (IOException e) {
			showAlert("网络错误，请检查你的网络连接");
			e.printStackTrace();
			//return false;
		}
	}

	
	public String getLoginUserId() {
		return loginUserId;
	}

	
	public void setLoginUserId(String loginUserId) {
		this.loginUserId = loginUserId;
	}

	public void showAlert(final String message){
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				new Alert(AlertType.ERROR, message, ButtonType.OK).showAndWait();
				originPwdTf.setDisable(false);
				modifyPwdTf.setDisable(false);
				comfirmPwdTf.setDisable(false);
				modifyPwdBt.setDisable(false);
			}
		});
		
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	
}
