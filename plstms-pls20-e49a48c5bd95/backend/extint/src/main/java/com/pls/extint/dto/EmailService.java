package com.pls.extint.dto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;

/**
 * Utility methods to read and manipulate email.
 * 
 * @author PAVANI CHALLA
 * 
 */
public class EmailService {

    /**
     * Connects to the Inbox folder of the email.
     * 
     * @param host
     *            email Host Name.
     * @param userName
     *            email user name.
     * @param password
     *            email password.
     * @param protocol
     *            protocol (pop3, imap etc).
     * @return Folder
     * @throws MessagingException
     *             MessagingException
     */
    public Folder connectFolder(String host, String userName, String password, String protocol) throws MessagingException {

        Properties properties = System.getProperties();
        Session session = Session.getDefaultInstance(properties, null);
        Store store = session.getStore(protocol);
        store.connect(host, userName, password);
        Folder folder = store.getFolder("inbox");
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    /**
     * Get attachments from messages.
     * 
     * @param inbox
     *            {@link Folder}.
     * @param location
     *            {@link String}.
     * @throws MessagingException
     *             MessagingException
     * @throws IOException
     *             IOException
     */
    public void getAttachmentOfMessages(Folder inbox, String location) throws MessagingException, IOException {

        Message[] message = inbox.getMessages();
        Message mesg = null;
        for (int i = 0; i < message.length; i++) {

            // if any criteria to pull email.
            if (message[i].getSubject().equalsIgnoreCase("load")) {
                mesg = message[i];
                readAttachment(mesg, location);
            }
        }
    }

    /**
     * Method to read attachments and write to the file server.
     * 
     * @param mesg
     *            {@link Message}.
     * @throws IOException
     *             IOException
     * @throws MessagingException
     *             MessagingException
     */
    private void readAttachment(Message mesg, String location) throws IOException, MessagingException {
        Multipart mesgPart = (Multipart) mesg.getContent();
        InputStream is = null;
        String fileName = null;
        for (int j = 0, n = mesgPart.getCount(); j < n; j++) {
            Part part = mesgPart.getBodyPart(j);
            BodyPart bodyPart = mesgPart.getBodyPart(j);
            String disposition = part.getDisposition();
            System.out.println(disposition);
            if (disposition != null && disposition.equals(Part.ATTACHMENT)) {
                System.out.println(bodyPart.getFileName());
                is = bodyPart.getInputStream();
                fileName = bodyPart.getFileName();
            }
        }
        // TODO: location to be defined.
        // location //srv-filesrv1/shared/IT/App_Dev/testing/PLSNextGenDocMgmtTesting/
        File file = new File(location + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buf = new byte[4096];
        int bytesRead = is.read(buf);
        while (bytesRead != -1) {
            fos.write(buf, 0, bytesRead);
            bytesRead = is.read(buf);
        }
        fos.close();
    }

    /**
     * Read messages for a particular date range.
     * 
     * @param inbox
     *            {@link Folder}.
     * @param location
     *            {@link String}.
     * @throws MessagingException
     *             MessagingException
     * @throws IOException
     *             IOException
     * @throws ParseException
     *             ParseException
     */
    public void getMessageBasedOnDate(Folder inbox, String location) throws MessagingException, IOException, ParseException {
        Date todayDate = new Date();
        SearchTerm searchCondition = new AndTerm(new SentDateTerm(ComparisonTerm.LE, todayDate), new SentDateTerm(ComparisonTerm.GT, testDate()));
        Message[] message = inbox.search(searchCondition);

        System.out.println(message.length);
        Message mesg = null;
        for (int i = 0; i < message.length; i++) {

            // if any criteria to pull email.
            if (message[i].getSubject().equalsIgnoreCase("load")) {
                mesg = message[i];
                readAttachment(mesg, location);
            }
        }
    }

    // TODO: to pull from the DB. This date to be the last scheduler run date.
    /**
     * Test date.
     * 
     * @return {@link Date}.
     * @throws ParseException
     *             ParseException
     */
    public Date testDate() throws ParseException {
        SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yy", Locale.US);
        String dt = "08/14/13";
        return df1.parse(dt);
    }

}
