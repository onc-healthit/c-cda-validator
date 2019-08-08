package gov.nist.healthcare.ttt.smtp.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

public class DeleteInbox {

	public static void main(String args[]) 
	{
		try{
			Store store;
			Properties props = System.getProperties();
		
			String hostname = args[0];
		
			String protocol = args[1];
		
			int port = Integer.parseInt(args[2]);
		
			String username = args[3];
		
			String password = args[4];
		
			
			Session session = Session.getDefaultInstance(props, null);
			store = session.getStore(protocol);

			store.connect(hostname,port,username,password);
			Folder inbox = store.getFolder("Inbox");
			inbox.open(Folder.READ_WRITE);


			Flags seen = new Flags(Flags.Flag.SEEN);
			FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
			Message messages[] = inbox.search(unseenFlagTerm);

			FlagTerm seenFlagTerm = new FlagTerm(seen,true);
			Message messages1[] = inbox.search(seenFlagTerm);

			for (Message message : messages){

				message.setFlag(Flags.Flag.DELETED, true);
			}

			for (Message message1 : messages1){

				message1.setFlag(Flags.Flag.DELETED, true);
			}

			inbox.close(true);

			System.out.println("Delete sequence completed succesfully.");

		}catch(Exception e){

			System.out.println("Error in delete sequence!");

			System.out.println(e.getLocalizedMessage());

			e.printStackTrace();
		}

	}
}
