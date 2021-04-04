package com.cqrs.eventbus.rabbitmq;

public class RabbitMqConfig {

    private final String host;
    private final int port;
    private final String vhost;

    public RabbitMqConfig(String host, int port, String vhost) {
        this.host = host;
        this.port = port;
        this.vhost = vhost;
    }

    public RabbitMqConfig(String host, int port) {
        this(host, port, "/");
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getVhost() {
        return vhost;
    }
}
