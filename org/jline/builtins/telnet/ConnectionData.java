/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins.telnet;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jline.builtins.telnet.ConnectionManager;

public class ConnectionData {
    private ConnectionManager connectionManager;
    private Socket socket;
    private InetAddress address;
    private Map<String, String> environment;
    private String hostName;
    private String hostAddress;
    private int port;
    private Locale locale;
    private long lastActivity;
    private boolean warned;
    private String negotiatedTerminalType;
    private int[] terminalGeometry;
    private boolean terminalGeometryChanged = true;
    private String loginShell;
    private boolean lineMode = false;

    public ConnectionData(Socket sock, ConnectionManager cm) {
        this.socket = sock;
        this.connectionManager = cm;
        this.address = sock.getInetAddress();
        this.setHostName();
        this.setHostAddress();
        this.setLocale();
        this.port = sock.getPort();
        this.terminalGeometry = new int[2];
        this.terminalGeometry[0] = 80;
        this.terminalGeometry[1] = 25;
        this.negotiatedTerminalType = "default";
        this.environment = new HashMap<String, String>(20);
        this.activity();
    }

    public ConnectionManager getManager() {
        return this.connectionManager;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public int getPort() {
        return this.port;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getHostAddress() {
        return this.hostAddress;
    }

    public InetAddress getInetAddress() {
        return this.address;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public long getLastActivity() {
        return this.lastActivity;
    }

    public void activity() {
        this.warned = false;
        this.lastActivity = System.currentTimeMillis();
    }

    public boolean isWarned() {
        return this.warned;
    }

    public void setWarned(boolean bool) {
        this.warned = bool;
        if (!bool) {
            this.lastActivity = System.currentTimeMillis();
        }
    }

    public void setTerminalGeometry(int width, int height) {
        this.terminalGeometry[0] = width;
        this.terminalGeometry[1] = height;
        this.terminalGeometryChanged = true;
    }

    public int[] getTerminalGeometry() {
        if (this.terminalGeometryChanged) {
            this.terminalGeometryChanged = false;
        }
        return this.terminalGeometry;
    }

    public int getTerminalColumns() {
        return this.terminalGeometry[0];
    }

    public int getTerminalRows() {
        return this.terminalGeometry[1];
    }

    public boolean isTerminalGeometryChanged() {
        return this.terminalGeometryChanged;
    }

    public String getNegotiatedTerminalType() {
        return this.negotiatedTerminalType;
    }

    public void setNegotiatedTerminalType(String termtype) {
        this.negotiatedTerminalType = termtype;
    }

    public Map<String, String> getEnvironment() {
        return this.environment;
    }

    public String getLoginShell() {
        return this.loginShell;
    }

    public void setLoginShell(String s) {
        this.loginShell = s;
    }

    public boolean isLineMode() {
        return this.lineMode;
    }

    public void setLineMode(boolean b) {
        this.lineMode = b;
    }

    private void setHostName() {
        this.hostName = this.address.getHostName();
    }

    private void setHostAddress() {
        this.hostAddress = this.address.getHostAddress();
    }

    private void setLocale() {
        String country = this.getHostName();
        try {
            country = country.substring(country.lastIndexOf(".") + 1);
            this.locale = country.equals("at") ? ConnectionData.localeOf("de", "AT") : (country.equals("de") ? ConnectionData.localeOf("de", "DE") : (country.equals("mx") ? ConnectionData.localeOf("es", "MX") : (country.equals("es") ? ConnectionData.localeOf("es", "ES") : (country.equals("it") ? Locale.ITALY : (country.equals("fr") ? Locale.FRANCE : (country.equals("uk") ? Locale.UK : (country.equals("arpa") ? Locale.US : (country.equals("com") ? Locale.US : (country.equals("edu") ? Locale.US : (country.equals("gov") ? Locale.US : (country.equals("org") ? Locale.US : (country.equals("mil") ? Locale.US : Locale.ENGLISH))))))))))));
        }
        catch (Exception ex) {
            this.locale = Locale.ENGLISH;
        }
    }

    private static Locale localeOf(String language, String country) {
        return new Locale(language, country);
    }
}

