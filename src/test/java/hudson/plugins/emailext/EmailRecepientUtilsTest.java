package hudson.plugins.emailext;

import hudson.EnvVars;
import hudson.tasks.Mailer;
import hudson.util.FormValidation;
import java.util.Set;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Mailer.class})
public class EmailRecepientUtilsTest {
    private EmailRecepientUtils emailRecepientUtils;

    private EnvVars envVars;

    @Before
    public void before() throws NoSuchMethodException {
        emailRecepientUtils = new EmailRecepientUtils();
        envVars = new EnvVars();
    }

    @Test
    public void testConvertRecipientList_emptyRecipientStringShouldResultInEmptyEmailList()
        throws AddressException {
        Set<InternetAddress> internetAddresses = emailRecepientUtils.convertRecipientString("", envVars, false);
        Assert.assertTrue(internetAddresses.isEmpty());
    }

    @Test
    public void testConvertRecipientList_emptyRecipientStringWithWhitespaceShouldResultInEmptyEmailList()
        throws AddressException {
        Set<InternetAddress> internetAddresses = emailRecepientUtils.convertRecipientString("   ", envVars, false);

        assertTrue(internetAddresses.isEmpty());
    }

    @Test
    public void testConvertRecipientList_singleRecipientShouldResultInOneEmailAddressInList()
        throws AddressException {
        Mailer.DescriptorImpl descriptor = createMock(Mailer.DescriptorImpl.class);
        mockStatic(Mailer.class);
        expect(Mailer.descriptor()).andReturn(descriptor).anyTimes();
        expect(descriptor.getDefaultSuffix()).andReturn("@some_domain").anyTimes();
        replayAll();
        Set<InternetAddress> internetAddresses =
            emailRecepientUtils.convertRecipientString("ashlux@gmail.com", envVars, false);
        verifyAll();
        assertEquals(1, internetAddresses.size());
        assertTrue(internetAddresses.contains(new InternetAddress("ashlux@gmail.com")));
    }

    @Test
    public void testConvertRecipientList_singleRecipientWithWhitespaceShouldResultInOneEmailAddressInList()
        throws AddressException {
        Mailer.DescriptorImpl descriptor = createMock(Mailer.DescriptorImpl.class);
        mockStatic(Mailer.class);
        expect(Mailer.descriptor()).andReturn(descriptor).anyTimes();
        expect(descriptor.getDefaultSuffix()).andReturn("@some_domain").anyTimes();
        replayAll();
        Set<InternetAddress> internetAddresses =
            emailRecepientUtils.convertRecipientString(" ashlux@gmail.com ", envVars, false);
        verifyAll();
        assertEquals(1, internetAddresses.size());
        assertTrue(internetAddresses.contains(new InternetAddress("ashlux@gmail.com")));
    }

    @Test
    public void testConvertRecipientList_commaSeparatedRecipientStringShouldResultInMultipleEmailAddressesInList()
        throws AddressException {
        Mailer.DescriptorImpl descriptor = createMock(Mailer.DescriptorImpl.class);
        mockStatic(Mailer.class);
        expect(Mailer.descriptor()).andReturn(descriptor).anyTimes();
        expect(descriptor.getDefaultSuffix()).andReturn("@some_domain").anyTimes();
        replayAll();
        Set<InternetAddress> internetAddresses =
            emailRecepientUtils.convertRecipientString("ashlux@gmail.com, mickeymouse@disney.com", envVars, false);
        verifyAll();
        assertEquals(2, internetAddresses.size());
        assertTrue(internetAddresses.contains(new InternetAddress("ashlux@gmail.com")));
        assertTrue(internetAddresses.contains(new InternetAddress("mickeymouse@disney.com")));
    }

    @Test
    public void testConvertRecipientList_spaceSeparatedRecipientStringShouldResultInMultipleEmailAddressesInList()
        throws AddressException {
        Mailer.DescriptorImpl descriptor = createMock(Mailer.DescriptorImpl.class);
        mockStatic(Mailer.class);
        expect(Mailer.descriptor()).andReturn(descriptor).anyTimes();
        expect(descriptor.getDefaultSuffix()).andReturn("@some_domain").anyTimes();
        replayAll();
        Set<InternetAddress> internetAddresses =
            emailRecepientUtils.convertRecipientString("ashlux@gmail.com mickeymouse@disney.com", envVars, false);
        verifyAll();
        assertEquals(2, internetAddresses.size());
        assertTrue(internetAddresses.contains(new InternetAddress("ashlux@gmail.com")));
        assertTrue(internetAddresses.contains(new InternetAddress("mickeymouse@disney.com")));
    }

    @Test
    public void testConvertRecipientList_emailAddressesShouldBeUnique()
        throws AddressException {
        Mailer.DescriptorImpl descriptor = createMock(Mailer.DescriptorImpl.class);
        mockStatic(Mailer.class);
        expect(Mailer.descriptor()).andReturn(descriptor).anyTimes();
        expect(descriptor.getDefaultSuffix()).andReturn("@some_domain").anyTimes();
        replayAll();
        Set<InternetAddress> internetAddresses =
            emailRecepientUtils.convertRecipientString("ashlux@gmail.com, mickeymouse@disney.com, ashlux@gmail.com",
                envVars, false);
        verifyAll();
        assertEquals(2, internetAddresses.size());
        assertTrue(internetAddresses.contains(new InternetAddress("ashlux@gmail.com")));
        assertTrue(internetAddresses.contains(new InternetAddress("mickeymouse@disney.com")));
    }

    @Test
    public void testConvertRecipientList_recipientStringShouldBeExpanded()
        throws AddressException {
        Mailer.DescriptorImpl descriptor = createMock(Mailer.DescriptorImpl.class);
        mockStatic(Mailer.class);
        expect(Mailer.descriptor()).andReturn(descriptor).anyTimes();
        expect(descriptor.getDefaultSuffix()).andReturn("@some_domain").anyTimes();
        replayAll();
        envVars.put("EMAIL_LIST", "ashlux@gmail.com");

        Set<InternetAddress> internetAddresses = emailRecepientUtils.convertRecipientString("$EMAIL_LIST", envVars,
            false);
        verifyAll();
        assertEquals(1, internetAddresses.size());
        assertTrue(internetAddresses.contains(new InternetAddress("ashlux@gmail.com")));
    }

    @Test
    public void testValidateFormRecipientList_validationShouldPassAListOfGoodEmailAddresses() {
        Mailer.DescriptorImpl descriptor = createMock(Mailer.DescriptorImpl.class);
        mockStatic(Mailer.class);
        expect(Mailer.descriptor()).andReturn(descriptor).anyTimes();
        expect(descriptor.getDefaultSuffix()).andReturn("@some_domain").anyTimes();
        replayAll();
        FormValidation formValidation =
            emailRecepientUtils.validateFormRecipientList("ashlux@gmail.com internal somewhere@domain");
        replayAll();
        assertEquals(FormValidation.Kind.OK, formValidation.kind);
    }

    @Test
    public void testValidateFormRecipientList_validationShouldFailWithBadEmailAddress() {
        Mailer.DescriptorImpl descriptor = createMock(Mailer.DescriptorImpl.class);
        mockStatic(Mailer.class);
        expect(Mailer.descriptor()).andReturn(descriptor).anyTimes();
        expect(descriptor.getDefaultSuffix()).andReturn("@some_domain").anyTimes();
        replayAll();
        FormValidation formValidation =
            emailRecepientUtils.validateFormRecipientList("test@gmail.com <@gmail.com");
        replayAll();
        assertEquals(FormValidation.Kind.ERROR, formValidation.kind);
    }
}
