package Ftp;

public class FtpClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			String[] s = { "-s", "-b", "localhost:2221", "IMG", "IMG", "aa.jpg", "/home/jack1/myLogo.jpg" };
			new FtpClient(s);
		}
		catch(Exception e){}
	}

}
