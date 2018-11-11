package de.diedev.smappee.messaging;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.diedev.smappee.model.Measurement;
import de.diedev.smappee.repository.MeasurementRepository;

@Service
public class MqttSubscriberService implements MqttCallback {

	private final static Logger logger = LoggerFactory.getLogger(MqttSubscriberService.class);

	private static final int QOS = 1;

	@Value("${smappee.messaging.security.username}")
	private String username;

	@Value("${smappee.messaging.security.password}")
	private String password;

	@Value("${smappee.messaging.topic}")
	private String topic;

	@Value("${smappee.messaging.host}")
	private String host;

	@Autowired
	private MeasurementRepository repository;

	private MqttAsyncClient client;

	@Override
	public void connectionLost(final Throwable cause) {
		logger.error("Connection lost because: " + cause);
		throw new Error(cause);
	}

	@Override
	public void deliveryComplete(final IMqttDeliveryToken token) {
		logger.info("Delivery complete.");
		logger.info(token.toString());
	}

	private MqttSubscriberService getReference() {
		return this;
	}

	@PostConstruct
	private void init() throws MqttException {
		final MqttConnectOptions conOpt = new MqttConnectOptions();
		conOpt.setCleanSession(true);
		// conOpt.setUserName(username); // TODO enable this if you have access control on your brocker
		// conOpt.setPassword(password.toCharArray());

		logger.info("Subscribing to topic " + topic + " at " + host);
		client = new MqttAsyncClient(host, "spring-boot-" + System.currentTimeMillis(), new MemoryPersistence());
		client.setCallback(this);
		client.connect(null, new IMqttActionListener() {

			@Override
			public void onFailure(final IMqttToken asyncActionToken, final Throwable exception) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(final IMqttToken asyncActionToken) {
				try {
					client.subscribe(topic, QOS);
				} catch (final MqttException e) {
					logger.error("Could not subscribe: " + e);
					e.printStackTrace();
				}
				client.setCallback(getReference());
				logger.info("Subscribed.");
			}
		});
	}

	@Override
	public void messageArrived(final String topic, final MqttMessage message) throws MqttException {
		if (message.isDuplicate()) {
			return;
		}

		try {
			final String rawMessage = new String(message.getPayload());
			final JSONObject jsonMessage = new JSONObject(rawMessage);
			final int power = jsonMessage.getInt("totalPower");
			final long utcTimestamp = jsonMessage.getLong("utcTimeStamp");
			final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(utcTimestamp), ZoneId.of("UTC"));
			final Measurement measurement = new Measurement(zonedDateTime, power);
			persistMeasurement(measurement);
		} catch (final JSONException e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public void persistMeasurement(final Measurement measurement) {
		repository.save(measurement);
	}

	public void sendMessage(final String payload) throws MqttException {
		final MqttMessage message = new MqttMessage(payload.getBytes());
		message.setQos(QOS);
		client.publish(topic, message);
	}

}
