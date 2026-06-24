//package com.proplay.arena.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//
//@Service
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//
//    @Value("${spring.mail.username}")
//    private String fromEmail;
//
//    @Value("${app.frontend.url:http://localhost:3000}")
//    private String frontendUrl;
//
//    public EmailService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    public void sendPasswordResetEmail(String toEmail, String username, String token) {
//        String resetLink = frontendUrl + "/reset-password?token=" + token;
//
//        String html = """
//            <!DOCTYPE html>
//            <html>
//            <head>
//              <meta charset="UTF-8">
//              <meta name="viewport" content="width=device-width, initial-scale=1.0">
//            </head>
//            <body style="margin:0;padding:0;background:#050A0F;font-family:'Segoe UI',Arial,sans-serif;">
//              <div style="max-width:520px;margin:40px auto;background:#0D1F35;border:1px solid #1A3A5C;border-radius:16px;overflow:hidden;">
//                
//                <!-- Header -->
//                <div style="background:linear-gradient(135deg,#0A1628,#112240);padding:32px;text-align:center;border-bottom:1px solid #1A3A5C;">
//                  <div style="display:inline-flex;align-items:center;gap:10px;margin-bottom:8px;">
//                    <span style="font-size:24px;">⚡</span>
//                    <span style="font-family:Arial;font-size:20px;font-weight:900;color:#E8F4FD;letter-spacing:2px;">
//                      PROPLAY<span style="color:#00F5FF;">ARENA</span>
//                    </span>
//                  </div>
//                  <p style="color:#8BAFC8;margin:0;font-size:13px;">Gaming Platform</p>
//                </div>
//
//                <!-- Body -->
//                <div style="padding:36px 32px;">
//                  <div style="text-align:center;margin-bottom:28px;">
//                    <div style="width:72px;height:72px;background:rgba(0,245,255,0.1);border:2px solid rgba(0,245,255,0.3);border-radius:50%;display:inline-flex;align-items:center;justify-content:center;font-size:32px;margin-bottom:16px;">
//                      🔑
//                    </div>
//                    <h2 style="color:#E8F4FD;font-size:22px;font-weight:700;margin:0 0 8px;">Reset Your Password</h2>
//                    <p style="color:#8BAFC8;font-size:14px;margin:0;">Hey <strong style="color:#00F5FF;">%s</strong>, we got your request!</p>
//                  </div>
//
//                  <p style="color:#8BAFC8;font-size:14px;line-height:1.7;margin:0 0 24px;">
//                    Click the button below to reset your ProPlay Arena password. This link is valid for <strong style="color:#E8F4FD;">15 minutes</strong> only.
//                  </p>
//
//                  <!-- CTA Button -->
//                  <div style="text-align:center;margin:28px 0;">
//                    <a href="%s"
//                       style="display:inline-block;background:#00F5FF;color:#050A0F;text-decoration:none;padding:14px 40px;border-radius:8px;font-weight:700;font-size:15px;letter-spacing:0.5px;">
//                      🔓 Reset My Password
//                    </a>
//                  </div>
//
//                  <!-- Link fallback -->
//                  <div style="background:#0A1628;border:1px solid #1A3A5C;border-radius:8px;padding:14px 16px;margin:20px 0;">
//                    <p style="color:#4A6580;font-size:11px;margin:0 0 6px;text-transform:uppercase;letter-spacing:1px;">Or copy this link:</p>
//                    <p style="color:#00F5FF;font-size:12px;word-break:break-all;margin:0;">%s</p>
//                  </div>
//
//                  <!-- Security note -->
//                  <div style="border-left:3px solid #FFD700;padding:12px 16px;background:rgba(255,215,0,0.05);border-radius:0 8px 8px 0;margin-top:20px;">
//                    <p style="color:#8BAFC8;font-size:12px;margin:0;line-height:1.6;">
//                      ⚠️ <strong style="color:#FFD700;">Security notice:</strong> If you didn't request this, ignore this email. Your password won't change.
//                    </p>
//                  </div>
//                </div>
//
//                <!-- Footer -->
//                <div style="background:#0A1628;padding:20px 32px;text-align:center;border-top:1px solid #1A3A5C;">
//                  <p style="color:#4A6580;font-size:12px;margin:0;">
//                    © 2025 ProPlay Arena · This is an automated email, please do not reply.
//                  </p>
//                </div>
//              </div>
//            </body>
//            </html>
//            """.formatted(username, resetLink, resetLink);
//
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//            helper.setFrom(fromEmail);
//            helper.setTo(toEmail);
//            helper.setSubject("🔑 Reset Your ProPlay Arena Password");
//            helper.setText(html, true);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
//        }
//    }
//}

package com.proplay.arena.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ================= PASSWORD RESET EMAIL =================
    public void sendPasswordResetEmail(String toEmail, String username, String token) {

        try {
            // 🔒 Safe values (avoid null issues)
            String safeUsername = (username != null && !username.isBlank()) ? username : "Player";
            String safeToken = (token != null && !token.isBlank()) ? token : "";

            String resetLink = frontendUrl + "/reset-password?token=" + safeToken;

            // 🧾 HTML TEMPLATE (NO %s — no formatting bugs)
            String html =
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "</head>" +
                "<body style='margin:0;padding:0;background:#050A0F;font-family:Segoe UI,Arial,sans-serif;'>" +

                "<div style='max-width:520px;margin:40px auto;background:#0D1F35;border:1px solid #1A3A5C;border-radius:16px;overflow:hidden;'>" +

                // Header
                "<div style='background:linear-gradient(135deg,#0A1628,#112240);padding:32px;text-align:center;border-bottom:1px solid #1A3A5C;'>" +
                "<h2 style='color:#E8F4FD;margin:0;'>⚡ PROPLAY ARENA</h2>" +
                "<p style='color:#8BAFC8;margin:4px 0 0;font-size:13px;'>Gaming Platform</p>" +
                "</div>" +

                // Body
                "<div style='padding:32px;'>" +
                "<h3 style='color:#E8F4FD;'>Reset Your Password</h3>" +

                "<p style='color:#8BAFC8;'>Hey <strong style='color:#00F5FF;'>" + safeUsername + "</strong>,</p>" +

                "<p style='color:#8BAFC8;'>Click the button below to reset your password. This link is valid for 15 minutes.</p>" +

                "<div style='text-align:center;margin:24px 0;'>" +
                "<a href='" + resetLink + "' " +
                "style='background:#00F5FF;color:#050A0F;padding:12px 30px;text-decoration:none;border-radius:8px;font-weight:bold;'>" +
                "Reset Password</a>" +
                "</div>" +

                "<p style='color:#8BAFC8;font-size:12px;'>Or copy this link:</p>" +
                "<p style='color:#00F5FF;font-size:12px;word-break:break-all;'>" + resetLink + "</p>" +

                "<p style='color:#8BAFC8;font-size:12px;margin-top:20px;'>If you didn’t request this, ignore this email.</p>" +

                "</div>" +

                // Footer
                "<div style='background:#0A1628;padding:16px;text-align:center;border-top:1px solid #1A3A5C;'>" +
                "<p style='color:#4A6580;font-size:12px;margin:0;'>© 2025 ProPlay Arena</p>" +
                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";

            // 📧 Send Email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Reset Your ProPlay Arena Password");
            helper.setText(html, true);

            mailSender.send(message);

            // ✅ Debug success
            System.out.println("✅ Email sent to: " + toEmail);

        } catch (Exception e) {
            // ❌ Proper error logging
            System.out.println("❌ Email failed: " + e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
