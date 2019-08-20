package me.sensingself.sensingsugar.Common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ashishshah on 02/05/17.
 */

public class ValidationUtil
{
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String ALPHA_ONLY_PATTERN = "^[a-zA-Z]*$";
    private static final String NUMARIC_PATTERN = "^[0-9]*$";

    private static Pattern pattern;
    private static Matcher matcher;

    public static boolean validateEmailField(String email) {

        if (email.length() > 0) {
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(email);
            return matcher.matches();
        }

        return false;
    }

    public static boolean checkValidate(String mobile) {
        return mobile.length() > 1 && mobile.length() == 10;
    }

    public static boolean isAlphabetsOnly(String string) {
        return string.matches(ALPHA_ONLY_PATTERN);
    }

    public static boolean isNumaric(String string){
        return string.matches(NUMARIC_PATTERN);
    }

}
