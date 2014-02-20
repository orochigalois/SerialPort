package com.ccjcfe.www;

import java.awt.Container;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker.StateValue;
import javax.swing.border.TitledBorder;



import java.io.UnsupportedEncodingException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import org.apache.commons.lang.ArrayUtils;

public class Smile {

	/*Actions*/
	private Action downloadAction;
	private Action loadAction;
	private Action chooseProjectAction;
	private Action chooseCOMAction;
	
	/*Components*/
	private JLabel lblCOMList;
	private JComboBox ddlCOMList;
	private JLabel lblProjectList;
	private JComboBox ddlProjectList;
	private JButton btnDownload;
	private JButton btnFile;
	//private JProgressBar progressBar;
	private JScrollPane scrollPane;
	
	/*Parsers object*/
	private JDF_Parser jdf_Parser;

	/*Serial Port object*/
	public static String[] portList;
	private static SerialPort serialPort;

	
	private void initActions() {
		downloadAction = new AbstractAction("Download") {

			private static final long serialVersionUID = 4669650683189592364L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				System.out.println("begin Transfering");
				toggleAllBtnWhenLoading(false);

				try {

					if (Constant.transferIndex < Constant.transferCount) {
						
						serialPort.writeBytes(Constant.TX.get(Constant.transferIndex).data);
						Constant.transferIndex++;
					}

				} catch (SerialPortException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		};

		loadAction = new AbstractAction("Load") {

			private static final long serialVersionUID = 4669650683189592364L;

			@Override
			public void actionPerformed(final ActionEvent e) {

				load();
				
				

			}
		};
		chooseProjectAction = new AbstractAction("ChooseProject") {

			private static final long serialVersionUID = 4669650683189592364L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				Constant.clearOutPut();
				Constant.outPut("Switch to project "
						+ ddlProjectList.getSelectedItem().toString());
			}
		};
		chooseCOMAction = new AbstractAction("ChooseCOM") {

			private static final long serialVersionUID = 4669650683189592364L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					if (serialPort != null && serialPort.isOpened())
						serialPort.closePort();
				} catch (SerialPortException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				createSerialPort();
			}
		};
	}
	
	
	public void initComponents(Container pane) {
		pane.setLayout(null);

		lblProjectList = new JLabel("Project:");
		ddlProjectList = new JComboBox();
		ddlProjectList.addItem("C131_cluster");
		ddlProjectList.addItem("C131_BCMs");
		ddlProjectList.addItem("MQB_cluster");
		ddlProjectList.addItem("G_cluster");
		ddlProjectList.addActionListener(chooseProjectAction);

		lblCOMList = new JLabel("COM Port:");
		ddlCOMList = new JComboBox();
		for (int i = 0; i < portList.length; i++)
			ddlCOMList.addItem(portList[i]);
		ddlCOMList.addActionListener(chooseCOMAction);
		
		
		
		btnFile = new JButton("...");
		btnFile.addActionListener(loadAction);
		
		btnDownload = new JButton("Download");
		btnDownload.addActionListener(downloadAction);

		//progressBar = new JProgressBar(0, 100);
		Constant.progressBar.setValue(0);
		Constant.progressBar.setStringPainted(true);

		scrollPane = new JScrollPane(Constant.outputTextArea);

		/* Add to Panel */
		JPanel jp0 = new JPanel();
		jp0.setBorder(new TitledBorder("Configuration"));
		pane.add(jp0);
		jp0.add(lblProjectList);
		jp0.add(ddlProjectList);
		jp0.add(lblCOMList);
		jp0.add(ddlCOMList);

		JPanel jp1 = new JPanel();
		jp1.setBorder(new TitledBorder("Download file"));
		pane.add(jp1);
		jp1.add(Constant.txtScript);
		jp1.add(btnFile);
		jp1.add(btnDownload);

		JPanel jp2 = new JPanel();
		jp2.setBorder(new TitledBorder("Progress"));
		pane.add(jp2);
		jp2.add(Constant.progressBar);

		JPanel jp3 = new JPanel();
		jp3.setBorder(new TitledBorder("Status"));
		pane.add(jp3);
		jp3.add(scrollPane);

		/* Layout */
		jp0.setLayout(null);
		jp0.setBounds(10, 10, 372, 60);
		lblProjectList.setBounds(10, 20, 60, btnFile.getPreferredSize().height);
		ddlProjectList
				.setBounds(70, 20, 120, btnFile.getPreferredSize().height);
		lblCOMList.setBounds(200, 20, 100, btnFile.getPreferredSize().height);
		ddlCOMList.setBounds(275, 20, 80, btnFile.getPreferredSize().height);

		jp1.setLayout(null);
		jp1.setBounds(10, 80, 372, 60);
		Constant.txtScript.setBounds(10, 20, 210,
				btnFile.getPreferredSize().height);
		btnFile.setBounds(225, 25, btnFile.getPreferredSize().width / 3 * 2,
				btnFile.getPreferredSize().height / 3 * 2);
		btnDownload.setBounds(270, 20, btnDownload.getPreferredSize().width,
				btnDownload.getPreferredSize().height);

		jp2.setLayout(null);
		jp2.setBounds(10, 150, 372, 60);
		Constant.progressBar.setBounds(10, 20, 350, btnFile.getPreferredSize().height);

		jp3.setLayout(null);
		jp3.setBounds(10, 220, 372, 120);
		scrollPane.setBounds(10, 20, 350, 88);


	}

	

	private void init() {

		JFrame frame = new JFrame("TinySmile");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initActions();
		initComponents(frame.getContentPane());
		

		Insets insets = frame.getInsets();
		frame.setSize(400 + insets.left + insets.right, 380 + insets.top
				+ insets.bottom);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	
	public void createSerialPort() {
		serialPort = new SerialPort(ddlCOMList.getSelectedItem().toString());
		try {
			serialPort.openPort();// Open port
			if (serialPort.isOpened()) {
				serialPort.setParams(256000, 8, 1, 0);// Set params
				int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS
						+ SerialPort.MASK_DSR;// Prepare mask
				serialPort.setEventsMask(mask);// Set mask
				serialPort.addEventListener(new SerialPortReader());// Add
																	// SerialPortEventListener
				Constant.outPut(ddlCOMList.getSelectedItem().toString()
						+ " has been connected!");
			} else
				Constant.outPut(ddlCOMList.getSelectedItem().toString()
						+ " can not be connected!");
		} catch (SerialPortException ex) {
			Constant.outPut(ex.toString());
		}

	}

	
	private void load() {
		// final String word = wordTextField.getText();
		// final File directory = new File(directoryPathTextField.getText());
		// messagesTextArea.setText("Searching for word '" + word +
		// "' in text files under: " + directory.getAbsolutePath()
		// + "\n");
		jdf_Parser = new JDF_Parser();
		jdf_Parser.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				System.out.println(event.getPropertyName());
				if (event.getPropertyName() == "progress") {
					Constant.progressBar.setIndeterminate(false);
					Constant.progressBar.setValue((Integer) event.getNewValue());
				} else if (event.getPropertyName() == "state") {
					switch ((StateValue) event.getNewValue()) {
					case DONE:
						Constant.wholeScript2TX();
						Constant.transferCount=Constant.TX.size();
						Toolkit.getDefaultToolkit().beep();
						toggleAllBtnWhenLoading(true);
						
						jdf_Parser = null;
						
						
						 //Constant.test();
						 
						break;
					case STARTED:
					case PENDING:
						
						toggleAllBtnWhenLoading(false);
						Constant.progressBar.setIndeterminate(true);
						break;
					}
				}

			}
		});
		
		if(jdf_Parser.loadFile())
		{
			jdf_Parser.execute();
		}
	}

	private void toggleAllBtnWhenLoading(boolean state) {
		btnDownload.setEnabled(state);
		btnFile.setEnabled(state);
		ddlCOMList.setEnabled(state);
		ddlProjectList.setEnabled(state);

	}


	
	
	public static void main(String[] args) {

		portList = jssc.SerialPortList.getPortNames();

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Smile smile = new Smile();
				smile.init();
				smile.createSerialPort();

			}
		});
	}

	static class SerialPortReader implements SerialPortEventListener {
		String xx;

		public void serialEvent(SerialPortEvent event) {
			if (event.isRXCHAR()) {

				try {
					Constant.RX = serialPort.readBytes(event.getEventValue());
					if (Constant.RX.length > 0) {
						if (Constant.RX[0] == -3)

						{
							if (Constant.transferIndex < Constant.transferCount) {

								serialPort.writeBytes(Constant.TX.get(Constant.transferIndex).data);
								Constant.transferIndex++;
							}
							
							Constant.progressBar.setValue(100*Constant.transferIndex/Constant.transferCount);
						} else {
							
								Constant.printRX();

//								xx = new String(Constant.RX, "US-ASCII");
//								System.out.print(xx);
							
						}
					}

				} catch (SerialPortException ex) {
					System.out.println(ex);
				}

			} else if (event.isCTS()) {// If CTS line has changed state
				if (event.getEventValue() == 1) {// If line is ON
					System.out.println("CTS - ON");
				} else {
					System.out.println("CTS - OFF");
				}
			} else if (event.isDSR()) {// /If DSR line has changed state
				if (event.getEventValue() == 1) {// If line is ON
					System.out.println("DSR - ON");
				} else {
					System.out.println("DSR - OFF");
				}
			}
		}
	}
}
