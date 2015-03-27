package com.appirio.event;

import java.io.IOException;
import java.util.Calendar;

//import com.appirio.event.Publisher;
/**
 * Created by thabo on 3/12/15.
 */
public class Main {
    public static void main(String[] args) throws IOException, AggregateIOException {

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

