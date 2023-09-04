package com.example.secerrordemo.infra.session.hazelcast;

import com.example.secerrordemo.infra.session.SessionKey;
import com.example.secerrordemo.infra.session.SessionStore;
import com.hazelcast.map.IMap;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

class HazelcastSessionStore implements SessionStore {

    private static final Logger log = LoggerFactory.getLogger(HazelcastSessionStore.class);

    private final IMap<String, byte[]> sessions;

    HazelcastSessionStore(IMap<String, byte[]> sessions) {
        this.sessions = sessions;
    }

    @Override
    public void save(@Nonnull SessionKey sessionKey, @Nonnull SessionSaveJob saveJob) {
        log.trace("Saving session {} to shared cache", sessionKey);
        this.sessions.lock(sessionKey.toString());
        try (var bos = new ByteArrayOutputStream(); var oos = new ObjectOutputStream(bos)) {
            saveJob.writeAttributes((attributeName, attributeValue) -> {
                try {
                    oos.writeObject(new Attribute(attributeName, attributeValue));
                } catch (IOException ex) {
                    log.warn("Could not write attribute " + attributeName + " to session " + sessionKey, ex);
                }
            });
            oos.writeObject(null); // Terminator
            sessions.set(sessionKey.toString(), bos.toByteArray());
        } catch (Exception ex) {
            log.warn("Could not save session " + sessionKey, ex);
        } finally {
            this.sessions.unlock(sessionKey.toString());
        }
    }

    @Override
    public void load(@Nonnull SessionKey sessionKey, @Nonnull SessionAttributeSink sink) {
        this.sessions.lock(sessionKey.toString());
        try {
            var data = sessions.get(sessionKey.toString());
            if (data == null) {
                log.trace("Session {} did not exist in shared cache", sessionKey);
                return;
            }
            log.trace("Loading session {} from shared cache", sessionKey);
            try (var bis = new ByteArrayInputStream(data); var ois = new ObjectInputStream(bis)) {
                var o = ois.readObject();
                while (o instanceof Attribute a) {
                    sink.write(a.name(), a.value());
                    o = ois.readObject();
                }
            } catch (Exception ex) {
                log.warn("Could not load session " + sessionKey, ex);
            }
        } finally {
            this.sessions.unlock(sessionKey.toString());
        }
    }

    @Override
    public void delete(@Nonnull SessionKey sessionKey) {
        log.trace("Deleting session {} from shared cache", sessionKey);
        sessions.delete(sessionKey.toString());
    }

    record Attribute(@Nonnull String name, @Nullable Object value) implements Serializable {
    }
}
