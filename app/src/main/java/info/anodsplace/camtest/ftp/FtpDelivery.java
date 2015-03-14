package info.anodsplace.camtest.ftp;

public interface FtpDelivery {
    /**
     * Parses a response from the network or cache and delivers it.
     */
    public void postDelivered(FtpRequest request);

    /**
     * Posts an error for the given request.
     */
    public void postError(FtpRequest request, FtpError error);
}
