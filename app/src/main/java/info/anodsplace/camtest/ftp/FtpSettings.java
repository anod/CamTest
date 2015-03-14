package info.anodsplace.camtest.ftp;

/**
 * @author alex
 * @date 2015-03-12
 */
public class FtpSettings {
    public static final int PORT_DEFAULT = 21;
    public String ip;
    public int port;
    public String username;
    public String password;

    public FtpSettings(String ip, int port, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public int getPort() {
        return port == 0 ? PORT_DEFAULT : port;
    }


    public String getHost() {
        return ip;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return username+"@"+ip+":"+getPort();
    }
}
