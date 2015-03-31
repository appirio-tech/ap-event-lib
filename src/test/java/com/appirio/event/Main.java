package com.appirio.event;

import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

//import com.appirio.event.Publisher;
/**
 * Created by thabo on 3/12/15.
 */
public class Main {
    public static void main(String[] args) throws IOException, AggregateIOException {
        //RunTest();
        Subscriber es = new Subscriber("MemberCertificationRegistration", "SampleSubscriber");

        System.out.println("Subscribed, waiting for events. Press any key to quit.");
        while(System.in.available() == 0) {
            try {
                Object[] items = es.getItems(RegistrationEventModel.class);
                //System.out.println("Events received:");
                for (int i = 0; i < items.length; i++) {
                    PrintEvent((RegistrationEventModel) items[i]);
                }

            } catch (AggregateIOException e) {
                List<IOException> inner = e.getInternalExceptions();
                for (int i = 0; i < inner.size(); i++) {
                    System.out.println("Unable to deserialize object on queue:");
                    System.out.println(inner.get(i));
                }
            }
        }
        System.out.println("Bye...");
    }

    private static void PrintEvent(RegistrationEventModel regEvent) {
        System.out.println(regEvent.getHandle() + " registered for event " + regEvent.getProgramId() + " on " + regEvent.getTimestamp());
    }

    private static void RunTest() throws IOException, AggregateIOException {
        Calendar start = Calendar.getInstance();

        Publisher foo = new Publisher("EventTopic1");
        Calendar pubcreated = Calendar.getInstance();

        Subscriber es = new Subscriber("EventTopic1", "TestClient3");
        Calendar subcreated = Calendar.getInstance();

        TestModel testObj = new TestModel("Hello World");
        foo.Publish(testObj);
        Calendar published = Calendar.getInstance();

        //TestModel[] received = (TestModel[]) es.getItems(TestModel.class);
        Object[] received = es.getItems(TestModel.class);

        Calendar finished = Calendar.getInstance();

        assert received.length == 1;
        TestModel testReceived = (TestModel) received[0];
        assert testReceived.getTestString() == "Hello World";

        boolean gotExc = false;
        try {
            foo.Publish(new BadModel("Hello Exception Land"));
            Object[] errorExpected = es.getItems(BadModel.class);
        } catch (AggregateIOException exc) {
            gotExc = true;
            assert (exc.getInternalExceptions().size() == 1);
        }
        assert (gotExc);


        System.out.println("Publish: " + (pubcreated.getTimeInMillis() - start.getTimeInMillis()) + " ms");
        System.out.println("Subscribe: " + (subcreated.getTimeInMillis() - pubcreated.getTimeInMillis()) + " ms");
        System.out.println("Publish Event: " + (published.getTimeInMillis() - subcreated.getTimeInMillis()) + " ms");
        System.out.println("Event Received: " + (finished.getTimeInMillis() - published.getTimeInMillis()) + " ms");
    }

}

