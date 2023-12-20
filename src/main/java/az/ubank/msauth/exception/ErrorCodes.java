package az.ubank.msauth.exception;

public class ErrorCodes {
    public static final String UNEXPECTED_EXCEPTION = "auth.unexpected-exception";
    public static final String NOT_FOUND = "auth.user-not-found-exception";
    public static final String EXISTED_USER_FOUND = "auth.user-existed-exception";
    public static final String WRONG_PASSWORD_EXCEPTION = "auth.wrong-password-exception";
    public static final String PASSWORD_WRONG_REGEX = "auth.password-regex-wrong-exception";
    public static final String EMAIL_WRONG = "auth.email-regex-wrong-exception";
    public static final String NULL_FIELD = "auth.null-field-exception";
    public static final String INCORRECT_PIN = "auth.customer-pin-field-exception";

    private ErrorCodes() {
    }
}
