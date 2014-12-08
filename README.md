# Build a Web API

## 
Staples needs a new report. We have access to session data from a 3rd party vendor, which is a pipe delimited formatted file. Staples also has internal data stored in a CSV formatted file.  The web application should compare the data in the 2 files, and build a report which shows the discrepancies.

## Requirements
* You need to create a web service, which has a single route, which generates a report as described in the [expected api response](https://github.com/StaplesLabs/code-puzzle/blob/master/expected-api-responses.json).
* The file structure provided by us was been generated using Leiningen (a Clojure build tool). At SparX, we primarily write our services in Clojure. Generally we prefer that the code puzzle be submitted in Clojure. However, our top priority is that a developer can demonstrate that he or she is skilled in his or her craft. If you believe that you can demonstrate this best by completing the puzzle in a different language, then use that language.

## API route:
* /runatic/report - route which returns the report data. It takes a parameter `order_by`, which can be any of these 3 values
  * `session-type-desc`         - sort by `session-type` in descending order
  * `order-id-asc`              - sort by `order-id` in ascending order
  * `unit-price-dollars-asc`    - sort by `unit-price-dollars` in ascending order<br />
[This](https://github.com/StaplesLabs/code-puzzle/blob/master/expected-api-responses.json) file has the above 3 use cases.

## Caveats:
* DO NOT use a library to parse any file. For example, many there are many libraries available to parse character delimited files. Do no use them. It is perfectly fine to use a libary that parses text into numbers. Or using a regular expression library. However, we would like to see you write the code that parses the actual file. Don't worry about the edge cases of parsing the CSV/PSV files. Just do enough to parse the 2 files. For any of the non-parsing parts of the application you are free to use any libraries you want (from Clojars or Maven Central, etc). Email punit@stapleslabs.com if you're not clear about this.
* It is fine to use a json processing library if you are interested in reading the expeced-api-reponse.json file for any reason.
* Notice the order of the columns in the Staples data file, and the Merchant data file are different, and the prices in the Runa data file are in cents, while the prices in the Merchant file are in dollars.

## Inputs
  * Files located in the resources folder
    * [Merchant Data File](https://github.com/StaplesLabs/InterviewCodePuzzle/blob/master/resources/external_data.psv) is the data from 3rd party vendor.          
    * [Staples Data File](https://github.com/StaplesLabs/InterviewCodePuzzle/blob/master/resources/staples_data.csv) is the data stored in Staples's database

## Outputs
Look at this file - [expected api response](https://github.com/StaplesLabs/InterviewCodePuzzle/blob/master/expected-api-responses.json)

## Solution Submission
Fork the project, and send an email to careers@stapleslabs.com once you're done.<br />
There are no points given for speed, so please take as much time as you desire,<br />
so that you can submit your best work.<br />
Please include instructions for how to run your application and any tests.

Have fun! :)
