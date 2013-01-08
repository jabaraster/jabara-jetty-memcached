package jabara.jetty_memcached;

import jabara.general.ExceptionUtil;
import jabara.jetty.ServerStarter;

import java.io.IOException;

import org.eclipse.jetty.nosql.kvs.AbstractKeyValueStoreClient;
import org.eclipse.jetty.nosql.memcached.AbstractMemcachedClientFactory;
import org.eclipse.jetty.nosql.memcached.MemcachedSessionIdManager;
import org.eclipse.jetty.nosql.memcached.MemcachedSessionManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;

/**
 * @author jabaraster
 */
public class MemcachedSessionServerStarter extends ServerStarter {
    /**
     * 
     */
    public static final String KEY_MEMCACHED_SERVERS  = "memcached.servers"; //$NON-NLS-1$
    /**
     * 
     */
    public static final String KEY_MEMCACHED_USERNAME = "memcached.username"; //$NON-NLS-1$
    /**
     * 
     */
    public static final String KEY_MEMCACHED_PASSWORD = "memcached.password"; //$NON-NLS-1$

    /**
     * @see jabara.jetty.ServerStarter#beforeCreateServer()
     */
    @Override
    protected void beforeCreateServer() {
        if (!hasMemcachedServersDirective()) {
            return;
        }
        try {
            getWebAppContext().setSessionHandler(createSessionHandler(getServer()));
        } catch (final IOException e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private static MemcachedSessionIdManager createMemcachedSessionIdManager(final Server pServer) throws IOException {
        final String serverString = System.getProperty(KEY_MEMCACHED_SERVERS);
        final MemcachedSessionIdManager memcachedSessionIdManager = new MemcachedSessionIdManager(pServer, serverString,
                new AbstractMemcachedClientFactory() {
                    @Override
                    public AbstractKeyValueStoreClient create(final String pServerString) {
                        final String username = System.getProperty(KEY_MEMCACHED_USERNAME);
                        if (username == null) {
                            return new MemcachedClient(pServerString);
                        }
                        final String password = System.getProperty(KEY_MEMCACHED_PASSWORD);
                        return new MemcachedClient(pServerString, username, password);
                    }
                });
        memcachedSessionIdManager.setKeyPrefix("session:"); //$NON-NLS-1$

        pServer.setSessionIdManager(memcachedSessionIdManager);

        return memcachedSessionIdManager;
    }

    private static SessionHandler createSessionHandler(final Server pServer) throws IOException {
        final MemcachedSessionManager memcachedSessionManager = new MemcachedSessionManager();
        memcachedSessionManager.setSessionIdManager(createMemcachedSessionIdManager(pServer));
        return new SessionHandler(memcachedSessionManager);
    }

    private static boolean hasMemcachedServersDirective() {
        return System.getProperty(KEY_MEMCACHED_SERVERS) != null;
    }
}
