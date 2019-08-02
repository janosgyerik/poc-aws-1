#!/usr/bin/env python

import logging
import os

import boto3
from botocore.exceptions import ClientError

import json
import uuid
import datetime
import sys


def timestamp():
    return datetime.datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%S.%f')[:-3] + 'Z'


def send_sqs_message(sqs_queue_url, msg_body):
    """
    :param sqs_queue_url: String URL of existing SQS queue
    :param msg_body: String message body
    :return: Dictionary containing information about the sent message. If
        error, returns None.
    """

    sqs_client = boto3.client('sqs')
    try:
        msg = sqs_client.send_message(QueueUrl=sqs_queue_url,
                                      MessageBody=msg_body)
    except ClientError as e:
        logging.error(e)
        return None
    return msg


def main():
    sqs_queue_url = os.environ.get('SQS_QUEUE_URL')
    if not sqs_queue_url:
        print('Error: SQS_QUEUE_URL environment variable not set. Set it to the URL of the queue.')
        sys.exit(1)

    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    pr_opened = {
            'cuuid': str(uuid.uuid4()),
            'date': timestamp(),
            'type': 'pull-request-opened',
            'alm': 'github',
            'repository': {
                'full_name': 'janosgyerik/upvotejs',
                'id': '10805910',
                },
            'pull_request': {
                'key': '1',
                'title': 'Foo PR #1',
                'url': 'https://github.com/janosgyerik/upvotejs/pull/1',
                'type': 'internal',
                'base': {
                    'branch': 'master',
                    'label': 'master',
                    },
                'head': {
                    'branch': 'feature/foo',
                    'label': 'feature/foo',
                    },
                },
            }

    msg_body = json.dumps(pr_opened)
    msg = send_sqs_message(sqs_queue_url, msg_body)
    if msg is not None:
        logging.info(f'Sent SQS message ID: {msg["MessageId"]}')

if __name__ == '__main__':
    main()
