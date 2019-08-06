#!/usr/bin/env python3

import logging
import os

import boto3
from botocore.exceptions import ClientError

import json
import sys


def retrieve_sqs_messages(sqs_queue_url, num_msgs=1, wait_time=0, visibility_time=5):
    """Retrieve messages from an SQS queue

    The retrieved messages are not deleted from the queue.

    :param sqs_queue_url: String URL of existing SQS queue
    :param num_msgs: Number of messages to retrieve (1-10)
    :param wait_time: Number of seconds to wait if no messages in queue
    :param visibility_time: Number of seconds to make retrieved messages
        hidden from subsequent retrieval requests
    :return: List of retrieved messages. If no messages are available, returned
        list is empty. If error, returns None.
    """

    # Validate number of messages to retrieve
    if num_msgs < 1:
        num_msgs = 1
    elif num_msgs > 10:
        num_msgs = 10

    sqs_client = boto3.client('sqs')
    try:
        msgs = sqs_client.receive_message(QueueUrl=sqs_queue_url,
                                          MessageAttributeNames=['event_type'],
                                          MaxNumberOfMessages=num_msgs,
                                          WaitTimeSeconds=wait_time,
                                          VisibilityTimeout=visibility_time)
    except ClientError as e:
        logging.error(e)
        return None

    return msgs['Messages'] if 'Messages' in msgs else None


def delete_sqs_message(sqs_queue_url, msg_receipt_handle):
    """Delete a message from an SQS queue

    :param sqs_queue_url: String URL of existing SQS queue
    :param msg_receipt_handle: Receipt handle value of retrieved message
    """

    # Delete the message from the SQS queue
    sqs_client = boto3.client('sqs')
    sqs_client.delete_message(QueueUrl=sqs_queue_url,
                              ReceiptHandle=msg_receipt_handle)


def main():
    sqs_queue_url = os.environ.get('SQS_QUEUE_URL')
    if not sqs_queue_url:
        print('Error: SQS_QUEUE_URL environment variable not set. Set it to the URL of the queue.')
        sys.exit(1)

    num_messages = 2

    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    msgs = retrieve_sqs_messages(sqs_queue_url, num_messages)
    if msgs is not None:
        for msg in msgs:
            logging.info(f'SQS: Message ID: {msg["MessageId"]}, '
                         f'Contents: {msg["Body"]}')

            if 'MessageAttributes' not in msg:
                print('Warning: MessageAttributes missing in the message (deleting anyway)')
            elif 'event_type' not in msg["MessageAttributes"]:
                print('Warning: event_type message attribute missing (invalid pull-request-opened event) (deleting anyway)')
            elif msg["MessageAttributes"]['event_type']['StringValue'] != 'pull-request-opened':
                print('Warning: event_type is not pull-request-opened (deleting anyway)')
            else:
                print(json.dumps(json.loads(msg["Body"]), indent=2))

            # Remove the message from the queue
            delete_sqs_message(sqs_queue_url, msg['ReceiptHandle'])


if __name__ == '__main__':
    main()
