package com.jimi.smt.eps.ghost.robot;

import java.awt.Robot;
import java.awt.event.KeyEvent;

/**
 * 辅助机器人进行输入
 * <br>
 * <b>2019年3月5日</b>
 * @author 几米物联自动化部-洪达浩
 */
public class RobotInputHelper {

	
	/**
	 * 输入一个整数，机器人依次执行按下键盘对应数字键的操作
	 * @param robot
	 * @param number
	 */
	public static final void inputNumber(Robot robot, int number) {
		String numberString = String.valueOf(number);
		for (char c : numberString.toCharArray()) {
			switch (c) {
			case '0':
				robot.keyPress(KeyEvent.VK_0);
				robot.keyRelease(KeyEvent.VK_0);
				break;
			case '1':
				robot.keyPress(KeyEvent.VK_1);
				robot.keyRelease(KeyEvent.VK_1);
				break;
			case '2':
				robot.keyPress(KeyEvent.VK_2);
				robot.keyRelease(KeyEvent.VK_2);
				break;
			case '3':
				robot.keyPress(KeyEvent.VK_3);
				robot.keyRelease(KeyEvent.VK_3);
				break;
			case '4':
				robot.keyPress(KeyEvent.VK_4);
				robot.keyRelease(KeyEvent.VK_4);
				break;
			case '5':
				robot.keyPress(KeyEvent.VK_5);
				robot.keyRelease(KeyEvent.VK_5);
				break;
			case '6':
				robot.keyPress(KeyEvent.VK_6);
				robot.keyRelease(KeyEvent.VK_6);
				break;
			case '7':
				robot.keyPress(KeyEvent.VK_7);
				robot.keyRelease(KeyEvent.VK_7);
				break;
			case '8':
				robot.keyPress(KeyEvent.VK_8);
				robot.keyRelease(KeyEvent.VK_8);
				break;
			case '9':
				robot.keyPress(KeyEvent.VK_9);
				robot.keyRelease(KeyEvent.VK_9);
				break;
			default:
				break;
			}
		}
	}	
	
}
