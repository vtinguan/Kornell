package kornell.gui.client;

import java.util.MissingResourceException;

import kornell.core.error.KornellErrorTO;

import com.google.gwt.core.client.GWT;

public class KornellConstantsHelper {

    private static KornellConstants constants = GWT.create(KornellConstants.class);

    public static String getMessage(String key) {
        try {
            String errorMessage = constants.getString(key);
            return errorMessage;
        } catch (MissingResourceException e) {
            return "Message not set for key [" + key + "]";
        }
    }

    public static String getErrorMessage(KornellErrorTO kornellErrorTO) {
        return getMessage(kornellErrorTO.getMessageKey());
    }
}
