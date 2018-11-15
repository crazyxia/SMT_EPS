
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

public class ChangePwdController implements Initializable {

	private static String CHANGE_PWD_ACTION = "/user/updatePassword";

	@FXML
	private TextField originPwdTf;
	@FXML
	private PasswordField modifyPwdTf;
	@FXML
	private PasswordField comfirmPwdTf;
	@FXML
	private Button changePwdBt;

	private String loginUserId;
	private HttpHelper httpHelper = HttpHelper.me;
	private Stage stage;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}


	private void init() {
		originPwdTf.setText("");
		modifyPwdTf.setText("");
		comfirmPwdTf.setText("");
	}


	public void onChangePwdBtClick() {
		String oldPassword = originPwdTf.getText();
		String newPassword = modifyPwdTf.getText();
		String comfirmPassword = comfirmPwdTf.getText();
		// 对密码进行初步判断
		if (oldPassword == null || oldPassword.equals("")) {
			new Alert(AlertType.WARNING, "原本密码不能为空", ButtonType.OK).showAndWait();
			return;
		}
		if (newPassword == null || newPassword.equals("")) {
			new Alert(AlertType.WARNING, "修改密码不能为空", ButtonType.OK).showAndWait();
			return;
		}
		if (comfirmPassword == null || comfirmPassword.equals("")) {
			new Alert(AlertType.WARNING, "确认密码不能为空", ButtonType.OK).showAndWait();
			return;
		}
		if (!newPassword.equals(comfirmPassword)) {
			new Alert(AlertType.WARNING, "修改密码必须和确认密码相一致", ButtonType.OK).showAndWait();
			return;
		}
		// 初步判断成功否封锁UI
		originPwdTf.setDisable(true);
		modifyPwdTf.setDisable(true);
		comfirmPwdTf.setDisable(true);
		changePwdBt.setDisable(true);
		// 填充修改密码接口的参数
		Map<String, String> map = new HashMap<>();
		map.put("id", loginUserId);
		map.put("oldPassword", oldPassword);
		map.put("newPassword", newPassword);
		try {
			httpHelper.requestHttp(CHANGE_PWD_ACTION, map, new Callback() {

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					JSONObject object = null;
					String responseString = response.body().string();
					try {
						System.err.println(responseString);
						object = (JSONObject) JSONArray.parse(responseString);
					} catch (Exception e) {
						showAlertAndOpenControlls("修改密码失败，回复解析错误");
						e.printStackTrace();
					}

					try {
						int resultCode = object.getInteger("code");
						// 修改密码成功
						if (resultCode == 200) {
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									// 解锁UI，关闭界面
									originPwdTf.setDisable(false);
									modifyPwdTf.setDisable(false);
									comfirmPwdTf.setDisable(false);
									changePwdBt.setDisable(false);
									getStage().close();
								}
							});
						} else {
							String result = object.getString("msg");
							showAlertAndOpenControlls(result);
						}
					} catch (Exception e) {
						showAlertAndOpenControlls("修改密码失败，回复解析错误");
						e.printStackTrace();
					}
				}


				@Override
				public void onFailure(Call call, IOException e) {
					showAlertAndOpenControlls("网络错误，请检查你的网络连接");
					e.printStackTrace();
				}
			});

		} catch (IOException e) {
			showAlertAndOpenControlls("网络错误，请检查你的网络连接");
			e.printStackTrace();
			// return false;
		}
		init();
	}


	public String getLoginUserId() {
		return loginUserId;
	}


	public void setLoginUserId(String loginUserId) {
		this.loginUserId = loginUserId;
	}


	/**
	 * 显示错误提示并解锁UI
	 * 
	 * @param message
	 */
	public void showAlertAndOpenControlls(final String message) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				new Alert(AlertType.ERROR, message, ButtonType.OK).showAndWait();
				originPwdTf.setDisable(false);
				modifyPwdTf.setDisable(false);
				comfirmPwdTf.setDisable(false);
				changePwdBt.setDisable(false);
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
