#!/usr/bin/env bash

echo -n "- Set permissions"
chown -R order:order /opt/omnius/order
chown -R order:order /etc/omnius/order
chown -R order:order /var/log/omnius/order

chmod 755 /opt/omnius/order
chmod 755 /etc/omnius/order
chmod 755 /var/log/omnius/order

chmod 644 /opt/omnius/order/*
chmod 644 /etc/omnius/order/*

echo "... Done"

echo -n "- Set permissions stickybit"
chmod +s /opt/omnius/order
chmod +s /etc/omnius/order
chmod +s /var/log/omnius/order

echo "... Done"

echo -n "- Symlink configs"
ln -s /etc/omnius/order/api.properties /opt/omnius/order/api.properties
ln -s /etc/omnius/order/api-log4j2.xml /opt/omnius/order/api-log4j2.xml
ln -s /etc/omnius/order/projections.properties /opt/omnius/order/projections.properties
ln -s /etc/omnius/order/projections-log4j2.xml /opt/omnius/order/projections-log4j2.xml
echo "... Done"
