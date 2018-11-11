## Spring Boot Integration with Smappee and MQTT

### What does this app do?
It uses oauth2 to access the functionality of the [Smappee API](https://smappee.atlassian.net/wiki/spaces/DEVAPI/overview) via its own controller ([SmappeeController.java](https://github.com/Theoklitos/smappee-service/blob/master/src/main/java/de/diedev/smappee/controller/SmappeeController.java)). It also subscribes to an MQTT broker and consumes new messages by writing them into a database.

[More information on MQTT messaging, with examples.](https://www.hivemq.com/blog/how-to-get-started-with-mqtt)

This project is intended as an example, since it does not useful on its own.

### How to start
* Configure your database, MQTT broker and Smappee oauth2 credentials in a new  applications.properties file. Use the applications.properties.example file as an example.
* Create a fat jar with `./gradlew bootJar`
* Execute that jar with `java -jar smappee-service-X.X.jar`

### Usage
* Navigate to `root-url/swagger-ui.html#/smappee-controller` to access for smappee's functionality. That controller implements 5 methods from the Smappee API.
* Monitor the database table (by default "t1") where all incoming MQTT messages should end up.

### Improvements
* More functionality from Smappee API can be implemented in the [SmappeeClient.java](https://github.com/Theoklitos/smappee-service/blob/master/src/main/java/de/diedev/smappee/client/SmappeeClient.java) and [SmappeeController.java](https://github.com/Theoklitos/smappee-service/blob/master/src/main/java/de/diedev/smappee/controller/SmappeeController.java) classes. Existing methods can be used as examples.
* The MQTT messaging stuff is inside the [MqttSubscriptionService.java](https://github.com/Theoklitos/smappee-service/blob/master/src/main/java/de/diedev/smappee/messaging/MqttSubscriberService.java) where new messages are consumed asynchronously via the messageArrived() method. There also exists a method to send messages to the same topic, to be used as an example.

### Issues
* Not much in terms of exception handling.
* I am not sure if the Oauth2 token will auto-refresh. However, the code to refresh is already implemented.
* Each message is written to the database via one transaction. Maybe it should be done in batches?
