package Client;
import java.io.*;
import java.util.*;

public class MessageSender implements Runnable
{
private DataOutputStream out;

public MessageSender(DataOutputStream o)
{
	out = o;
}

@Override
public void run()
{
	while(true)
	{
		Scanner scanner = new Scanner(System.in);
		String msg = scanner.nextLine();
		try
		{
			out.writeUTF(msg);
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
		}
	}

}
}
