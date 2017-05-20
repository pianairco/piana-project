package ir.piana.dev.server.session;

import ir.piana.dev.secure.PianaSecureException;
import ir.piana.dev.secure.cache.PianaCacheProvider;
import ir.piana.dev.secure.key.KeyPairAlgorithm;
import ir.piana.dev.secure.key.KeyPairMaker;
import ir.piana.dev.server.config.PianaServerConfig.PianaSessionConfig;
import ir.piana.dev.server.role.RoleType;
import org.apache.log4j.Logger;

import javax.ws.rs.core.*;
import java.util.UUID;

/**
 * @author Mohammad Rahmati, 4/29/2017 2:15 PM
 */
public class SessionManager {
    final static Logger logger =
            Logger.getLogger(SessionManager.class);
    public static final String PIANA_SESSION_MANAGER =
            "piana-session-manager";
    private PianaCacheProvider cacheProvider = null;
    protected PianaSessionConfig sessionConfig;

    public SessionManager(
            PianaSessionConfig sessionConfig) {
        this.sessionConfig = sessionConfig;
        cacheProvider =
                PianaCacheProvider.getInstance(
                        key -> createNewSession(),
                        sessionConfig.getSessionCacheSize(),
                        sessionConfig.getSessionExpireSecond());
    }

    public static SessionManager createSessionManager(
            PianaSessionConfig sessionConfig) {
        return new SessionManager(
                sessionConfig);
    }

    public Session revivalSession(
            HttpHeaders httpHeaders) {
        Session session = null;
        try {
            Cookie cookie = httpHeaders.getCookies()
                    .get(sessionConfig.getSessionName());
            if(cookie == null ||
                    cookie.getValue() == null ||
                    cookie.getValue().isEmpty())
                return null;
            session = (Session) cacheProvider
                    .retrieve(cookie.getValue());
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return session;
    }

    public Session retrieveSession(
            HttpHeaders httpHeaders) {
        Session session = null;
        String sessionKey = null;
        try {
            Cookie cookie = httpHeaders.getCookies()
                    .get(sessionConfig.getSessionName());
            if(cookie != null) {
                sessionKey = cookie.getValue();
                if(sessionKey == null || sessionKey.isEmpty())
                    sessionKey = createSessionKey();
                session = (Session) cacheProvider
                        .retrieve(sessionKey);
            } else {
                sessionKey = createSessionKey();
                session = (Session) cacheProvider
                        .retrieve(sessionKey);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        session.setSessionKey(sessionKey);
        return session;
    }

    public NewCookie registerCookie(
            Session session) {
        if(session == null) {
            logger.info("session is null.");
            return null;
        }
        return new NewCookie(
                sessionConfig.getSessionName(),
                session.getSessionKey(), "/", "", null,
                sessionConfig.getSessionExpireSecond(),
                false, false);
    }

    private static Session createNewSession()
            throws PianaSecureException {
        return new Session(
                KeyPairMaker.createKeyPair(
                        KeyPairAlgorithm.RSA_1024),
                RoleType.GUEST
        );
    }

    private static String createSessionKey() {
        return UUID.randomUUID().toString();
    }

}
