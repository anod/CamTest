package info.anodsplace.camtest.ftp;

public class FtpError extends Exception {

    public FtpError(String exceptionMessage) {
       super(exceptionMessage);
    }

    public FtpError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }
}