package org.usfirst.frc.team6619.robot.io;

import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public class SmartDashboardB {
	public static void run() {
		
		JRadioButton option1 = new JRadioButton();
		option1.setMnemonic(KeyEvent.VK_B);
		//option1.setActionCommand();
		
		ButtonGroup autonomouses = new ButtonGroup();
		autonomouses.add(option1);
		
	}
}
