package hudson.plugins.emailext;

import hudson.model.FreeStyleProject;
import hudson.plugins.emailext.plugins.EmailTrigger;
import org.junit.Before;
import org.junit.Ignore;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.mock_javamail.Mailbox;

/**
 *
 * @author Bob Foster
 */
public class BaseEmailTest extends HudsonTestCase {
    protected ExtendedEmailPublisher publisher;

    protected FreeStyleProject project;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        publisher = new ExtendedEmailPublisher();
        publisher.defaultSubject = "%DEFAULT_SUBJECT";
        publisher.defaultContent = "%DEFAULT_CONTENT";

        project = createFreeStyleProject();
        project.addPublisher( publisher );
        
        Mailbox.clearAll();
    }

    protected void addEmailType( EmailTrigger trigger )
    {
        EmailType type = new EmailType();
        type.setRecipientList( "ashlux@gmail.com" );
        type.setSubject( "Yet another Hudson email" );
        type.setBody( "Boom goes the dynamite." );
        trigger.setEmail(type);
        /* This is a bad idiom. It makes the test fail for a serialization error.
        trigger.setEmail( new EmailType()
        {{
                setRecipientList( "mickey@disney.com" );
                setSubject( "Yet another Hudson email" );
                setBody( "Boom goes the dynamite." );
            }} );
        */
    }
    
    @Ignore
    public void test() {
        // dummy test
    }

}
