package com.jimi.smt.eps.display.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import com.jimi.smt.eps.display.controller.DisplayController;

import cc.darhao.dautils.api.ResourcesUtil;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

	private DisplayController displayController;

	private static final String VERSION = "2.4.0";

	private static final String NAME = "SMT-Display_";

	private static final String TYPE = ".jar";
	// 日志记录
	private Logger logger = LogManager.getRootLogger();

	
	@Override
	public void start(Stage primaryStage) {
		try {
			if (isRunning()) {
				new Alert(AlertType.WARNING, "另一个实例已经在运行中，请勿重复运行！", ButtonType.OK).show();
				new Thread(() -> {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.exit(0);
				}).start();
			} else {
				FXMLLoader loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/Display.fxml"));
				Parent root = loader.load();
				displayController = loader.getController();
				displayController.closeWindow(primaryStage);
				displayController.scenceChangeListener(primaryStage);
				Scene scene = new Scene(root, 800, 600);
				scene.getStylesheets().add(ResourcesUtil.getResourceURL("css/application.css").toExternalForm());
				primaryStage.getIcons().add(new Image(ResourcesUtil.getResourceURL("image/smt.png").openStream()));
				primaryStage.setTitle("产线实时监控-V" + VERSION);
				primaryStage.setScene(scene);
				primaryStage.setMinWidth(800);
				primaryStage.setMinHeight(600);
				primaryStage.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			System.exit(0);

		}
	}

	
	public static void main(String[] args) throws IOException {
		ConfigurationSource source;
		try {
			source = new ConfigurationSource(ResourcesUtil.getResourceAsStream("log4j/log4j.xml"));
			Configurator.initialize(null, source);
		} catch (IOException e) {
			e.printStackTrace();
		}
		launch(args);
	}

	
	/**
	 * 判断是否已经有一个实例在运行
	 * 
	 * @return
	 * @throws IOException
	 */
	public static boolean isRunning() throws IOException {
		String line = null;
		InputStream in = Runtime.getRuntime().exec("jps").getInputStream();
		BufferedReader b = new BufferedReader(new InputStreamReader(in));
		int count = 0;
		while ((line = b.readLine()) != null) {
			if (line.contains(NAME) && line.contains(TYPE)) {
				count++;
				if (count > 1) {
					return true;
				}
			}
		}
		return false;
	}

	
	public static String getVersion() {
		return VERSION;
	}

}
