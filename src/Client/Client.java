package Client;

import Gui.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javafx.application.*;
import javafx.concurrent.*;

public class Client
{
private MainGui mainGui;
String hostAddress;
int port;
String username;
Socket socket;
DataInputStream in;
DataOutputStream out;

public Client(MainGui mg, String h, int p)
{
	mainGui = mg;
	hostAddress = h;
	port = p;
	//username = un;
}
public boolean connect(String un)
{
	username = un;
	try
	{
		socket = new Socket(hostAddress, port);
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		//send name
		String msg = in.readUTF();
		System.err.println(msg);
		if(msg.equals("ack"))
		{
			out.writeUTF(username);
			msg = in.readUTF();
			System.err.println(msg);
			if(msg.equals("ack"))
			{
				System.out.println("Welcome");
				//Thread outThread = new Thread(new MessageSender(out));
				//outThread.start();
				Thread inThread = new Thread(new MessageReceiver(in, this));
				inThread.start();
				//Platform.runLater(new MessageReceiver(in, this));
				return true;
			}
		}
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
	}
	return false;
}

/***************************************
 *             util                    *
 ***************************************/
public String getName()
{
	return username;
}

public RoomPage getRoomPage(String roomname)
{
	return mainGui.getRoomPage(roomname);
}

public MainPage getMainPage()
{
	return mainGui.getMainPage();
}

public DataOutputStream getOutputStream()
{
	return out;
}

public DataInputStream getInputStream()
{
	return in;
}

public void showMessageDialog(String msg)
{
	mainGui.showMessageDialog(msg);
}

/***************************************
 *       send methods                  *
 ***************************************/
public void sendToMainRoom(String msg)
{
	try
	{
		out.writeUTF("m//" + getName() + " says: " + msg);
	}
	catch(Exception e){}
}
public boolean sendToRoom(String roomname, String msg)
{
	try
	{
		out.writeUTF("r/" + roomname + "/" + msg);
		return true;
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
		e.printStackTrace();
		System.err.println("Cannot send to room: " + roomname);
	}
	return false;
}

public boolean sendToUser(String un, String msg)
{
	try
	{
		out.writeUTF("s/" + un + "/" + msg);
		return true;
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
		e.printStackTrace();
		System.err.println("Cannot send to user: " + un);
	}
	return false;
}

/***************************************
 *       room methods                  *
 ***************************************/
public boolean createRoomRequest(String roomname)
{
	try
	{
		out.writeUTF("c/" + roomname + "//");
		return true;
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
		e.printStackTrace();
		System.err.println("Cannot create room: " + roomname);
	}
	return false;
}

public void roomCreated(String rn, String un)
{
	try
	{
		mainGui.getMainPage().addRoom(rn);
	}
	catch(Exception e){}
}

public boolean joinRoomRequest(String roomname)
{
	try
	{
		out.writeUTF("j/" + roomname + "/" + getName());
		return true;
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
		e.printStackTrace();
		System.err.println("Cannot join room: " + roomname);
	}
	return false;
}

public void joinRoom(String rn, String[] ul)
{
	mainGui.addRoomTab(rn, ul);
}

public void addUser(String rn, String un)
{
	try
	{
		if(rn.equals("Main Page"))
		{
			mainGui.getMainPage().addUser(un);
		}
		else
		{
			mainGui.getRoomPage(rn).addUser(un);
		}
	}
	catch(Exception e){}
}

public void removeUser(String rn, String un)
{
	System.err.println("client removeUser");
	try
	{
		if(rn.equals("Main Page"))
		{
			mainGui.getMainPage().removeUser(un);
		}
		else
		{
			mainGui.getRoomPage(rn).removeUser(un);
		}
	}
	catch(Exception e){}
}

public void getJoinReply(String rn, String un)
{
	mainGui.getJoinReply(rn, un);
}

public void joinReply(String rn, String un, boolean r)
{
	try
	{
		if(r)
		{
			out.writeUTF("y/" + rn + "/" + un);
		}
		else
		{
			out.writeUTF("n/" + rn + "/" + un);
		}
	}
	catch(Exception e){}
}

public boolean leaveRoom(String roomname)
{
	try
	{
		out.writeUTF("l/" + roomname + "/" + getName());
		return true;
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
		e.printStackTrace();
		System.err.println("Cannot leave room: " + roomname);
	}
	return false;
}

public boolean invite(String roomname, String un)
{
	try
	{
		out.writeUTF("j/" + roomname + "//");
		return true;
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
		e.printStackTrace();
		System.err.println("Cannot invite " + un + " to " + roomname);
	}
	return false;
}

public boolean kickUser(String roomname, String un)
{
	try
	{
		out.writeUTF("k/" + roomname + "/" + un);
		return true;
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
		e.printStackTrace();
		System.err.println("Cannot kick " + un + " from " + roomname);
	}
	return false;
}

public void kicked(String rn, String mng)
{
	try
	{
		mainGui.removeRoomTab(rn);
		mainGui.showMessageDialog("Kicked from " + rn + " by " + mng);
	}
	catch(Exception e){}
}

public void sendFileInfo(String rn, String fn)
{
	try
	{
		out.writeUTF("f/" + rn + "/" + fn);
	}
	catch(Exception e){}
}

public void setFileInfo(String rn, String un, String fn)
{
	try
	{
		mainGui.getRoomPage(rn).setFileInfo(un, fn);
	}
	catch(Exception e){}
}

public boolean createStream(String roomname)
{
	try
	{
		out.writeUTF("v/" + roomname + "/" + getName());
		return true;
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
		e.printStackTrace();
		System.err.println("Cannot send stream msg to " + roomname);
	}
	return false;
}
}

