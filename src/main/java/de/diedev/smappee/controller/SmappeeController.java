package de.diedev.smappee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.diedev.smappee.client.SmappeeClient;

@RestController
public class SmappeeController {

	@Autowired
	private SmappeeClient client;

	@RequestMapping(value = "/consumption/{serviceLocationId}", method = RequestMethod.GET, produces = "application/json")
	public String getConsumption(@PathVariable(value = "serviceLocationId", required = true) final String serviceLocationId,
			@RequestParam(value = "aggregation", required = true) final int aggregation,
			@RequestParam(value = "from", required = false, defaultValue = "0") final Long from,
			@RequestParam(value = "to", required = false, defaultValue = "2500000000000") final Long to) {
		return client.getConsumtion(serviceLocationId, aggregation, from, to).toString(5);
	}

	@RequestMapping(value = "/costAnalysis/{serviceLocationId}", method = RequestMethod.GET, produces = "application/json")
	public String getCostAnalysis(@PathVariable(value = "serviceLocationId", required = true) final String serviceLocationId,
			@RequestParam(value = "aggregation", required = true) final int aggregation,
			@RequestParam(value = "from", required = false, defaultValue = "0") final Long from,
			@RequestParam(value = "to", required = false, defaultValue = "2500000000000") final Long to) {
		return client.getCostAnalysis(serviceLocationId, aggregation, from, to).toString(5);
	}

	@RequestMapping(value = "/events/{serviceLocationId}", method = RequestMethod.GET, produces = "application/json")
	public String getEvents(@PathVariable(value = "serviceLocationId", required = true) final String serviceLocationId,
			@RequestParam(value = "applianceId", required = false, defaultValue = "0") final int applianceId,
			@RequestParam(value = "from", required = false, defaultValue = "0") final Long from,
			@RequestParam(value = "to", required = false, defaultValue = "2500000000000") final Long to,
			@RequestParam(value = "maxNumber", required = false, defaultValue = "1000000") final Integer maxNumber) {
		return client.getEvents(serviceLocationId, applianceId, from, to, maxNumber).toString(5);
	}

	@RequestMapping(value = "/serviceLocations/{serviceLocationId}", method = RequestMethod.GET)
	public String getServiceLocationInformation(@PathVariable(value = "serviceLocationId", required = true) final String serivceLocationId) {
		return client.getServiceLocationInfo(serivceLocationId).toString(5);
	}

	@RequestMapping(value = "/serviceLocations", method = RequestMethod.GET)
	public String getServiceLocations() {
		return client.getServiceLocations().toString(5);
	}

}
