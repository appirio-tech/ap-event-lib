package com.appirio.event;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.SQSActions;
import com.amazonaws.auth.policy.conditions.ConditionFactory;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sqs.AmazonSQSClient;
//import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//import javax.xml.ws.AsyncHandler;
//import javax.xml.ws.Response;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * Created by thabo on 3/12/15.
 */
//public class Subscriber<T> {
public class Subscriber {
    final Integer LONG_POLL_WAIT_TIME = 20;

    //private AmazonSQSAsyncClient sqsClient = new AmazonSQSAsyncClient(new ProfileCredentialsProvider());
    private AmazonSQSClient sqs = new AmazonSQSClient(new ProfileCredentialsProvider());
    private AmazonSNSClient sns = new AmazonSNSClient(new ProfileCredentialsProvider());
    private String queueUrl;
    private ObjectMapper mapper = new ObjectMapper();

    private Topic topic;

    public Subscriber(String subscriptionTopic, String subscriber) throws JsonParseException, JsonMappingException, IOException {
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
        String queueArn = getQueueArn(queueUrl);
        AllowSNStoPublishToSQS(topicArn, queueArn);
        SubscribeResult subscribeResult = sns.subscribe(topicArn, "sqs", queueArn);

        // this causes the json that is published to be delivered as raw json to the endpoint
        // without this the json that is published is encoded within the json and message metadata can be set
        String subscriptionArn = subscribeResult.getSubscriptionArn();
        sns.setSubscriptionAttributes(subscriptionArn, "RawMessageDelivery", "True");
    }

    private void AllowSNStoPublishToSQS(String topicArn, String queueArn) {
        Statement statement = new Statement(Statement.Effect.Allow)
            .withActions(SQSActions.SendMessage)
            .withPrincipals(new Principal("*"))
            .withConditions(ConditionFactory.newSourceArnCondition(topicArn))
            .withResources(new Resource(queueArn));

        Policy policy = new Policy("SubscriptionPermission")
            .withStatements(statement);

        HashMap<String, String> attributes = new HashMap<String, String>();
        attributes.put("Policy", policy.toJson());
        SetQueueAttributesRequest request = new SetQueueAttributesRequest(queueUrl, attributes);
        sqs.setQueueAttributes(request);
    }

    private String getQueueArn(String queueUrl) {
        List<String> attributeTypes = new ArrayList<String>();
        attributeTypes.add("QueueArn");
        attributeTypes.add("Policy");
        GetQueueAttributesResult attributesResult = sqs.getQueueAttributes(queueUrl, attributeTypes);
        return attributesResult.getAttributes().get("QueueArn");
    }

    private List<Message> getMessages() {
        ReceiveMessageRequest rmr = new ReceiveMessageRequest();
        rmr.setQueueUrl(queueUrl);
        rmr.setWaitTimeSeconds(LONG_POLL_WAIT_TIME);
        final ReceiveMessageResult result = sqs.receiveMessage(rmr);
        return result.getMessages();
    }

//    public T[] getItems(Class<T> type) throws IOException {
//        List<Message> messages = getMessages();
//        final T[] items = (T[]) Array.newInstance(type, messages.size());
//        for (int i = 0; i < messages.size(); i++) {
//            items[i] = (T) mapper.readValue(messages.get(i).getBody(), type);
//        }
//        return  items;
//    }

    public Object[] getItems(Class type) throws IOException {
        List<Message> messages = getMessages();
        Object[] items = new Object[messages.size()];
        for (int i = 0; i < messages.size(); i++) {
            items[i] = mapper.readValue(messages.get(i).getBody(), type);
        }
        return  items;
    }
}
