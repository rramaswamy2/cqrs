package com.cqrs.eventstore.sql;

import org.assertj.db.type.Table;
import org.junit.jupiter.api.Test;

import static org.assertj.db.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WhenCreatingANewEventStoreTest extends SqlEventStoreTest {
    public WhenCreatingANewEventStoreTest() {
        super();

    }

    @Test
    public void itShouldReturnAnEventStore() {
        assertNotNull(store);
    }

    @Test
    public void itShouldHaveCreatedTheEventsTable() {
        Table table = new Table(dataSource, "events");
        assertThat(table).hasNumberOfColumns(5);
        assertThat(table).column(0).hasColumnName("stream_id").isText(true).hasOnlyNotNullValues();
        assertThat(table).column(1).hasColumnName("event_id").isText(true).hasOnlyNotNullValues();
        assertThat(table).column(2).hasColumnName("data").isBytes(true).hasOnlyNotNullValues();
        assertThat(table).column(3).hasColumnName("type").isText(true).hasOnlyNotNullValues();
        assertThat(table).column(4).hasColumnName("version").isNumber(true).hasOnlyNotNullValues();
    }

    @Test
    public void itShouldHaveCreatedTheStreamsTable() {
        Table table = new Table(dataSource, "streams");
        assertThat(table).hasNumberOfColumns(3);
        assertThat(table).column(0).hasColumnName("id").isText(true).hasOnlyNotNullValues();
        assertThat(table).column(1).hasColumnName("type").isText(true).hasOnlyNotNullValues();
        assertThat(table).column(2).hasColumnName("version").isNumber(true).hasOnlyNotNullValues();
    }
}
