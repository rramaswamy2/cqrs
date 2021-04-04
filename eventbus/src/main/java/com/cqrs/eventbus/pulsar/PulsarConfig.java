package com.cqrs.eventbus.pulsar;

public class PulsarConfig {
	private String serviceUrl;
	private String subscriptionType;
	
	public PulsarConfig(String serviceUrl, String subscriptionType) {
		this.serviceUrl = serviceUrl;
		this.subscriptionType = subscriptionType;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

}