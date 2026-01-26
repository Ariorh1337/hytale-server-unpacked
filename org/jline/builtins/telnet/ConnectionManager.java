/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins.telnet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jline.builtins.telnet.Connection;
import org.jline.builtins.telnet.ConnectionData;
import org.jline.builtins.telnet.ConnectionEvent;
import org.jline.builtins.telnet.ConnectionFilter;

public abstract class ConnectionManager
implements Runnable {
    private static Logger LOG = Logger.getLogger(ConnectionManager.class.getName());
    private final List<Connection> openConnections;
    private Thread thread;
    private ThreadGroup threadGroup = new ThreadGroup(this.toString() + "Connections");
    private Stack<Connection> closedConnections = new Stack();
    private ConnectionFilter connectionFilter;
    private int maxConnections;
    private int warningTimeout;
    private int disconnectTimeout;
    private int housekeepingInterval;
    private String loginShell;
    private boolean lineMode = false;
    private boolean stopping = false;

    public ConnectionManager() {
        this.openConnections = Collections.synchronizedList(new ArrayList(100));
    }

    public ConnectionManager(int con, int timew, int timedis, int hoke, ConnectionFilter filter, String lsh, boolean lm) {
        this();
        this.connectionFilter = filter;
        this.loginShell = lsh;
        this.lineMode = lm;
        this.maxConnections = con;
        this.warningTimeout = timew;
        this.disconnectTimeout = timedis;
        this.housekeepingInterval = hoke;
    }

    public ConnectionFilter getConnectionFilter() {
        return this.connectionFilter;
    }

    public void setConnectionFilter(ConnectionFilter filter) {
        this.connectionFilter = filter;
    }

    public int openConnectionCount() {
        return this.openConnections.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Connection getConnection(int idx) {
        List<Connection> list = this.openConnections;
        synchronized (list) {
            return this.openConnections.get(idx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Connection[] getConnectionsByAdddress(InetAddress addr) {
        ArrayList<Connection> l = new ArrayList<Connection>();
        List<Connection> list = this.openConnections;
        synchronized (list) {
            for (Connection connection : this.openConnections) {
                if (!connection.getConnectionData().getInetAddress().equals(addr)) continue;
                l.add(connection);
            }
        }
        Connection[] conns = new Connection[l.size()];
        return l.toArray(conns);
    }

    public void start() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        LOG.log(Level.FINE, "stop()::" + this.toString());
        this.stopping = true;
        try {
            if (this.thread != null) {
                this.thread.join();
            }
        }
        catch (InterruptedException iex) {
            LOG.log(Level.SEVERE, "stop()", iex);
        }
        List<Connection> list = this.openConnections;
        synchronized (list) {
            for (Connection tc : this.openConnections) {
                try {
                    tc.close();
                }
                catch (Exception exc) {
                    LOG.log(Level.SEVERE, "stop()", exc);
                }
            }
            this.openConnections.clear();
        }
        LOG.log(Level.FINE, "stop():: Stopped " + this.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void makeConnection(Socket insock) {
        LOG.log(Level.FINE, "makeConnection()::" + insock.toString());
        if (this.connectionFilter == null || this.connectionFilter.isAllowed(insock.getInetAddress())) {
            ConnectionData newCD = new ConnectionData(insock, this);
            newCD.setLoginShell(this.loginShell);
            newCD.setLineMode(this.lineMode);
            if (this.openConnections.size() < this.maxConnections) {
                Connection con = this.createConnection(this.threadGroup, newCD);
                Object[] args = new Object[]{this.openConnections.size() + 1};
                LOG.info(MessageFormat.format("connection #{0,number,integer} made.", args));
                List<Connection> list = this.openConnections;
                synchronized (list) {
                    this.openConnections.add(con);
                }
                con.start();
            }
        } else {
            LOG.info("makeConnection():: Active Filter blocked incoming connection.");
            try {
                insock.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    protected abstract Connection createConnection(ThreadGroup var1, ConnectionData var2);

    @Override
    public void run() {
        try {
            do {
                this.cleanupClosed();
                this.checkOpenConnections();
                Thread.sleep(this.housekeepingInterval);
            } while (!this.stopping);
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "run()", e);
        }
        LOG.log(Level.FINE, "run():: Ran out " + this.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cleanupClosed() {
        if (this.stopping) {
            return;
        }
        while (!this.closedConnections.isEmpty()) {
            Connection nextOne = this.closedConnections.pop();
            LOG.info("cleanupClosed():: Removing closed connection " + nextOne.toString());
            List<Connection> list = this.openConnections;
            synchronized (list) {
                this.openConnections.remove(nextOne);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkOpenConnections() {
        if (this.stopping) {
            return;
        }
        List<Connection> list = this.openConnections;
        synchronized (list) {
            for (Connection conn : this.openConnections) {
                ConnectionData cd = conn.getConnectionData();
                if (!conn.isActive()) {
                    this.registerClosedConnection(conn);
                    continue;
                }
                long inactivity = System.currentTimeMillis() - cd.getLastActivity();
                if (inactivity <= (long)this.warningTimeout) continue;
                if (inactivity > (long)(this.disconnectTimeout + this.warningTimeout)) {
                    LOG.log(Level.FINE, "checkOpenConnections():" + conn.toString() + " exceeded total timeout.");
                    conn.processConnectionEvent(new ConnectionEvent(conn, ConnectionEvent.Type.CONNECTION_TIMEDOUT));
                    continue;
                }
                if (cd.isWarned()) continue;
                LOG.log(Level.FINE, "checkOpenConnections():" + conn.toString() + " exceeded warning timeout.");
                cd.setWarned(true);
                conn.processConnectionEvent(new ConnectionEvent(conn, ConnectionEvent.Type.CONNECTION_IDLE));
            }
        }
    }

    public void registerClosedConnection(Connection con) {
        if (this.stopping) {
            return;
        }
        if (!this.closedConnections.contains(con)) {
            LOG.log(Level.FINE, "registerClosedConnection()::" + con.toString());
            this.closedConnections.push(con);
        }
    }

    public int getDisconnectTimeout() {
        return this.disconnectTimeout;
    }

    public void setDisconnectTimeout(int disconnectTimeout) {
        this.disconnectTimeout = disconnectTimeout;
    }

    public int getHousekeepingInterval() {
        return this.housekeepingInterval;
    }

    public void setHousekeepingInterval(int housekeepingInterval) {
        this.housekeepingInterval = housekeepingInterval;
    }

    public boolean isLineMode() {
        return this.lineMode;
    }

    public void setLineMode(boolean lineMode) {
        this.lineMode = lineMode;
    }

    public String getLoginShell() {
        return this.loginShell;
    }

    public void setLoginShell(String loginShell) {
        this.loginShell = loginShell;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getWarningTimeout() {
        return this.warningTimeout;
    }

    public void setWarningTimeout(int warningTimeout) {
        this.warningTimeout = warningTimeout;
    }
}

