package dy.arch.aef.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import dy.arch.util.Log;
import dy.arch.util.constant.HTMLConst;

/**
 * 邮件工具类 发送邮件
 * @author Administrator
 *
 */
public class SendMail
{
    /**
     * 邮件发送方法，仅需关键参数即可。
     * 需要遵守命名映射。
     * smtp_server      SMTP服务器地址；
     * smtp_port        对应端口；
     * smtp_account     账户；
     * smtp_password    密码；
     * smtp_target      收信人（可为群发，群发列表需要使用,或者;分割）
     * smtp_subject     标题
     * smtp_text        邮件正文（HTML格式）
     * @param params    关键参数集合
     * @throws MessagingException
     */
    @SuppressWarnings("static-access")
    public static void sendMessage(Map<String, String> params) throws MessagingException
    {
        // 第一步：配置javax.mail.Session对象
        Properties props = new Properties();
        props.put("mail.smtp.host", params.get("smtp_server"));
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "smtp_port");
        props.put("mail.smtp.connectiontimeout", "30000"); //
        props.put("mail.smtp.timeout", "30000"); //
        
        // 启用SSL的链接方式
        // props.put("mail.smtp.socketFactory.class",
        // "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.socketFactory.fallback", "false");
        // props.put("mail.smtp.port", "smtp_port");
        // props.put("mail.smtp.socketFactory.port", "smtp_port");
        
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.quitwait", "false");
        props.put("mail.smtp.debug", "true");
        Session mailSession = Session.getInstance(props, new MyAuthenticator(params.get("smtp_account"),
                params.get("smtp_password")));
        
        // 第二步：编写消息
        Log.notice("[" + new Date() + "]" + "编写消息from——to:" + params.get("smtp_account") + "——"
                + params.get("smtp_target"));
        
        InternetAddress fromAddress = new InternetAddress(params.get("smtp_account"));
        
        MimeMessage message = new MimeMessage(mailSession);
        
        message.setFrom(fromAddress);
        message.setRecipients(RecipientType.TO, params.get("smtp_target"));
        
        message.setSentDate(Calendar.getInstance().getTime());
        message.setSubject(params.get("smtp_subject"));
        message.setContent(params.get("smtp_text") + HTMLConst.TWO_EMPTY_TAGS, HTMLConst.HTML_CONTEXT_TYPE);
        // 第三步：发送消息
        Transport transport = mailSession.getTransport("smtp");
        transport.connect(params.get("smtp_server"), params.get("smtp_account"), params.get("smtp_password"));
        
        transport.send(message, message.getAllRecipients());
        Log.notice("send message yes");
        transport.close();
        Log.notice("close yes");
    }
}

class MyAuthenticator extends Authenticator
{
    String userName = "";
    String password = "";
    
    public MyAuthenticator()
    {
        
    }
    
    public MyAuthenticator(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }
    
    protected PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(userName, password);
    }
    
}
