package Client;
import java.net.*;
import java.io.*;
import java.util.*;

public class Client
{
String hostAddress;
int port;
Socket socket;
DataInputStream in;
DataOutputStream out;

public Client(String h, int p)
{
	hostAddress = h;
	port = p;
	try
	{
		socket = new Socket(hostAddress, port);
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		//send name
		while(true)
		{
			String msg = in.readUTF();
			if(msg.equals("ack"))
			{
				System.out.println("Please enter your name");
			}
			
			Scanner scanner = new Scanner(System.in);
			msg = scanner.nextLine();
			out.writeUTF(msg);
			msg = in.readUTF();
			if(msg.equals("ack"))
			{
				System.out.println("Welcome");
				Thread outThread = new Thread(new MessageSender(out));
				outThread.start();
				Thread inThread = new Thread(new MessageReceiver(in));
				inThread.start();
				break;
			}
		}
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
	}
}

/*@Override
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
}*/
}

