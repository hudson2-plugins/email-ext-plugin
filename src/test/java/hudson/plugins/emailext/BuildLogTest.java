package hudson.plugins.emailext;

import hudson.model.FreeStyleBuild;
import hudson.plugins.emailext.plugins.trigger.SuccessTrigger;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

/**
 *
 * @author Bob Foster
 */
public class BuildLogTest extends BaseEmailTest {
     @Test
    public void testAttachBuildLogUncompressed() throws Exception {
        publisher.attachBuildLog = true;
        
        SuccessTrigger trigger = new SuccessTrigger();
        addEmailType(trigger);
        
        publisher.getConfiguredTriggers().add(trigger);
        
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
        assertTrue("There should be a file named \"build.log\" attached", "build.log".equalsIgnoreCase(attach.getFileName()));        
    }
    
    @Test
    public void testAttachmentFromWorkspaceSubdir() throws Exception {
        publisher.attachBuildLog = true;
        publisher.compressBuildLog = true;
        
        SuccessTrigger trigger = new SuccessTrigger();
        addEmailType(trigger);
        
        publisher.getConfiguredTriggers().add(trigger);
        
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
        assertTrue("There should be a file named \"build.zip\" attached", "build.zip".equalsIgnoreCase(attach.getFileName()));        
    }
}
