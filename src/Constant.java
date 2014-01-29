import javax.swing.JTextField;

import org.apache.commons.lang.ArrayUtils;


public class Constant {
	public static String[] portList;
	
	public static JTextField txtScript = new JTextField();
	public static String wholeScript="";
	
	public static byte[] RX;
	public static byte[] TX;
	
	public static byte TX_OneFrame[] = new byte[2048];
	
	public static void handleTX(byte[] TX)
	{
		int loop=Constant.TX.length<2048?Constant.TX.length:2048;
		for(int i=0;i<loop;i++)
		{
			Constant.TX_OneFrame[i]=Constant.TX[i];
			
		}
		for(int i=0;i<loop;i++)
		{
			Constant.TX = ArrayUtils.remove(Constant.TX, 0);
		}
	}
	
}
