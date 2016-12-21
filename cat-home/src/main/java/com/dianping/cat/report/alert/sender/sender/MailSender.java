package com.dianping.cat.report.alert.sender.sender;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.dianping.cat.Cat;
import com.dianping.cat.report.alert.sender.AlertChannel;
import com.dianping.cat.report.alert.sender.AlertMessageEntity;
import org.apache.commons.mail.HtmlEmail;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailSender extends AbstractSender implements Initializable {

    public static final String ID = AlertChannel.MAIL.getName();

    private static final String PROPERTIES_ALERT = "/data/appdatas/cat/cat.properties";

    private boolean initialized = false;

    private final Properties catProps = new Properties();

    Authenticator authenticator = null;

    @Override
    public String getId() {
        return ID;
    }

    /*@Override
    public boolean send(AlertMessageEntity message) {
        com.dianping.cat.home.sender.entity.Sender sender = querySender();
        boolean batchSend = sender.isBatchSend();
        boolean result = false;

        if (batchSend) {
            String emails = message.getReceiverString();

            result = sendEmail(message, emails, sender);
        } else {
            List<String> emails = message.getReceivers();

            for (String email : emails) {
                boolean success = sendEmail(message, email, sender);
                result = result || success;
            }
        }
        return result;
    }*/

    @Override
    public boolean send(AlertMessageEntity message) {
        if (!initialized) return false;
        m_logger.info(String.format("start to send email alert message, title:%s, receivers:%s", message.getTitle(), message.getReceiverString()));
        com.dianping.cat.home.sender.entity.Sender sender = querySender();
        if (sender == null) {
            return false;
        }
        boolean batchSend = sender.isBatchSend();
        boolean result = false;
        if (batchSend) {
            String emails = message.getReceiverString();
            result = sendEmail(message, emails);
        } else {
            List<String> emails = message.getReceivers();
            for (String email : emails) {
                boolean success = sendEmail(message, email);
                result = result || success;
            }
        }
        return result;
    }

	/*private boolean sendEmail(AlertMessageEntity message, String receiver,
          com.dianping.cat.home.sender.entity.Sender sender) {
		String title = message.getTitle().replaceAll(",", " ");
		String content = message.getContent().replaceAll(",", " ");
		String urlPrefix = sender.getUrl();
		String urlPars = m_senderConfigManager.queryParString(sender);
		String time = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());

		try {
			urlPars = urlPars.replace("${receiver}", receiver).replace("${title}", URLEncoder.encode(title, "utf-8"))
			      .replace("${content}", URLEncoder.encode(content, "utf-8"))
			      .replace("${time}", URLEncoder.encode(time, "utf-8"));

		} catch (Exception e) {
			Cat.logError(e);
		}

		return httpSend(sender.getSuccessCode(), sender.getType(), urlPrefix, urlPars);
	}*/

    private boolean sendEmail(AlertMessageEntity alertEntity, String receiver) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(catProps.getProperty("mail.smtp.host"));
            email.setAuthentication(catProps.getProperty("mail.user"), catProps.getProperty("mail.password"));
            email.setCharset("UTF-8");
            email.setFrom(catProps.getProperty("mail.user"));
            String[] tos = receiver.split(",");
            for (String to : tos) {
                email.addTo(to);
            }
            email.setSubject(alertEntity.getTitle());
            email.setHtmlMsg(alertEntity.getContent());
            email.send();
            return true;
        } catch (Exception e) {
            Cat.logError(e);
            return false;
        }
    }

    @Override
    public void initialize() throws InitializationException {
        InputStream in = null;
        InputStreamReader reader = null;
        try {
            in = new FileInputStream(new File(PROPERTIES_ALERT));
            reader = new InputStreamReader(in, "UTF-8");
            catProps.load(reader);
            m_logger.info(String.format("initialing cat properties: %s", catProps));
            authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    String userName = catProps.getProperty("mail.user");
                    String password = catProps.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            initialized = true;
        } catch (IOException e) {
            Cat.logError(e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    Cat.logError(e);
                }
            }
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Cat.logError(e);
                }
            }
        }
    }
}
