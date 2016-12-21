package com.dianping.cat.report.alert.sender.decorator;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.cat.Cat;
import com.dianping.cat.report.alert.AlertType;
import com.dianping.cat.report.alert.sender.AlertEntity;
import com.dianping.cat.report.alert.summary.AlertSummaryExecutor;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ExceptionDecorator extends ProjectDecorator implements Initializable {

    @Inject
    private AlertSummaryExecutor m_executor;

    public Configuration m_configuration;

    public static final String ID = AlertType.Exception.getName();

    protected DateFormat m_linkFormat = new SimpleDateFormat("yyyyMMddHH");

    private static final String PROPERTIES_ALERT = "/data/appdatas/cat/cat.properties";
    private Properties catProps = new Properties();

    @Override
    public String generateContent(AlertEntity alert) {
        Map<Object, Object> dataMap = generateExceptionMap(alert);
        StringWriter sw = new StringWriter(5000);

        try {
            Template t = m_configuration.getTemplate("exceptionAlert.ftl");
            t.process(dataMap, sw);
        } catch (Exception e) {
            Cat.logError("build exception content error:" + alert.toString(), e);
        }

        String alertContent = sw.toString();
        String summaryContext = "";

        try {
            summaryContext = m_executor.execute(alert.getGroup(), alert.getDate());
        } catch (Exception e) {
            Cat.logError(alert.toString(), e);
        }

        if (summaryContext != null) {
            return alertContent + "<br/>" + summaryContext;
        } else {
            return alertContent;
        }
    }

    protected Map<Object, Object> generateExceptionMap(AlertEntity alert) {
        String domain = alert.getGroup();
        String contactInfo = buildContactInfo(domain);
        Map<Object, Object> map = new HashMap<Object, Object>();

        map.put("domain", domain);
        map.put("content", alert.getContent());
        map.put("date", m_format.format(alert.getDate()));
        map.put("linkDate", m_linkFormat.format(alert.getDate()));
        map.put("contactInfo", contactInfo);
        map.put("catHost", catProps.getProperty("cat.host"));
        System.out.println("######## " + map);
        return map;
    }

    @Override
    public String generateTitle(AlertEntity alert) {
        StringBuilder sb = new StringBuilder();
        sb.append("[CAT异常告警] [项目: ").append(alert.getGroup()).append("]");
        return sb.toString();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void initialize() throws InitializationException {
        m_configuration = new Configuration();
        m_configuration.setDefaultEncoding("UTF-8");
        InputStream in = null;
        InputStreamReader reader = null;
        try {
            in = new FileInputStream(new File(PROPERTIES_ALERT));
            reader = new InputStreamReader(in, "UTF-8");
            catProps.load(reader);
            m_configuration.setClassForTemplateLoading(this.getClass(), "/freemaker");
        } catch (Exception e) {
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
