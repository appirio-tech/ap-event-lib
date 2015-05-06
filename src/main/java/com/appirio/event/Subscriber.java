package com.appirio.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

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
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by thabo on 3/12/15.
 *
 * Sets up a subscription to a topic. Each application should be one unique subscriber.
 */
public class Subscriber {
    final Integer LONG_POLL_WAIT_TIME = 20;

    private AmazonSQSClient sqs = new AmazonSQSClient(new ProfileCredentialsProvider());
    private AmazonSNSClient sns = new AmazonSNSClient(new ProfileCredentialsProvider());
    private String queueUrl;
    private ObjectMapper mapper = new ObjectMapper();

    private Topic topic;

    public Subscriber(String subscriptionTopic, String subscriber) throws JsonParseException, JsonMappingException, IOException {
        sqs.setRegion(Configuration.REGION);

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

    public List<Message> getMessages() {
        ReceiveMessageRequest rmr = new ReceiveMessageRequest();
        rmr.setQueueUrl(queueUrl);
        rmr.setWaitTimeSeconds(LONG_POLL_WAIT_TIME);
        final ReceiveMessageResult result = sqs.receiveMessage(rmr);
        return result.getMessages();
    }

    public Object[] getItems(Class type) throws AggregateIOException {
        List<Message> messages = getMessages();
        Object[] items = new Object[messages.size()];

        List<IOException> deserializationErrors = Collections.synchronizedList(new ArrayList<IOException>());
        List<DeleteMessageBatchRequestEntry> receivedItems = new ArrayList<DeleteMessageBatchRequestEntry>();
        IntStream.range(0, items.length)
            .parallel()
            .forEach(i -> {
                Message message = messages.get(i);
                String messageBody = message.getBody();
                try {
                    items[i] = mapper.readValue(messageBody, type);
                } catch (IOException e) {
                    synchronized (deserializationErrors) {
                        deserializationErrors.add(e);
                    }
                }

                // TODO decide if we persist items on the queue that fail to deserialize
                receivedItems.add(new DeleteMessageBatchRequestEntry(message.getMessageId(), message.getReceiptHandle()));
            });

        if (receivedItems.size() > 0)
            sqs.deleteMessageBatch(queueUrl, receivedItems);

        if (deserializationErrors.size() > 0)
            throw new AggregateIOException(deserializationErrors);

        return  items;
    }
}
