package Client;

import java.io.*;

public class MessageReceiver implements Runnable
{
private DataInputStream in;

public MessageReceiver(DataInputStream i)
{
	in = i;
}
@Override
public void run()
{
	//System.out.println("run");
	while(true)
	{
		try
		{
			String msg = in.readUTF();
			System.out.println("msg:" + msg);
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
		}
	}
}
}
