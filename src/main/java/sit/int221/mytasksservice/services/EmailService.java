package sit.int221.mytasksservice.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sit.int221.mytasksservice.models.primary.Boards;
import sit.int221.mytasksservice.models.secondary.Users;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.team.code}")
    private String teamCode;

    @Async
    public void sendInvitationEmailWithReplyTo(Users inviter, Users invitee, Boards board, String accessRight, String token) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("noreply@intproj23.sit.kmutt.ac.th", "ITBKK-" + teamCode);

        helper.setTo(invitee.getEmail());
        helper.setSubject(inviter.getName() + " has invited you to collaborate with " + accessRight + " access right on " + board.getBoard_name());

        String acceptLink = String.format("%s/v3/boards/%s/collabs/invitations/accept?token=%s", frontendUrl, board.getBoardId(), token);
        String declineLink = String.format("%s/v3/boards/%s/collabs/invitations/decline?token=%s", frontendUrl, board.getBoardId(), token);

        String htmlContent = String.format(
                "<p>%s has invited you to collaborate with <strong>%s</strong> access right on <strong>%s</strong>.</p>" +
                        "<p>Please accept the invitation by clicking the link below:</p>" +
                        "<a href=\"%s\">Accept Invitation</a>" +
                        "<p>Or decline the invitation by clicking the link below:</p>" +
                        "<a href=\"%s\">Decline Invitation</a>" +
                        "<p>If you did not expect this invitation, please ignore this email.</p>",
                inviter.getName(),
                accessRight,
                board.getBoard_name(),
                acceptLink,
                declineLink
        );

        helper.setText(htmlContent, true);

        helper.setReplyTo("DO NOT REPLY <noreply@intproj23.sit.kmutt.ac.th>");

        mailSender.send(message);
    }
}
