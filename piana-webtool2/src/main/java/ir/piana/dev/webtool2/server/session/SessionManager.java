package ir.piana.dev.webtool2.server.session;

import ir.piana.dev.secure.PianaSecureException;
import ir.piana.dev.secure.cache.PianaCacheProvider;
import ir.piana.dev.secure.key.KeyPairAlgorithm;
import ir.piana.dev.secure.key.KeyPairMaker;
import ir.piana.dev.webtool2.server.annotation.PianaServerSession;
import ir.piana.dev.webtool2.server.role.RoleType;
import org.apache.log4j.Logger;

import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    protected PianaServerSession serverSession;

    public SessionManager(
            PianaServerSession serverSession) {
        this.serverSession = serverSession;
        cacheProvider =
                PianaCacheProvider.getInstance(
                        key -> createNewSession(serverSession),
                        serverSession.sessionCacheSize(),
                        serverSession.sessionExpiredSecond());
    }

    public static SessionManager createSessionManager(
            PianaServerSession serverSession) {
        return new SessionManager(
                serverSession);
    }

    public Session revivalSession(
            HttpHeaders httpHeaders) {
        Session session = null;
        try {
            Cookie cookie = httpHeaders.getCookies()
                    .get(serverSession.sessionName());
            if(cookie == null ||
                    cookie.getValue() == null ||
                    cookie.getValue().isEmpty())
                return null;
            session = (Session) cacheProvider
                    .retrieveIfExist(cookie.getValue());
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
                    .get(serverSession.sessionName());
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

    public Session retrieveSessionIfExist(
            String sessionKey) throws Exception {
        return  (Session) cacheProvider
                        .retrieveIfExist(sessionKey);
    }

    public List<NewCookie> clearOtherCookies(
            Session session,
            HttpHeaders httpHeaders) {
        Map<String, Cookie> cookieMap =
                httpHeaders.getCookies();
        List<NewCookie> cookies = new ArrayList<>();
        cookieMap.forEach((cKey, cValue) -> {
            if(!cKey.equalsIgnoreCase(
                    serverSession.sessionName())) {
                cookies.add(new NewCookie(
                        cKey, "", "/", "", "", 0, false));
            }
        });
        return cookies;
    }

    public NewCookie makeSessionCookie(
            Session session) {
        if(session == null) {
            logger.info("session is null.");
            return null;
        }
        return new NewCookie(
                serverSession.sessionName(),
                session.getSessionKey(), "/", "", null,
                serverSession.sessionExpiredSecond(),
                false, false);
    }

    public NewCookie[] removeOtherCookies(
            Session session, HttpHeaders httpHeaders) {
        NewCookie[] cookies = null;
        List<NewCookie> newCookieList =
                clearOtherCookies(session, httpHeaders);
        if(newCookieList != null)
            cookies = new NewCookie[1 + newCookieList.size()];
        else
            return new NewCookie[] {makeSessionCookie(session)};
            cookies[0] = makeSessionCookie(session);
        for(int i = 1; i < cookies.length; i++)
            cookies[i] = newCookieList.get(i - 1);
        return cookies;
    }

    private static Session createNewSession(
            PianaServerSession serverSession)
            throws PianaSecureException {
        return new Session(
                serverSession.sessionName(),
                KeyPairMaker.createKeyPair(
                        KeyPairAlgorithm.RSA_1024),
                RoleType.GUEST
        );
    }

    private static String createSessionKey() {
        return UUID.randomUUID().toString();
    }

}
