package com.dianping.cat.report.alert.sender.sender;

import java.util.List;

import com.dianping.cat.Cat;
import com.dianping.cat.CatProperties;
import com.dianping.cat.report.alert.sender.AlertChannel;
import com.dianping.cat.report.alert.sender.AlertMessageEntity;
import org.apache.commons.mail.HtmlEmail;

public class MailSender extends AbstractSender {

    public static final String ID = AlertChannel.MAIL.getName();

    private CatProperties catProps = CatProperties.getInstance();

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

    public boolean sendEmail(AlertMessageEntity alertEntity, String receiver) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(catProps.getProperty("mail.smtp.host"));
            email.setSmtpPort(Integer.valueOf(catProps.getProperty("mail.smtp.port")));
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

}
