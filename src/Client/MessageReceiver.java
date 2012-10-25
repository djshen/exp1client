package Client;

import Gui.*;
import java.io.*;
import javafx.application.*;

public class MessageReceiver implements Runnable
{
private DataInputStream in;
private Client client;

public MessageReceiver(DataInputStream i, Client c)
{
	in = i;
	client = c;
}

private void parseMsg(String msg)
{
	System.err.println("msg: " + msg);
	String[] msgs;
	try
	{
		msgs = msg.split("/", 3);
		char header = msgs[0].charAt(0);
		final String msg1 = msgs[1];
		final String msg2 = msgs[2];
		switch(header)
		{
			case 'r'://send to room
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.getRoomPage(msg1).displayMsg(msg2);
					}
				});
				break;
			}
			case 'm'://send to main room
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.getMainPage().displayMsg(msg2);
					}
				});
				break;
			}
			case 'x'://add room
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.getMainPage().addRoom(msg1);
					}
				});
				break;
			}
			case 'z'://room created
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.roomCreated(msg1, msg2);
					}
				});
				break;
			}
			case 'a'://get reply for join
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.getJoinReply(msg1, msg2);
					}
				});
				break;
			}
			case 'y'://yes join
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							String[] userList = msg2.split("/");
							client.joinRoom(msg1, userList);
						}
				catch(Exception e){}
					}
				});
				break;
			}
			case 'n'://no join
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.showMessageDialog("Cannot join " + msg1);
					}
				});
				break;
			}
			case 'u'://user join
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.addUser(msg1, msg2);
					}
				});
				break;
			}
			case 's'://secret chat
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.getMainPage().displayMsg(msg2);
					}
				});
				break;
			}
			/*case 'j'://join room
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.getJoinReply("Cannot join " + msg1);
					}
				});
				break;
			}*/
			case 'c'://ack create room
			{
				if(msgs[2].equals("ack"))
				{
					Platform.runLater(new Runnable()
					{
						@Override
						public void run()
						{
							//client.addRoomTab(msg1);
							String[] s = { client.getName() };
							client.joinRoom(msg1, s);
						}
					});
				}
				else
				{
					Platform.runLater(new Runnable()
					{
						@Override
						public void run()
						{
							client.showMessageDialog("Cannot create room: " + msg1);
						}
					});
				}
				break;
			}
			case 'l'://leave
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.removeUser(msg1, msg2);
					}
				});
				break;
			}
			case 'k'://kick
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.kicked(msg1, msg2);
					}
				});
				break;
			}
			case 'w'://remove user from room
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						client.removeUser(msg1, msg2);
					}
				});
				break;
			}
			case 'f'://file info
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							String[] msg2s = msg2.split("/", 2);
							client.setFileInfo(msg1, msg2s[0], msg2s[1]);
						}
				catch(Exception e){}
					}
				});
				break;
			}
			case 'e'://error occur
			{
				//parseErr(msgs[1]);
				break;
			}
			default:
			{
				System.err.println("undefined msg: " + msg);
			}
		}
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
		e.printStackTrace();
	}
}

@Override
public void run()
{
	//System.out.println("run");
	while(true)
	{
		try
		{
			synchronized(in)
			{
				String msg = in.readUTF();
				//System.out.println("msg:" + msg);
				parseMsg(msg);
			}
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
		}
	}
}
}
