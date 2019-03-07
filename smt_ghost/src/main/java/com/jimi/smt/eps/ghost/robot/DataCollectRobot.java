package com.jimi.smt.eps.ghost.robot;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * 数据采集机器人<br>
 * 模拟用户操作
 * <br>
 * <b>2019年3月5日</b>
 * @author 几米物联自动化部-洪达浩
 */
public class DataCollectRobot {
	
	private int reporterInitDelay;//报表生成器初始化延迟
	
	private int reportCreatDelay;//报表生成延迟
	
	private int notepadLoadDelay;//记事本打开延迟

	
	public DataCollectRobot (File configFile) {
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(configFile));
			reporterInitDelay = Integer.parseInt(properties.getProperty("reporterInitDelay"));
			reportCreatDelay = Integer.parseInt(properties.getProperty("reportCreatDelay"));
			notepadLoadDelay = Integer.parseInt(properties.getProperty("notepadLoadDelay"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 通过模拟用户操作返回报表的html源码
	 * @param pastMinutes 范围：过去的N分钟内
	 * @return
	 */
	public String getData(int pastMinutes) {
		try {
			Robot robot = new Robot();
			openReporterWindow(robot);// 打开生产线报告器
			robot.delay(reporterInitDelay);// 等待：打开中...
			Date lastHourDatetime = getPastMinutesDatetime(pastMinutes);// 计算：N分钟之前的日期时间
			inputStartTime(robot, lastHourDatetime);// 输入开始范围
			openIEWindow(robot);// 生成报表
			robot.delay(reportCreatDelay);// 等待：生成中...
			openSourceWindow(robot);// 打开源码
			robot.delay(notepadLoadDelay);// 等待：打开记事本延迟
			String source = copySource(robot);// 复制源码
			closeWindows(robot);// 关闭窗口
			return source;// 返回：粘贴板内容
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	private void closeWindows(Robot robot) {
		//Alt + F4
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_F4);
		robot.keyRelease(KeyEvent.VK_F4);
		//Alt + F4
		robot.keyPress(KeyEvent.VK_F4);
		robot.keyRelease(KeyEvent.VK_F4);
		robot.keyRelease(KeyEvent.VK_ALT);
	}


	private String copySource(Robot robot) {
		//Ctrl + A
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_A);
		//Ctrl + C
		robot.keyPress(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		//返回粘贴板内容
		return ClipBoardUtil.getClipboardString();
	}


	private void openSourceWindow(Robot robot) {
		//Alt + F
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_F);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.keyRelease(KeyEvent.VK_F);
		//D
		robot.keyPress(KeyEvent.VK_D);
		robot.keyRelease(KeyEvent.VK_D);
	}


	private void openIEWindow(Robot robot) {
		//Alt + O
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_O);
		robot.keyRelease(KeyEvent.VK_O);
	}


	private void inputStartTime(Robot robot, Date lastHourDatetime) {
		//Alt + F
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_F);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.keyRelease(KeyEvent.VK_F);
		//输入年
		int year = lastHourDatetime.getYear() + 1900;
		RobotInputHelper.inputNumber(robot, year);
		//→
		robot.keyPress(KeyEvent.VK_RIGHT);
		robot.keyRelease(KeyEvent.VK_RIGHT);
		//输入月
		int month = lastHourDatetime.getMonth() + 1;
		RobotInputHelper.inputNumber(robot, month);
		//→
		robot.keyPress(KeyEvent.VK_RIGHT);
		robot.keyRelease(KeyEvent.VK_RIGHT);		
		//输入日
		int day = lastHourDatetime.getDate();
		RobotInputHelper.inputNumber(robot, day);
		//TAB
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
		//输入小时
		int hour = lastHourDatetime.getHours();
		RobotInputHelper.inputNumber(robot, hour);
	}


	private Date getPastMinutesDatetime(int pastMinutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, - pastMinutes);
		return calendar.getTime();
	}


	private void openReporterWindow(Robot robot) {
		//Alt + T
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_T);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.keyRelease(KeyEvent.VK_T);
		//R
		robot.keyPress(KeyEvent.VK_R);
		robot.keyRelease(KeyEvent.VK_R);
	}
	
	
//	public static void main(String[] args) throws InterruptedException {
//		DataCollectRobot dataCollectRobot = new DataCollectRobot(new File("config.ini"));
//		System.out.println("5秒后开始，请确保：导向器窗口处于前置激活状态！");
//		Thread.sleep(5000);
//		System.out.println("开始");
//		System.out.println(dataCollectRobot.getData());
//	}
	
}
