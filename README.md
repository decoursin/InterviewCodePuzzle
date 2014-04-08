# Build a Web API

## 
A Runa customer, AwesomeShoes.com, needs a new report. They've given us their session data, which is a pipe delimited formatted file. Runa also has data stored in a CSV formatted file.  The report should compare the data in the 2 files, and builds a report which shows the discrepancies.

## Requirements
* You need to create a web service, which has a single route, which generates a report as described in the [expected api response](https://github.com/StaplesLabs/code-puzzle/blob/master/expected-api-responses.json).

## API route:
* /runatic/report - route which returns the report data. It takes a parameter `order_by`, which can be any of these 3 values
  * `session-type-desc`         - sort by `session-type` in descending order
  * `order-id-asc`              - sort by `order-id` in ascending order
  * `unit-price-dollars-asc`    - sort by `unit-price-dollars` in ascending order<br />
[This](https://github.com/StaplesLabs/code-puzzle/blob/master/expected-api-responses.json) file has the above 3 use cases.

## Caveats:
* DO NOT use a library to parse any file. Use only clojure.core functions. (You CANNOT use String methods or libraries like clojure.string, clojure.set to parse the file, but you CAN use java methods to parse numbers etc). Don't worry about the edge cases of parsing the CSV/PSV files. Just do enough to parse the 2 files. For any of the non-parsing parts of the application you are free to use any libraries you want (from Clojars or Maven Central, etc). Email punit@stapleslabs.com if you're not clear about this.
* Notice the order of the columns in the Runa data file, and the Merchant data file are different, and the prices in the Runa data file are in cents, while the prices in the Merchant file are in dollars.

## Inputs
  * Files located in the resources folder
    * [Merchant Data File](https://github.com/StaplesLabs/code-puzzle/blob/master/resources/merchant_data.psv) is the data from AwesomeShoes.com          
    * [Runa Data File](https://github.com/StaplesLabs/code-puzzle/blob/master/resources/runa_data.csv) is the data stored in Runa's database

## Outputs
Look at this file - [expected api response](https://github.com/StaplesLabs/code-puzzle/blob/master/expected-api-responses.json)

## Solution Submission
Fork the project, and send an email to devs@stapleslabs.com once you're done.<br />
There are no points given for speed, so please take as much time as you desire,<br />
so that you can submit your best work.<br />
Please include instructions for how to run your application and any tests.

Have fun! :)
