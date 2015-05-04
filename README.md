# ap-event-lib
Event Framework Library

## Overview
This shared library provides a high-level way to publish events and listen to events. This library attempts to hide the details of the implementation of event transport. There is however some setup that needs to be done to connect endpoints. The shared library assumes this setup has already been completed.

## Publishing
In order to publish events, a microservice will create a SNS topic for each event type that it wants to publish.
> The assumed format for SNS topic names is `<microservice name>-<event type>`. For example, for the app-work-requests topic that sends "timeline" events, the SNS topic name is `app-work-requests-timeline`

### Example Usage
```
// appRequest = AppWorkRequest, pub = EventPublisher

// create the event object
Event event = new Event(appRequest.getCreatedBy(), appRequest.getCreatedBy());
event.setCreatedAt(appRequest.getCreatedAt());
event.setEventType("timeline");
event.setEventSubType("created");
event.setSourceObjectId(appRequest.getId());
event.setSourceObjectType("app-work-requests");
// set write the source object as json
event.setSourceObjectContent(mapper.writeValueAsString(appRequest));

// publish the event
pub.publish(event);
```

## Listening
Each microservice that wants to listen to events will need to create a SQS queue that will collect event messages. The SQS queue is then subscribed to one or more SNS topics of event publishers.

> The assumed format for a microservice's event listening SQS queue is `<microservice name>-events`. For example `app-work-requests-events`

You will need to create a subscription to the SQS queue for each SNS event publisher that you want to listen to. It is important to set the **Raw message delivery** subscription attribute to *True* for each subscription you create.

> While rare, it is possible that an event message is read multiple times. **Listeners should therefore be idempotent and be able to handle the same event being processed multiple times.**

### Example Usage
```
// define an event listener
public class ExamplesEventListener implements EventListener {
    @Override
	public void processEvent(Event event) {
	    // do something with the event...
	}
}

// start up the event listener managed object in the dropwizard application
@Override
public void run(ExamplesServiceConfiguration config, Environment env) throws Exception {
    // ...
    // "examples" here is the microservice name
    env.lifecycle().manage(new ExamplesMonitorManager("examples", new ExamplesEventListener()));
    // ...
}

```
