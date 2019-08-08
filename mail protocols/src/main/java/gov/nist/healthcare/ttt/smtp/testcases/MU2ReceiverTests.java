package gov.nist.healthcare.ttt.smtp.testcases;

import gov.nist.healthcare.ttt.smtp.TestInput;
import gov.nist.healthcare.ttt.smtp.TestResult;
import gov.nist.healthcare.ttt.smtp.TestResult.CriteriaStatus;
import gov.nist.healthcare.ttt.smtp.util.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.mail.dsn.DispositionNotification;


public class MU2ReceiverTests {
	public static Logger log = Logger.getLogger(MU2ReceiverTests.class.getName());

	public TestResult fetchMail(TestInput ti) throws IOException {
		System.setProperty("java.net.preferIPv4Stack", "true");
		TestResult tr = new TestResult();
		HashMap<String, String> result = tr.getTestRequestResponses();
		HashMap<String, String> bodyparts = tr.getAttachments();
		LinkedHashMap<String, String> buffer = new LinkedHashMap<String, String>();
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> list1 = new ArrayList<String>();
		int dsnFlag = 0;
		int headerFlag = 0;
		int dispatchedFlag = 0;
		int xHeaderFlag = 0;
		int failureFlag = 0;
		Store store;
		Properties props = new Properties();

		TestResult t = ti.tr;

		String id = t.getMessageId();
		String fetch = t.getFetchType();
		String type = t.getSearchType();
		String startTime = t.getStartTime();
		Duration duration = null;
		Duration timeout = null;
		final long  timeoutConstant = 70; // 1 hour 10 mins (or) 70 minutes to get back the failure MDN


		try {


			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();

			Session session = Session.getDefaultInstance(props, null);

			store = session.getStore("imap");

			if (fetch.equals("smtp")){
				store.connect(ti.tttSmtpAddress,143,prop.getProperty("ett.smtpmu2.address"),prop.getProperty("ett.password"));
			}
			else if (fetch.equals("imap")) {
				store.connect(ti.sutSmtpAddress,143,ti.sutUserName,ti.sutPassword);
			}

			else if (fetch.equals("imap1")) {
				store.connect(prop.getProperty("dir.hostname"),143,prop.getProperty("dir.username"), prop.getProperty("dir.password"));
			}

			else {
				store = session.getStore("pop3");
				store.connect(ti.sutSmtpAddress,110,ti.sutUserName,ti.sutPassword);
			}
			Folder inbox = store.getFolder("Inbox");
			inbox.open(Folder.READ_WRITE);


			/*Flags seen = new Flags(Flags.Flag.SEEN);
			FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
			Message messages[] = inbox.search(unseenFlagTerm);*/


			int mcount = inbox.getMessageCount();
			Message messages[] = inbox.getMessages(1, mcount);

			if (mcount > 50){
				messages = inbox.getMessages(mcount-49, mcount); // Search for last 50 emails.
			}




			if(type.equals("fail")){
				System.out.println("Search X-Original-Message-ID or Failure MDN");
				for (Message message : messages){
					if(message.getContent() instanceof Multipart){
						MimeMessage mime = (MimeMessage) message;
						Message message1 = new MimeMessage(mime);
						Multipart multipart1 = (Multipart) message1.getContent();
						for (int i = 0; i < multipart1.getCount(); i++) {
							BodyPart bodyPart = multipart1.getBodyPart(i);
							InputStream stream = bodyPart.getInputStream();
							byte[] targetArray = IOUtils.toByteArray(stream);
							System.out.println(new String(targetArray));

							String searchString = new String(targetArray);

							if (searchString.contains(id) && searchString.contains("X-Original-Message-ID")){
								dsnFlag = 1;
								result.put("\nNotification Type", "DSN"+"\n"+searchString);
								System.out.println("\nX-Original-Message-ID Found\n");
								log.info("Failure DSN with X-header found with ID " + id);

								ZonedDateTime endTime = ZonedDateTime.now();
								duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
								result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
								//	timeout = Duration.between(ZonedDateTime.parse(startTime),endTime);
							}
						}




						if (dsnFlag == 0){
							//		MimeMessage mime = (MimeMessage) message;
							//		Message message1 = new MimeMessage(mime);
							if(!(message1.getContentType().contains("delivery-status"))){
								Object m =  message.getContent();
								if (message.getContent() instanceof Multipart){
									Multipart multipart = (Multipart) message.getContent();
									for (int i = 0; i < ((Multipart) m).getCount(); i++){
										BodyPart bodyPart = multipart.getBodyPart(i);
										if (!(bodyPart.isMimeType("text/*"))){
											Object d =   bodyPart.getContent();
											//d.getNotifications();
											if (d instanceof DispositionNotification){
												Enumeration headers2 = ((DispositionNotification) d).getNotifications().getAllHeaders();
												while (headers2.hasMoreElements()) {
													Header h1 = (Header) headers2.nextElement();
													buffer.put("\n"+h1.getName(), h1.getValue()+"\n");
												}
												System.out.println(buffer);
												if(buffer.containsValue(id+"\n")
														&& (buffer.containsValue("automatic-action/MDN-sent-automatically;failed"+"\n") || 
																buffer.containsValue("automatic-action/MDN-sent-automatically; failed"+"\n") ||
																buffer.containsValue("automatic-action/MDN-sent-automatically;failure"+"\n") ||
																buffer.containsValue("automatic-action/MDN-sent-automatically; failure"+"\n"))){
													//	buffer.get("\n"+"Disposition").toLowerCase().contains("fail");
													ZonedDateTime endTime = ZonedDateTime.now();
													result.putAll(buffer);
													duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
													result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
													//	timeout = Duration.between(ZonedDateTime.parse(startTime),endTime);
													log.info("Failure MDN found with ID " + id);
												}


											}

										}

									}

								}
							}
						}
					}

				}
			}


			else if (type.equals("timeout")){
				System.out.println("Search X-Original-Message-ID or Failure MDN on timeout");
				for (Message message : messages){
					if(message.getContent() instanceof Multipart){
						MimeMessage mime = (MimeMessage) message;
						Message message1 = new MimeMessage(mime);
						Multipart multipart1 = (Multipart) message1.getContent();
						for (int i = 0; i < multipart1.getCount(); i++) {
							BodyPart bodyPart = multipart1.getBodyPart(i);
							InputStream stream = bodyPart.getInputStream();
							byte[] targetArray = IOUtils.toByteArray(stream);
							//	System.out.println(new String(targetArray));

							String searchString = new String(targetArray);

							if (searchString.contains(id) && searchString.contains("X-Original-Message-ID")){
								dsnFlag = 1;
								result.put("\nNotification Type", "DSN"+"\n"+searchString);
								System.out.println("\nX-Original-Message-ID Found\n");
								log.info("Failure DSN with X-header found with ID " + id);

								ZonedDateTime endTime = ZonedDateTime.now();
								duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
								result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
								timeout = Duration.between(ZonedDateTime.parse(startTime),endTime);
							}
						}




						if (dsnFlag == 0){
							//		MimeMessage mime = (MimeMessage) message;
							//		Message message1 = new MimeMessage(mime);
							if(!(message1.getContentType().contains("delivery-status"))){
								Object m =  message.getContent();
								if (message.getContent() instanceof Multipart){
									Multipart multipart = (Multipart) message.getContent();
									for (int i = 0; i < ((Multipart) m).getCount(); i++){
										BodyPart bodyPart = multipart.getBodyPart(i);
										if (!(bodyPart.isMimeType("text/*"))){
											Object d =   bodyPart.getContent();
											//d.getNotifications();
											if (d instanceof DispositionNotification){
												Enumeration headers2 = ((DispositionNotification) d).getNotifications().getAllHeaders();
												while (headers2.hasMoreElements()) {
													Header h1 = (Header) headers2.nextElement();
													buffer.put("\n"+h1.getName(), h1.getValue()+"\n");
												}
												//	System.out.println(buffer);
												if(buffer.containsValue(id+"\n")
														&& (buffer.containsValue("automatic-action/MDN-sent-automatically;failed"+"\n") || 
																buffer.containsValue("automatic-action/MDN-sent-automatically; failed"+"\n") ||
																buffer.containsValue("automatic-action/MDN-sent-automatically;failure"+"\n") ||
																buffer.containsValue("automatic-action/MDN-sent-automatically; failure"+"\n"))){
													//	buffer.get("\n"+"Disposition").toLowerCase().contains("fail");
													ZonedDateTime endTime = ZonedDateTime.now();
													result.putAll(buffer);
													duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
													result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
													timeout = Duration.between(ZonedDateTime.parse(startTime),endTime);
													log.info("Failure MDN found with ID " + id);
												}


											}

										}

									}

								}
							}
						}
					}

				}

			}
			else if (type.equals("timeout28")){
				System.out.println("Search X-Original-Message-ID or Failure MDN on timeout");
				for (Message message : messages){
					if(message.getContent() instanceof Multipart){
						MimeMessage mime = (MimeMessage) message;
						Message message1 = new MimeMessage(mime);
						Multipart multipart1 = (Multipart) message1.getContent();
						for (int i = 0; i < multipart1.getCount(); i++) {
							BodyPart bodyPart = multipart1.getBodyPart(i);
							InputStream stream = bodyPart.getInputStream();
							byte[] targetArray = IOUtils.toByteArray(stream);
							//	System.out.println(new String(targetArray));

							String searchString = new String(targetArray);

							if (searchString.contains(id) && searchString.contains("X-Original-Message-ID")){
								dsnFlag = 1;
								result.put("\nNotification Type", "DSN"+"\n"+searchString);
								System.out.println("\nX-Original-Message-ID Found\n");
								log.info("Failure DSN with X-header found with ID " + id);

								ZonedDateTime endTime = ZonedDateTime.now();
								duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
								result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
								timeout = Duration.between(ZonedDateTime.parse(startTime),endTime);
								failureFlag = 1;
							}
						}




						if (dsnFlag == 0){
							//		MimeMessage mime = (MimeMessage) message;
							//		Message message1 = new MimeMessage(mime);
							if(!(message1.getContentType().contains("delivery-status"))){
								Object m =  message.getContent();
								if (message.getContent() instanceof Multipart){
									Multipart multipart = (Multipart) message.getContent();
									for (int i = 0; i < ((Multipart) m).getCount(); i++){
										BodyPart bodyPart = multipart.getBodyPart(i);
										if (!(bodyPart.isMimeType("text/*"))){
											Object d =   bodyPart.getContent();
											//d.getNotifications();
											if (d instanceof DispositionNotification){
												Enumeration headers2 = ((DispositionNotification) d).getNotifications().getAllHeaders();
												while (headers2.hasMoreElements()) {
													Header h1 = (Header) headers2.nextElement();
													buffer.put("\n"+h1.getName(), h1.getValue()+"\n");
												}
												//	System.out.println(buffer);
												if(buffer.containsValue(id+"\n")
														&& (buffer.containsValue("automatic-action/MDN-sent-automatically;failed"+"\n") || 
																buffer.containsValue("automatic-action/MDN-sent-automatically; failed"+"\n") ||
																buffer.containsValue("automatic-action/MDN-sent-automatically;failure"+"\n") ||
																buffer.containsValue("automatic-action/MDN-sent-automatically; failure"+"\n"))){
													//	buffer.get("\n"+"Disposition").toLowerCase().contains("fail");
													ZonedDateTime endTime = ZonedDateTime.now();
													result.putAll(buffer);
													duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
													result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
													timeout = Duration.between(ZonedDateTime.parse(startTime),endTime);
													log.info("Failure MDN found with ID " + id);
													failureFlag = 1;
												}
												if(buffer.containsValue(id+"\n")
														&& (buffer.containsValue("automatic-action/MDN-sent-automatically;dispatched"+"\n"))){
													//	buffer.get("\n"+"Disposition").toLowerCase().contains("fail");
													ZonedDateTime endTime = ZonedDateTime.now();
													result.putAll(buffer);
													duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
													result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
													timeout = Duration.between(ZonedDateTime.parse(startTime),endTime);
													log.info("Dispatched MDN found with ID. Failing case" + id);
													dispatchedFlag = 1;
												}

											}

										}

									}

								}
							}
						}
					}

				}

			}
			else if (type.equals("28")){
				System.out.println("Search X-Original-Message-ID or Failure MDN");
				for (Message message : messages){
					Enumeration headers = message.getAllHeaders();
					while(headers.hasMoreElements()) {
						Header h = (Header) headers.nextElement();
						String x = h.getValue();
						if (id.equals(x)){
							dsnFlag = 1;
							Enumeration headers1 = message.getAllHeaders();
							while (headers1.hasMoreElements()) {
								Header h1 = (Header) headers1.nextElement();
								//	result.put(h.getName() + " " +  "[" + j +"]", h.getValue());
								buffer.put("\n"+h1.getName(), h1.getValue()+"\n");


							}
						}

						if (dsnFlag == 0){
							MimeMessage mime = (MimeMessage) message;
							Message message1 = new MimeMessage(mime);
							if(!(message1.getContentType().contains("delivery-status"))){
								Object m =  message.getContent();
								if (message.getContent() instanceof Multipart){
									Multipart multipart = (Multipart) message.getContent();
									for (int i = 0; i < ((Multipart) m).getCount(); i++){
										BodyPart bodyPart = multipart.getBodyPart(i);
										if (!(bodyPart.isMimeType("text/*"))){
											Object d =   bodyPart.getContent();
											//d.getNotifications();
											if (d instanceof DispositionNotification){
												Enumeration headers2 = ((DispositionNotification) d).getNotifications().getAllHeaders();
												while (headers2.hasMoreElements()) {
													Header h1 = (Header) headers2.nextElement();
													list.add(h1.getName());
													list.add(h1.getValue());


												}
												System.out.println(buffer);
												if(list.contains(id) && Utils.isDispatchedMDN(list)){
													dispatchedFlag = 1;
												}

												else{
													ZonedDateTime endTime = ZonedDateTime.now();
													duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
													result.putAll(buffer);
													result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
												}


											}

										}

									}

								}
							}
						}
					}
				}
			}

			else if (type.equals("failure/dispatched")) { 
				System.out.println("Search Original-Message-Id for Processed/Dispatched in DN with no X-Header");
				int j = 1;
				for (Message message : messages){
					MimeMessage mime = (MimeMessage) message;
					Message message1 = new MimeMessage(mime);
					if(!(message1.getContentType().contains("delivery-status"))){
						Object m =  message.getContent();
						if (message.getContent() instanceof Multipart){
							Multipart multipart = (Multipart) message.getContent();
							for (int i = 0; i < ((Multipart) m).getCount(); i++){
								BodyPart bodyPart = multipart.getBodyPart(i);
								if (!(bodyPart.isMimeType("text/*"))){
									Object d =   bodyPart.getContent();
									//d.getNotifications();
									if (d instanceof DispositionNotification){
										Enumeration headers2 = ((DispositionNotification) d).getNotifications().getAllHeaders();
										while (headers2.hasMoreElements()) {
											Header h1 = (Header) headers2.nextElement();
											if (id.equals(h1.getValue())){
												Enumeration headers3 = ((DispositionNotification) d).getNotifications().getAllHeaders();
												while (headers3.hasMoreElements()) {
													Header h2 = (Header) headers3.nextElement();
													buffer.put("\n"+h2.getName()+" "+j, h2.getValue()+"\n");
													list.add(h2.getValue());
													list1.add(h2.getName());
													j++;
												}
											}

											/*else{
											message.setFlag(Flags.Flag.SEEN, false);
										}*/
										}

										System.out.println(list);

										if(list1.contains("X-DIRECT-FINAL-DESTINATION-DELIVERY")){
											headerFlag = 1;
											ZonedDateTime endTime = ZonedDateTime.now();
											result.putAll(buffer);
											duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
											result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
										}

										else if (Utils.isProcessedMDN(list)){
											ZonedDateTime endTime = ZonedDateTime.now();
											result.putAll(buffer);
											duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
											result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
										}


									}

								}

							}

						}
					}
				}

			}

			else if (type.equals("either")) { 
				System.out.println("Search Original-Message-Id for Processed/Dispatched in DN with no X-Header");
				int j = 1;
				for (Message message : messages){
					MimeMessage mime = (MimeMessage) message;
					Message message1 = new MimeMessage(mime);
					if(!(message1.getContentType().contains("delivery-status"))){
						Object m =  message.getContent();
						if (message.getContent() instanceof Multipart){
							Multipart multipart = (Multipart) message.getContent();
							for (int i = 0; i < ((Multipart) m).getCount(); i++){
								BodyPart bodyPart = multipart.getBodyPart(i);
								if (!(bodyPart.isMimeType("text/*"))){
									Object d =   bodyPart.getContent();
									//d.getNotifications();
									if (d instanceof DispositionNotification){
										Enumeration headers2 = ((DispositionNotification) d).getNotifications().getAllHeaders();
										while (headers2.hasMoreElements()) {
											Header h1 = (Header) headers2.nextElement();
											if (id.equals(h1.getValue())){
												Enumeration headers3 = ((DispositionNotification) d).getNotifications().getAllHeaders();
												while (headers3.hasMoreElements()) {
													Header h2 = (Header) headers3.nextElement();
													buffer.put("\n"+h2.getName()+" "+j, h2.getValue()+"\n");
													list.add(h2.getValue());
													list1.add(h2.getName());
													j++;
												}
											}

											/*else{
											message.setFlag(Flags.Flag.SEEN, false);
										}*/
										}

										System.out.println(list);

										if(list1.contains("X-DIRECT-FINAL-DESTINATION-DELIVERY")){
											headerFlag = 1;
											ZonedDateTime endTime = ZonedDateTime.now();
											result.putAll(buffer);
											duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
											result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
										}

										else if (Utils.isProcessedMDN(list)){
											ZonedDateTime endTime = ZonedDateTime.now();
											result.putAll(buffer);
											duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
											result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
											log.info("Processed MDN found with ID " + id);
										}


									}

								}

							}

						}
					}
				}

			}

			else if (type.equals("dispatched")) {
				System.out.println("Search Original-Message-Id for Dispatched in DN");
				String s = "";
				for (Message message : messages){
					MimeMessage mime = (MimeMessage) message;
					Message message1 = new MimeMessage(mime);
					if(!(message1.getContentType().contains("delivery-status"))){
						Object m =  message.getContent();
						if (message.getContent() instanceof Multipart){
							Multipart multipart = (Multipart) message.getContent();
							for (int i = 0; i < ((Multipart) m).getCount(); i++){
								BodyPart bodyPart = multipart.getBodyPart(i);
								if (!(bodyPart.isMimeType("text/*"))){
									Object d =   bodyPart.getContent();
									//d.getNotifications();
									if (d instanceof DispositionNotification){
										Enumeration headers2 = ((DispositionNotification) d).getNotifications().getAllHeaders();
										while (headers2.hasMoreElements()) {
											Header h1 = (Header) headers2.nextElement();
											buffer.put("\n"+h1.getName(), h1.getValue()+"\n");
										}
										System.out.println(buffer);
										if(buffer.containsValue(id+"\n") && (buffer.containsValue("automatic-action/MDN-sent-automatically;dispatched"+"\n") || buffer.containsValue("automatic-action/MDN-sent-automatically; dispatched"+"\n"))){
											ZonedDateTime endTime = ZonedDateTime.now();
											result.putAll(buffer);
											duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
											result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
											log.info("Dispatched MDN found with ID " + id);
										}


									}

								}

							}

						}
					}
				}

			}

			else if (type.equals("both")) {
				System.out.println("Search Original-Message-Id for Processed and Dispatched in DN");
				int j = 1;
				for (Message message : messages){
					MimeMessage mime = (MimeMessage) message;
					Message message1 = new MimeMessage(mime);
					if(!(message1.getContentType().contains("delivery-status"))){
						Object m =  message.getContent();
						if (message.getContent() instanceof Multipart){
							Multipart multipart = (Multipart) message.getContent();
							for (int i = 0; i < ((Multipart) m).getCount(); i++){
								BodyPart bodyPart = multipart.getBodyPart(i);
								if (!(bodyPart.isMimeType("text/*"))){
									Object d =   bodyPart.getContent();
									//d.getNotifications();
									if (d instanceof DispositionNotification){
										Enumeration headers2 = ((DispositionNotification) d).getNotifications().getAllHeaders();
										while (headers2.hasMoreElements()) {
											Header h1 = (Header) headers2.nextElement();
											if (id.equals(h1.getValue())){
												Enumeration headers3 = ((DispositionNotification) d).getNotifications().getAllHeaders();
												while (headers3.hasMoreElements()) {
													Header h2 = (Header) headers3.nextElement();
													buffer.put("\n"+h2.getName()+" "+j, h2.getValue()+"\n");
													list.add(h2.getValue());
													j++;
												}
											}
										}

										System.out.println(list);

										if(Utils.isProcessedMDN(list) && Utils.isDispatchedMDN(list)){

											ZonedDateTime endTime = ZonedDateTime.now();
											result.putAll(buffer);
											duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
											result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
											log.info("Processed & Dispatched MDNs found with ID " + id);
										}

									}

								}

							}

						}
					}
				}

			}

			else if (type.equals("processedandfailure")) {
				String searchString = "";
				System.out.println("Search X-Original-Message-ID or Failure MDN and Processed MDN");
				int j = 1;
				for (Message message : messages){
					MimeMessage mime = (MimeMessage) message;
					Message message1 = new MimeMessage(mime);
					if(!(message1.getSubject()!=null && message1.getSubject().equals("Re:"))){
						if(message1.getContent() instanceof Multipart){
							Multipart multipart1 = (Multipart) message1.getContent();
							for (int i = 0; i < multipart1.getCount(); i++) {
								BodyPart bodyPart = multipart1.getBodyPart(i);
								InputStream stream = bodyPart.getInputStream();
								byte[] targetArray = IOUtils.toByteArray(stream);
								System.out.println(new String(targetArray));

								searchString = new String(targetArray);

								if (searchString.contains(id) && searchString.contains("X-Original-Message-ID")){
									dsnFlag = 1;
									list.add(searchString);
									list.add("X-Original-Message-ID Found");
									System.out.println("\nX-Original-Message-ID Found\n");
									log.info("Failure DSN with X-header found with ID " + id);
								}
							}




							if (dsnFlag == 0){
								//	MimeMessage mime = (MimeMessage) message;
								//	Message message1 = new MimeMessage(mime);
								if(!(message1.getContentType().contains("delivery-status"))){
									Object m =  message.getContent();
									if (message.getContent() instanceof Multipart){
										Multipart multipart = (Multipart) message.getContent();
										for (int i = 0; i < ((Multipart) m).getCount(); i++){
											BodyPart bodyPart = multipart.getBodyPart(i);
											if (!(bodyPart.isMimeType("text/*"))){
												Object d =   bodyPart.getContent();
												//d.getNotifications();
												if (d instanceof DispositionNotification){
													Enumeration headers2 = ((DispositionNotification) d).getNotifications().getAllHeaders();
													while (headers2.hasMoreElements()) {
														Header h1 = (Header) headers2.nextElement();
														if (id.equals(h1.getValue())){
															Enumeration headers3 = ((DispositionNotification) d).getNotifications().getAllHeaders();
															while (headers3.hasMoreElements()) {
																Header h2 = (Header) headers3.nextElement();
																buffer.put("\n"+h2.getName(), h2.getValue()+"\n");
																list1.add(h2.getValue());
															}
														}
													}

												}

											}

										}

									}
								}
							}
						}
					}
				}

				System.out.println(list1);
				System.out.println(list);
				if(Utils.isProcessedMDN(list1)){
					if(list.contains("X-Original-Message-ID Found") || Utils.isFailureMDN(list1) || Utils.isFailedMDN(list1)){
						ZonedDateTime endTime = ZonedDateTime.now();
						result.put("\n","\n"+searchString);
						result.putAll(buffer);
						duration = Duration.between(ZonedDateTime.parse(startTime),endTime);
						result.put("\nElapsed Time", duration.toString().replace("PT", "")+"\n");
						log.info("Processed & Failure Notifications found with ID " + id);
					}
				}

			}


			store.close();

			if (result.size() == 0) {
				tr.setCriteriamet(CriteriaStatus.STEP2);
				tr.getTestRequestResponses().put("ERROR","No messages found with Original Message ID: " + id);
				log.error("No messages found with Original Message ID: " + id);
			}

			else if(timeout!=null && (timeout.toMinutes() > timeoutConstant)){

				tr.setCriteriamet(CriteriaStatus.FALSE);
				tr.getTestRequestResponses().put("ERROR","MDN received after timeout");
				log.error("MDN received after timeout");

			}
			
			else if(timeout!=null && failureFlag ==1 && (timeout.toMinutes() < ti.sutCommandTimeoutInSeconds)){
                System.out.println(timeout.toMinutes());
				tr.setCriteriamet(CriteriaStatus.FALSE);
				tr.getTestRequestResponses().put("ERROR","Failure Notification received before the entered timeout of "+ ti.sutCommandTimeoutInSeconds+ " minutes");
				log.error("MDN received before timeout");

			}
			
			else if(timeout!=null && dispatchedFlag == 0 && failureFlag ==1 && timeout.toMinutes()+1 > ti.sutCommandTimeoutInSeconds && timeout.toMinutes() < ti.sutCommandTimeoutInSeconds+5){

				tr.setCriteriamet(CriteriaStatus.STEP2);
				tr.getTestRequestResponses().put("INFO","Failure Notification received");
				System.out.println(timeout.toMinutes());
				log.info("Failure Notification received");

			}
			
			else if(timeout!=null && dispatchedFlag == 0 && failureFlag ==1 && (timeout.toMinutes() > ti.sutCommandTimeoutInSeconds+5)){

				tr.setCriteriamet(CriteriaStatus.TRUE);
				tr.getTestRequestResponses().put("SUCCESS","Failure Notification received");
				log.info("Failure Notification received");

			}

			else if (headerFlag == 1){
				tr.setCriteriamet(CriteriaStatus.FALSE);
				tr.getTestRequestResponses().put("\n"+"ERROR","Dispatched MDN contains X-DIRECT-FINAL-DESTINATION-DELIVERY header");
				log.error("Dispatched MDN contains X-DIRECT-FINAL-DESTINATION-DELIVERY header");
			}

			else if (dispatchedFlag == 1){
				tr.setCriteriamet(CriteriaStatus.FALSE);
				tr.getTestRequestResponses().put("\n"+"ERROR","Dispatched MDN is sent from SUT");
				log.error("Dispatched MDN is sent from SUT");
			}

			else {
				tr.setCriteriamet(CriteriaStatus.TRUE);
				log.info("Test Passed for ID " + id);
			}

		} catch (Exception e) {
			tr.setCriteriamet(CriteriaStatus.FALSE);
			e.printStackTrace();
			log.info("Error fetching email " + e.getLocalizedMessage());
			tr.getTestRequestResponses().put("1","Error fetching email :" + e.getLocalizedMessage());
		}

		tr.setMessageId(id);
		tr.setFetchType(fetch);
		tr.setSearchType(type);
		tr.setStartTime(startTime);
		return tr;
	}
}
