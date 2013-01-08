package jabara.jetty_memcached;

import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;

import org.eclipse.jetty.nosql.memcached.spymemcached.BinarySpyMemcachedClient;

/**
 * @author jabaraster
 */
public class MemcachedClient extends BinarySpyMemcachedClient {

    private final String  username;
    private final String  password;
    private final boolean needAuth;

    /**
     * 認証不要なmemcachedサーバに接続するときにこのコンストラクタを使用して下さい.
     * 
     * @param pServersString 例）localhost:11211 <br>
     *            複数のサーバに接続する場合は" "(半角空白)区切りで指定.
     */
    public MemcachedClient(final String pServersString) {
        super(pServersString);
        this.needAuth = false;
        this.username = null;
        this.password = null;
    }

    /**
     * 認証が必要なmemcachedサーバに接続するときにこのコンストラクタを使用して下さい.
     * 
     * @param pServersString 例）localhost:11211 <br>
     *            複数のサーバに接続する場合は" "(半角空白)区切りで指定.
     * @param pUsername -
     * @param pPassword -
     */
    public MemcachedClient(final String pServersString, final String pUsername, final String pPassword) {
        super(pServersString);
        this.needAuth = true;
        this.username = pUsername;
        this.password = pPassword;
    }

    /**
     * @see org.eclipse.jetty.nosql.memcached.spymemcached.BinarySpyMemcachedClient#getConnectionFactoryBuilder()
     */
    @Override
    protected ConnectionFactoryBuilder getConnectionFactoryBuilder() {
        final ConnectionFactoryBuilder factoryBuilder = super.getConnectionFactoryBuilder();
        factoryBuilder.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY);

        if (this.needAuth) {
            final AuthDescriptor ad = new AuthDescriptor(new String[] { "PLAIN" }, new PlainCallbackHandler(this.username, this.password)); //$NON-NLS-1$
            factoryBuilder.setAuthDescriptor(ad);
        }

        return factoryBuilder;
    }
}