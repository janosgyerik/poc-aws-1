#!/usr/bin/env python3
import json
import logging
import os
import sys
from argparse import ArgumentParser
from decimal import Decimal

import boto3


class DecimalEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, Decimal):
            return float(o)
        return super(DecimalEncoder, self).default(o)


def list_items(table):
    response = table.scan(ConsistentRead=True)

    for item in response['Items']:
        logging.info(json.dumps(item, indent=2, cls=DecimalEncoder))


def main():
    parser = ArgumentParser()
    parser.parse_args()

    table_name = os.environ.get('TABLE_NAME')
    if not table_name:
        print('Error: TABLE_NAME environment variable not set. Set it to the name of the DynamoDB table.')
        sys.exit(1)

    logging.basicConfig(level=logging.INFO,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    table = boto3.resource('dynamodb').Table(table_name)
    list_items(table)


if __name__ == '__main__':
    main()
