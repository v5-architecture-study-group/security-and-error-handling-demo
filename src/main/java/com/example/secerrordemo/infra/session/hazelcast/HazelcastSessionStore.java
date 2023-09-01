package com.example.secerrordemo.infra.session.hazelcast;

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
    public void save(@Nonnull String sessionId, @Nonnull SessionSaveJob saveJob) {
        log.debug("Saving session {} to shared cache", sessionId);
        this.sessions.lock(sessionId);
        try (var bos = new ByteArrayOutputStream(); var oos = new ObjectOutputStream(bos)) {
            saveJob.writeAttributes((attributeName, attributeValue) -> {
                try {
                    oos.writeObject(new Attribute(attributeName, attributeValue));
                } catch (IOException ex) {
                    log.error("Could not write attribute " + attributeName + " to session " + sessionId, ex);
                }
            });
            oos.writeObject(null); // Terminator
            sessions.set(sessionId, bos.toByteArray());
        } catch (Exception ex) {
            log.error("Could not save session " + sessionId, ex);
        } finally {
            this.sessions.unlock(sessionId);
        }
    }

    @Override
    public void load(@Nonnull String sessionId, @Nonnull SessionAttributeSink sink) {
        var data = sessions.get(sessionId);
        if (data != null) {
            log.debug("Loading session {} from shared cache", sessionId);
            try (var bis = new ByteArrayInputStream(data); var ois = new ObjectInputStream(bis)) {
                var o = ois.readObject();
                while (o instanceof Attribute a) {
                    sink.write(a.name(), a.value());
                    o = ois.readObject();
                }
            } catch (Exception ex) {
                log.error("Could not load session " + sessionId, ex);
            }
        } else {
            log.debug("Session {} did not exist in shared cache", sessionId);
        }
    }

    @Override
    public void delete(@Nonnull String sessionId) {
        log.debug("Deleting session {} from shared cache", sessionId);
        sessions.delete(sessionId);
    }

    record Attribute(@Nonnull String name, @Nullable Object value) implements Serializable {
    }
}
