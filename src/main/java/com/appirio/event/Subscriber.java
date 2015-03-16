package com.appirio.event;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
//import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.List;

/**
 * Created by thabo on 3/12/15.
 */
public class Subscriber {
    final Integer LONG_POLL_WAIT_TIME = 20;

    //private AmazonSQSAsyncClient sqsClient = new AmazonSQSAsyncClient(new ProfileCredentialsProvider());
    private AmazonSQSClient sqs = new AmazonSQSClient(new ProfileCredentialsProvider());
    private AmazonSNSClient sns = new AmazonSNSClient(new ProfileCredentialsProvider());
    private String topicArn;
    private String queueUrl;

    private Topic topic;

    public Subscriber(String subscriptionTopic, String subscriber) {
        sqs.setRegion(Configuration.REGION);

//        AsyncHandler asyncHandler = new AsyncHandler() {
//            @Override
//            public void handleResponse(Response res) {
//                System.out.println(res.get().toString())
//            }
//        };
//        sqsClient.createQueueAsync((CreateQueueRequest req, CreateQueueResult res) => {

        topic = new Topic(subscriptionTopic);

        CreateQueueRequest createQueueRequest = new CreateQueueRequest(subscriptionTopic + "-" + subscriber);
        queueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

        String topicArn = topic.getArn();
        sns.subscribe(topicArn, "sqs", topicArn.replaceAll("sns", "sqs") + "-" + subscriber);
    }

    public List<Message> getMessages() {
        ReceiveMessageRequest rmr = new ReceiveMessageRequest();
        rmr.setQueueUrl(queueUrl);
        rmr.setWaitTimeSeconds(LONG_POLL_WAIT_TIME);
        final ReceiveMessageResult result = sqs.receiveMessage(rmr);
        return result.getMessages();
    }
}
