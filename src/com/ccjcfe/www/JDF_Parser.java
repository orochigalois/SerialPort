package com.ccjcfe.www;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.StringUtils;

public class JDF_Parser extends SwingWorker<Void, Void>{

	ArrayList<String> fileArray = new ArrayList<String>();

	String currentDir;

	public boolean loadFile() {

		JFileChooser chooser;
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Download file");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//
		// disable the "All files" option.
		//
		chooser.setAcceptAllFileFilterUsed(true);

		chooser.addChoosableFileFilter(new FileNameExtensionFilter("JDL", "jdl"));

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			Constant.txtScript.setText(chooser.getSelectedFile().toString());

			File file = new File(chooser.getSelectedFile().toString());
			currentDir = file.getParent();
			try {
				BufferedReader br;
				br = new BufferedReader(new FileReader(chooser
						.getSelectedFile().toString()));

				String line = br.readLine();

				while (line != null) {

					fileArray.add(line);

					line = br.readLine();
				}

				br.close();

				Constant.outPut("Succeed in loading "+chooser.getSelectedFile().toString()
						);

			} catch (IOException e1) {
	
				Constant.outPut(e1.toString());
				e1.printStackTrace();
				return false;
			}
			return true;

		} else {
			Constant.outPut("No Selection!Please select file");
			return false;
		}

	}

	@Override
	protected Void doInBackground() throws Exception {
		
		for (int i = 0; i < fileArray.size(); i++) {
			Constant.outPut("----- Start loading "+fileArray.get(i)+" -----");
			if (openEachJDF(currentDir + "\\" + fileArray.get(i))!=true)
			{
				Constant.outPut(currentDir + "\\" + fileArray.get(i)+" Parser Error!");
				break;
			}
			else
			{
				Constant.outPut( "Succeed in loading " + fileArray.get(i)+"!");
			}
		}
		return null;
	}

	public int count(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	public boolean openEachJDF(String file) {

		ArrayList<DataBlock> dataBlockArray = new ArrayList<DataBlock>();
		
		String logicalZone = "";
		
		BufferedReader br;
		String line = null;

		int i = 0;
		boolean bFirstLine = true;
		
		
		
		
		//for progress bar
		int indexForProgress=0;
		int totalcount=0;
		try {
			totalcount = count(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			/* Step1: Jdf -> dataBlockArray */
			StringBuilder sbContent = new StringBuilder();
			
			br = new BufferedReader(new FileReader(file));
			line = br.readLine();
			

			while (line != null) {

				
				LineObject lineObject = new LineObject();
				if (line.charAt(0) != 'S') {
					Constant.outPut("Error!File format is not correct!");
					return false;
				}

				lineObject.type = line.charAt(1);

				lineObject.lng = Integer.parseInt(line.substring(2, 4), 16);

				switch (lineObject.type) {
				case '0':
					lineObject.lng -= 3;
					break;
				case '1':
				case '9':
					lineObject.lng -= 3;
					break;
				case '2':
				case '8':
					lineObject.lng -= 4;
					break;
				case '3':
				case '7':
					lineObject.lng -= 5;
					break;
				case '5':
					lineObject.lng -= 1;
					break;
				}
				if (lineObject.lng < 0) {
					Constant.outPut("CFileMotorola::ReadLine>Line length defined in the Jdf file is wrong");
					return false;
				}

				switch (lineObject.type) {
				case '0':
				case '1':
				case '9':
					i = 4;
					break;
				case '2':
				case '8':
					i = 6;
					break;
				case '3':
				case '7':
					i = 8;
					break;
				case '5':
					i = 0;
				}

				lineObject.address = Integer.parseInt(line.substring(4, 4 + i),
						16);

				lineObject.buffer = line.substring(4 + i, 4 + i + 2
						* lineObject.lng);

				if (lineObject.type == '0') {
					if (lineObject.lng < 1) {
						Constant.outPut("CFileJdf::Open>Bad line - length is 0");
						return false;
					}
					switch (Integer.parseInt(lineObject.buffer.substring(0, 2),
							16)) {
					case 1:
						if (lineObject.lng != 2) {
							Constant.outPut("CFileJdf::Open>Invalid length for S0 with descriptor 1");
							return false;
						}
						logicalZone = lineObject.buffer.substring(2, 4);
						break;
					default:
						Constant.outPut("CFileJdf::Open>File too new");
						return false;
					}
				} else {
					if ((lineObject.type == '1') || (lineObject.type == '2')
							|| (lineObject.type == '3')) {
						DataBlock dataBlock = new DataBlock();
						if (bFirstLine != false) {
							bFirstLine = false;
							dataBlock.startAddress = lineObject.address;
							dataBlock.uncompressedSize = lineObject.lng;
							sbContent.append(lineObject.buffer);
							
							dataBlockArray.add(dataBlock);

						} else {

							if (dataBlockArray.get(dataBlockArray.size() - 1).startAddress
									+ dataBlockArray
											.get(dataBlockArray.size() - 1).uncompressedSize < lineObject.address) {
								
								dataBlockArray.get(dataBlockArray.size() - 1).content=sbContent.toString();
								sbContent.delete(0, sbContent.length());
								
								dataBlock.startAddress = lineObject.address;
								dataBlock.uncompressedSize = lineObject.lng;
								sbContent.append(lineObject.buffer);
							
								dataBlockArray.add(dataBlock);
								
								
							}

							else {
								dataBlockArray.get(dataBlockArray.size() - 1).uncompressedSize += lineObject.lng;
								
								sbContent.append(lineObject.buffer);
								

							}

						}
					}

				}
				

				line = br.readLine();
				
				
				//update progress bar
				indexForProgress++;
				setProgress(100*(indexForProgress)/totalcount);
			}
			
			if(sbContent.toString()!="")
				dataBlockArray.get(dataBlockArray.size() - 1).content=sbContent.toString();
			
			

			/* Step2: dataBlockArray -> Constant.wholeScript */
			StringBuilder sb = new StringBuilder();

			sb.append("053101FF00" + logicalZone + "C2C2");// erase zone
			for (int j = 0; j < dataBlockArray.size(); j++) {

				String Hex1 = StringUtils
						.leftPad(
								Integer.toHexString(dataBlockArray.get(j).startAddress),
								8, "0");
				String Hex2 = StringUtils.leftPad(Integer
						.toHexString(dataBlockArray.get(j).uncompressedSize),
						8, "0");
				sb.append("100B340044" + Hex1.substring(0, 6));
				sb.append("21" + Hex1.substring(6, 8) + Hex2 + "C2C2");
				
				
				//2.1 handle every 256 frame
				String HexContent = dataBlockArray.get(j).content;
				int blockNum = 1;
				while (HexContent.length()/2 > 256) {
					String oneFrame = HexContent.substring(0, 512);
					sb.append("110236"
							+ StringUtils.leftPad(
									Integer.toHexString(blockNum), 2, "0")
							+ oneFrame.substring(0, 8));
					oneFrame = oneFrame.substring(8);
					int startIndex = 0x21;
					while (oneFrame.length() > 0) {
						sb.append(Integer.toHexString(startIndex)
								+ oneFrame.substring(0, 14));
						oneFrame = oneFrame.substring(14);
						startIndex++;
						if (startIndex > 0x2F)
							startIndex = 0x20;

					}

					HexContent = HexContent.substring(512);
					blockNum++;
					if(blockNum>255)
						blockNum=0;
					


				}
				//2.2 handle the last frame
				String lastContent = HexContent;
				if (lastContent.length() <= 8) {
					String beforePad="1"
							+ StringUtils.leftPad(Integer
									.toHexString(lastContent.length()/2 + 2), 3,
									"0")
							+ "36"
							+ StringUtils.leftPad(
									Integer.toHexString(blockNum), 2, "0")
							+ lastContent;
					sb.append(pad_with_C2(beforePad));
				}
				else
				{
					sb.append("1"
							+ StringUtils.leftPad(Integer
									.toHexString(lastContent.length()/2 + 2), 3,
									"0")
							+"36"
							+ StringUtils.leftPad(
									Integer.toHexString(blockNum), 2, "0")
							+ lastContent.substring(0, 8));
					
					
					lastContent = lastContent.substring(8);
					int Index = 0x21;
					while (lastContent.length() > 0) {
						if(lastContent.length()>=14)
						{
							sb.append(Integer.toHexString(Index)
									+ lastContent.substring(0, 14));
							lastContent = lastContent.substring(14);
						}
						else
						{
							sb.append(pad_with_C2(
									Integer.toHexString(Index)
									+ lastContent
									));
							lastContent = "";
						
						}
						Index++;
						if (Index > 0x2F)
							Index = 0x20;

					}
				
				}
				
				
				sb.append("0137C2C2C2C2C2C2");//cDiagMasterCodeTransferExit

			}
			
			sb.append("053101FF02" + logicalZone + "C2C2");// cRoutineSelfCheck

			Constant.wholeScript += sb.toString();
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;

	}
	
	String pad_with_C2(String str)
	{
		if(str.length()>=16)
			return str;
		else
		{
		
			while(str.length()<16)
			{
				str=str+"C2";
			}
			return str;
		
		}
			
		
	}

	
}

class LineObject {
	char type;
	int address;
	int lng;
	String buffer;
}

class DataBlock {
	int startAddress;
	int uncompressedSize;
	String content;
}
