import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import org.apache.commons.lang.ArrayUtils;

public class Smile {

	static SerialPort serialPort;
	Task task;

	JLabel lblCOMList;
	JComboBox ddlCOMList;
	JLabel lblProjectList;
	JComboBox ddlProjectList;

	JButton btnDownload;
	JButton btnFile;
	JProgressBar progressBar;
	JTextArea outputTextArea;
	JScrollPane scrollPane;

	static class SerialPortReader implements SerialPortEventListener {
		String xx;

		public void serialEvent(SerialPortEvent event) {
			if (event.isRXCHAR()) {

				try {
					Constant.RX = serialPort.readBytes(event.getEventValue());
					if (Constant.RX.length > 0) {
						if (Constant.RX[0] == -3)

						{
							if (Constant.TX.length != 0) {
								Constant.handleTX(Constant.TX);
								serialPort.writeBytes(Constant.TX_OneFrame);
							}
						} else {
							try {

								xx = new String(Constant.RX, "US-ASCII");
								System.out.print(xx);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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

	public void addComponentsToPane(Container pane) {
		pane.setLayout(null);

		lblProjectList = new JLabel("Project:");
		ddlProjectList = new JComboBox();
		ddlProjectList.addItem("C131_cluster");
		ddlProjectList.addItem("C131_BCMs");
		ddlProjectList.addItem("MQB_cluster");
		ddlProjectList.addItem("G_cluster");
		ddlProjectList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outPut("Switch to project "
						+ ddlProjectList.getSelectedItem().toString());
			}
		});

		lblCOMList = new JLabel("COM Port:");
		ddlCOMList = new JComboBox();
		for (int i = 0; i < Constant.portList.length; i++)
			ddlCOMList.addItem(Constant.portList[i]);

		ddlCOMList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (serialPort != null && serialPort.isOpened())
						serialPort.closePort();
				} catch (SerialPortException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				createSerialPort();
			}
		});

		btnDownload = new JButton("Download");
		btnFile = new JButton("...");

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		outputTextArea = new JTextArea("", 5, 20);
		scrollPane = new JScrollPane(outputTextArea);

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
		jp2.add(progressBar);

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
		progressBar.setBounds(10, 20, 350, btnFile.getPreferredSize().height);

		jp3.setLayout(null);
		jp3.setBounds(10, 220, 372, 120);
		scrollPane.setBounds(10, 20, 350, 88);

		/* Event Handler */
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser;
				chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Download file");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//
				// disable the "All files" option.
				//
				chooser.setAcceptAllFileFilterUsed(true);

				chooser.addChoosableFileFilter(new FileNameExtensionFilter(
						"JDL", "jdl"));

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					Constant.txtScript.setText(chooser.getSelectedFile()
							.toString());
					try {
						BufferedReader br;
						br = new BufferedReader(new FileReader(chooser
								.getSelectedFile().toString()));
						StringBuilder sb = new StringBuilder();
						String line = br.readLine();

						while (line != null) {
							sb.append(line);
							sb.append("\n");
							line = br.readLine();
						}
						Constant.wholeScript = sb.toString().trim();
						Constant.TX = Constant.wholeScript.getBytes();
						br.close();

						outPut(chooser.getSelectedFile().toString()
								+ " has been loaded");

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						outPut(e1.toString());
						e1.printStackTrace();
					}

				} else {
					outPut("No Selection!Please select file");
				}

			}
		});

		btnDownload.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {

				System.out.println("begin Transfering");

				try {
					
					if (Constant.TX.length != 0) {
						Constant.handleTX(Constant.TX);
						serialPort.writeBytes(Constant.TX_OneFrame);
					}

				} catch (SerialPortException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				task = new Task();
				task.start();
			}
		});
	}

	public void outPut(String output) {
		outputTextArea.setText(outputTextArea.getText() + output + "\n");
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
				outPut(ddlCOMList.getSelectedItem().toString()
						+ " has been connected!");
			} else
				outPut(ddlCOMList.getSelectedItem().toString()
						+ " can not be connected!");
		} catch (SerialPortException ex) {
			outPut(ex.toString());
		}

	}

	private void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("TinySmile");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set up the content pane.
		addComponentsToPane(frame.getContentPane());

		// Size and display the window.
		Insets insets = frame.getInsets();
		frame.setSize(400 + insets.left + insets.right, 380 + insets.top
				+ insets.bottom);
		frame.setVisible(true);
	}

	class Task extends Thread {
		public Task() {
		}

		public void run() {
			for (int i = 0; i <= 100; i += 10) {
				final int progress = i;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						progressBar.setValue(progress);
						outputTextArea.setText(outputTextArea.getText()
								+ String.format("Completed %d%% of task.\n",
										progress));
					}
				});
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public static void main(String[] args) {

		Constant.portList = jssc.SerialPortList.getPortNames();

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Smile smile = new Smile();
				smile.createAndShowGUI();
				smile.createSerialPort();

			}
		});
	}
}
