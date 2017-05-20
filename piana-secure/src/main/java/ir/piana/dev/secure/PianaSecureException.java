package ir.piana.dev.secure;

/**
 * @author Mohammad Rahmati, 4/15/2017 7:10 AM
 */
public class PianaSecureException
        extends Exception {
    private static final String EXCEPTION_CLASS_NAME =
            "shb-secure";

    public PianaSecureException() {
        super(EXCEPTION_CLASS_NAME
                .concat(":")
                .concat("unknown message"));
    }

    public PianaSecureException(String message) {
        super(EXCEPTION_CLASS_NAME
                .concat(":").concat(message));
    }

    public PianaSecureException(Exception exception) {
        super(EXCEPTION_CLASS_NAME
                .concat(":")
                .concat(exception.getMessage()));
    }
}
