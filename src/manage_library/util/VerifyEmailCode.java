package manage_library.util;

public class VerifyEmailCode {
    private String code = null;
    private static VerifyEmailCode instance;

    private VerifyEmailCode() {
    }

    public static VerifyEmailCode getInstance() {
        if (instance == null) {
            instance = new VerifyEmailCode();
        }
        return instance;
    }

    public void checkCodeEmail(String codesent) {
        this.code = codesent;
    }

    public String code() {
        return this.code;
    }
}
