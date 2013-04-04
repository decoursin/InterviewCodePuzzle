# Build a Web API

## What the Merchant h
A Runa customer, AwesomeShoes.com, needs a new report. They've given us their session data, which is a TSV formatted file. Runa also has data stored in a CSV formatted file. We need a report which compares the data in the 2 files, and builds a report which shows the discrepancies.

## Requirements
* You will be given 2 files, a CSV and a TSV formatted file, each containing multiple rows of session data.

* You need to read and parse the files, and generate a coreport depending on the content type specified in the headers.


## API routes:
* /runatic/report - route which returns the report data

## Caveats:
* DO NOT use a library to parse the CSV file.Use only clojure.core functions(You cant use libraries like clojure.string, clojure.set). However you can use libraries to parse other file formats.

## Inputs
  * Files located in the resources folder
    * merchant_data.tsv is the data from AwesomeShoes.com
    * runa_data.csv is the data stored in Runa's database

## Outputs
