package com.cqrs.eventstore.sql.dbi;

import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.cqrs.eventstore.sql.EventMapper;

public interface EventStreams {
    @SqlQuery("select e.stream_id as streamId, e.event_id as id, e.data, e.version, e.type " +
            "from events as e join streams as s on s.id=e.stream_id where s.id = :streamId")
    @RegisterMapper(EventMapper.class)
    Iterable<Map> loadEvents(@Bind("streamId") String streamId);
    
    @SqlQuery("select DISTINCT(stream_id) from events")
    List<String> getAllAggregateIds();

    @SqlQuery("select version from streams where id = :streamId")
    Integer getCurrentStreamVersion(@Bind("streamId") String streamId);
    
    @SqlUpdate("insert into streams (id, type, version) values (:streamId, :type, :version)")
    int createNewStream(@Bind("type") String type, @Bind("streamId") String streamId, @Bind("version") int version);

    @SqlUpdate("insert into events (event_id, data, type, version, stream_id) values (:eventId, :data, :type, :version, :streamId)")
    void appendNewEvent(@Bind("streamId") String streamId,
            @Bind("eventId") String eventId,
            @Bind("data") byte[] data,
            @Bind("type") String type,
            @Bind("version") int version);

    @SqlUpdate("update streams set version = :version where id = :streamId")
    void setCurrentStreamVersion(@Bind("streamId") String streamId, @Bind("version") int version);
}
