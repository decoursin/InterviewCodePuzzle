# Build a Web API

## 
A Runa customer, AwesomeShoes.com, needs a new report. They've given us their session data, which is a pipe delimited formatted file. Runa also has data stored in a CSV formatted file. We need a report which compares the data in the 2 files, and builds a report which shows the discrepancies.

## Requirements
* You need to create a web service, which has a single route, which generates a report as described in the `expected-api-responses.txt` file.

## API route:
* /runatic/report - route which returns the report data. It takes a parameter `order_by`, which can be any of these 3 values
  * `session-type-desc`         - sort by `session-type` in descending order
  * `order-id-asc`              - sort by `order-id` in ascending order
  * `unit-price-dollars-asc`    - sort by `unit-price-dollars` in ascending order

## Caveats:
* DO NOT use a library to parse any file. Use only clojure.core functions(You cant use libraries like clojure.string, clojure.set to parse the file, but you can use java methods to parse numbers etc). Email punit@runa.com if you're not clear about this.
* Notice the order of the columns in the Runa data file, and the Merchant data file are different, and the prices in the Runa Data file are in cents, while the prices in the Merchant file are in dollars.

## Inputs
  * Files located in the resources folder
    * merchant_data.tsv is the data from AwesomeShoes.com          
    * runa_data.csv is the data stored in Runa's database

## Outputs
Look at this file - `expected-api-responses.txt`
