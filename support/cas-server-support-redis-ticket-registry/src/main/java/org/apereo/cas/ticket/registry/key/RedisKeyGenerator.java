package org.apereo.cas.ticket.registry.key;

import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketCatalog;
import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

/**
 * This is {@link RedisKeyGenerator}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
public interface RedisKeyGenerator {
    /**
     * Redis message topic key used to sync memory cache across nodes.
     */
    String REDIS_TICKET_REGISTRY_MESSAGE_TOPIC = "redisTicketRegistryMessageTopic";
    
    /**
     * Gets type.
     *
     * @return the type
     */
    String getPrefix();

    /**
     * For entry string.
     *
     * @param entry the entry
     * @return the string
     */
    String forId(String entry);

    /**
     * For entry string.
     *
     * @param type  the type
     * @param entry the entry
     * @return the string
     */
    String forPrefixAndId(String type, String entry);
    
    /**
     * Raw key string.
     *
     * @param type the type
     * @return the string
     */
    String rawKey(String type);

    /**
     * Gets namespace.
     *
     * @return the namespace
     */
    String getNamespace();

    /**
     * Gets ticket catalog.
     *
     * @return the ticket catalog
     */
    TicketCatalog getTicketCatalog();

    /**
     * Gets keyspace.
     *
     * @return the keyspace
     */
    String getKeyspace();

    /**
     * Is ticket key generator?.
     *
     * @return the boolean
     */
    boolean isTicketKeyGenerator();

    /**
     * If no time out value is specified, expire the ticket immediately.
     *
     * @param ticket the ticket
     * @return timeout
     */
    static Long getTicketExpirationInSeconds(final Ticket ticket) {
        val ttl = ticket.getExpirationPolicy().getTimeToLive();
        if (ttl > Integer.MAX_VALUE) {
            return (long) Integer.MAX_VALUE;
        }
        return ttl <= 0 ? 1L : ttl;
    }

    /**
     * Parse key into redis composite key.
     *
     * @param redisKeyPattern the redis key pattern
     * @return the redis composite key
     */
    static RedisCompositeKey parseKey(final String redisKeyPattern) {
        var patternBits = Splitter.on(':').splitToList(redisKeyPattern);
        if (patternBits.size() == 1) {
            return RedisCompositeKey.builder()
                .namespace(patternBits.getFirst()).build();
        }
        if (patternBits.size() == 2) {
            return RedisCompositeKey.builder().namespace(patternBits.getFirst())
                .prefix(patternBits.getLast()).build();
        }
        if (patternBits.size() == 3) {
            return RedisCompositeKey.builder().namespace(patternBits.getFirst())
                .prefix(patternBits.get(1)).id(patternBits.getLast()).build();
        }
        throw new IllegalArgumentException("Unable to parse pattern for key " + redisKeyPattern);
    }

    @SuperBuilder
    @Getter
    class RedisCompositeKey {
        private final String namespace;

        private final String prefix;

        private final String id;

        @Override
        public String toString() {
            var pattern = StringUtils.EMPTY;
            if (StringUtils.isNotBlank(namespace)) {
                pattern += namespace;
            }
            if (StringUtils.isNotBlank(prefix)) {
                pattern += ':' + prefix;
            }
            if (StringUtils.isNotBlank(id)) {
                pattern += ':' + id;
            }
            return pattern;
        }
    }
}
