package com.ccjcfe.www;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang.ArrayUtils;


public class Constant {
	
	/*Log area*/
	public static JTextArea outputTextArea = new JTextArea("", 5, 20);
	public static void outPut(String output) {
		outputTextArea.setText(outputTextArea.getText() + output + "\n");
	}
	public static void clearOutPut() {
		outputTextArea.setText("");
	}
	
	public static JTextField txtScript = new JTextField();
	public static String wholeScript="";
	
	public static byte[] RX;

	public static List<TX_OneFrame> TX = new ArrayList<TX_OneFrame>();
	
	public static int transferIndex=0;
	public static int transferCount=0;

	

	
	
	
	
	
//	public static void wholeScript2TX()
//	{
//		while(wholeScript.length()>0)
//		{
//			String tmp=wholeScript.substring(0, 2);
//			int parseInt = Integer.parseInt(tmp, 16);
//			byte b = (byte)parseInt;
//			TX.add(b);
//			
//			wholeScript=wholeScript.substring(2);
//		
//		}
//	}
	
	public static void wholeScript2TX()
	{
		String s4096;
		String s2;
		while(wholeScript.length()>=4096)
		{
			s4096=wholeScript.substring(0, 4096);
			TX_OneFrame tx_OneFrame = new TX_OneFrame();
			int i=0;
			while(s4096.length()>0)
			{
				s2=s4096.substring(0, 2);
				tx_OneFrame.data[i]=(byte)Integer.parseInt(s2, 16);
				i++;
				
				s4096=s4096.substring(2);
			}
			TX.add(tx_OneFrame);
			
			wholeScript=wholeScript.substring(4096);

		}
		
		//padding
		while(wholeScript.length()<4096)
		{
			wholeScript=wholeScript+"AB";
		}
		
		//last frame
		TX_OneFrame tx_LastFrame = new TX_OneFrame();
		int j=0;
		while(wholeScript.length()>0)
		{
			s2=wholeScript.substring(0, 2);

			tx_LastFrame.data[j]=(byte)Integer.parseInt(s2, 16);
			j++;
			
			wholeScript=wholeScript.substring(2);
		}
		TX.add(tx_LastFrame);
		
	}
	
	public static void test()
	{
		BufferedWriter bw;

		try {
			bw = new BufferedWriter(new FileWriter(new File("D:\\test.txt")));
			while(wholeScript.length()>0)
			{
				bw.write(wholeScript.substring(0, 16));
				bw.newLine();
				
				wholeScript=wholeScript.substring(16);
			
			}
			
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void printRX()
	{
		
		byte[] bytes = {-1, 0, 1, 2, 3 };
	    StringBuilder sb = new StringBuilder();
	    for (byte b : RX) {
	        sb.append(String.format("%02X ", b));
	    }
	    System.out.println(sb.toString());
		
	}
	
	public static JProgressBar progressBar = new JProgressBar(0, 100);
	

	
}

class TX_OneFrame
{
	byte[] data =new byte[2048];
}