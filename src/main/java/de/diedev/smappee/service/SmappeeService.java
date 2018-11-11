package de.diedev.smappee.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.diedev.smappee.client.SmappeeClient;
import de.diedev.smappee.repository.MeasurementRepository;

@SuppressWarnings("unused")
@Service
public class SmappeeService {

	private final static Logger logger = LoggerFactory.getLogger(SmappeeService.class);

	@Autowired
	private SmappeeClient client;

	@Autowired
	private MeasurementRepository somethingRepository;

	@PostConstruct
	public void init() {
		logger.info("Service init");
	}
}
