/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hudson.plugins.emailext;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.plugins.emailext.plugins.trigger.SuccessTrigger;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.junit.Test;
import org.jvnet.hudson.test.TestBuilder;
import org.jvnet.mock_javamail.Mailbox;

/**
 *
 * @author acearl
 */
public class AttachmentUtilsTest extends BaseEmailTest {
    @Test
    public void testAttachmentFromWorkspace() throws Exception {
        URL url = this.getClass().getResource("/test.pdf");
        final File attachment = new File(url.getFile());
        
        publisher.attachmentsPattern = "*.pdf";
        //publisher.recipientList = "ashlux@gmail.com";
        
        SuccessTrigger trigger = new SuccessTrigger();
        addEmailType(trigger);
        
        publisher.getConfiguredTriggers().add(trigger);
        
        project.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
                build.getWorkspace().child("test.pdf").copyFrom(new FilePath(attachment));
                return true;
            }
        });
        FreeStyleBuild b = project.scheduleBuild2(0).get();     
        assertBuildStatusSuccess(b);
        
        Mailbox mbox = Mailbox.get("ashlux@gmail.com");
        assertEquals("Should have an email from success", 1, mbox.size());
        
        Message msg = mbox.get(0);
        assertTrue("Message should be multipart", msg instanceof MimeMessage);
        assertTrue("Content should be a MimeMultipart", msg.getContent() instanceof MimeMultipart);
        
        MimeMultipart part = (MimeMultipart)msg.getContent();
        
        assertEquals("Should have two body items (message + attachment)", 2, part.getCount());
        
        BodyPart attach = part.getBodyPart(1);
        assertTrue("There should be a PDF named \"test.pdf\" attached", "test.pdf".equalsIgnoreCase(attach.getFileName()));        
    }
    
    @Test
    public void testAttachmentFromWorkspaceSubdir() throws Exception {
        URL url = this.getClass().getResource("/test.pdf");
        final File attachment = new File(url.getFile());
        
        publisher.attachmentsPattern = "**/*.pdf";
        //publisher.recipientList = "ashlux@gmail.com";
        
        SuccessTrigger trigger = new SuccessTrigger();
        addEmailType(trigger);
        
        publisher.getConfiguredTriggers().add(trigger);
        
        project.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
                build.getWorkspace().child("testreport").mkdirs();
                build.getWorkspace().child("testreport").child("test.pdf").copyFrom(new FilePath(attachment));
                return true;
            }
        });
        FreeStyleBuild b = project.scheduleBuild2(0).get();     
        assertBuildStatusSuccess(b);
        
        Mailbox mbox = Mailbox.get("ashlux@gmail.com");
        assertEquals("Should have an email from success", 1, mbox.size());
        
        Message msg = mbox.get(0);
        assertTrue("Message should be multipart", msg instanceof MimeMessage);
        assertTrue("Content should be a MimeMultipart", msg.getContent() instanceof MimeMultipart);
        
        MimeMultipart part = (MimeMultipart)msg.getContent();
        
        assertEquals("Should have two body items (message + attachment)", 2, part.getCount());
        
        BodyPart attach = part.getBodyPart(1);
        assertTrue("There should be a PDF named \"test.pdf\" attached", "test.pdf".equalsIgnoreCase(attach.getFileName()));        
    }
}
