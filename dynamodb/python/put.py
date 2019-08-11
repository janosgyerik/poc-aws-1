#!/usr/bin/env python3
import json
import logging
import os
import sys
import uuid
from argparse import ArgumentParser

import boto3


def put_item(table, item):
    logging.info(f"attempting to send {json.dumps(item, indent=2)}")
    return table.put_item(Item=item)


def main():
    parser = ArgumentParser()
    parser.add_argument('-j', '--json', help="JSON file to send", required=True)
    args = parser.parse_args()

    table_name = os.environ.get('TABLE_NAME')
    if not table_name:
        print('Error: TABLE_NAME environment variable not set. Set it to the name of the DynamoDB table.')
        sys.exit(1)

    logging.basicConfig(level=logging.INFO,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    table = boto3.resource('dynamodb').Table(table_name)
    with open(args.json) as fh:
        item = json.load(fh)

    pk_attr_name = 'analysis_id'
    if pk_attr_name not in item:
        item[pk_attr_name] = str(uuid.uuid4())

    put_item(table, item)


if __name__ == '__main__':
    main()
