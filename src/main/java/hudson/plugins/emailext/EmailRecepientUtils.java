package hudson.plugins.emailext;

import hudson.EnvVars;
import hudson.tasks.Mailer;
import hudson.util.FormValidation;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang.StringUtils;

public class EmailRecepientUtils {
    public static final String COMMA_SEPARATED_SPLIT_REGEXP = "[,\\s]+";

    public Set<InternetAddress> convertRecipientString(String recipientList, EnvVars envVars, boolean skipInvalid)
        throws AddressException {
        final Set<InternetAddress> internetAddresses = new LinkedHashSet<InternetAddress>();
        if (StringUtils.isBlank(recipientList)) {
            return internetAddresses;
        }

        final String expandedRecipientList = envVars.expand(recipientList);
        final String[] addresses = StringUtils.trim(expandedRecipientList).split(COMMA_SEPARATED_SPLIT_REGEXP);
        String ds = Mailer.descriptor().getDefaultSuffix();
        for (String address : addresses) {
            if (!StringUtils.contains(address, "@") && ds != null) {
                address += ds;
            }
            try {
                InternetAddress internetAddress = new InternetAddress(address);
                internetAddresses.add(internetAddress);
            } catch (AddressException e) {
                if (!skipInvalid) {
                    throw e;
                }
            }
        }

        return internetAddresses;
    }

    public FormValidation validateFormRecipientList(String recipientList) {
        // Try and convert the recipient string to a list of InternetAddress. If this fails then the validation fails.
        try {
            convertRecipientString(recipientList, new EnvVars(), false);
            return FormValidation.ok();
        } catch (AddressException e) {
            return FormValidation.error(e.getMessage() + ": \"" + e.getRef() + "\"");
        }
    }
}
