Author: Nick DeCoursin

I tried to make this as real-life as possible. For example, I treat the data as if it would change, grow, etc. By doing so, I think the program would continue to work even if:
    rename columns (restricted to certain name conventions like dollars and cents)
    move columns around
    add columns
    add/delete rows
    etc.
As a byproduct of this, sorting on all existing columns works successfully, and it should continue to work if columns are renamed/moved/added. Also, functions are treated as if they would/could be used later on, as the code changes.

I didn't abstract-out the files themselves, so it wouldn't work to add more csv or psv files.

Some gotchyas:
- Programatically, the delimeter of the csv/psv files are determined by their file extension, which is sufficient for this, but in other scenarios the delimiter would be determined by parsing the files itself.

High level throughput
handler       -> make-report    -> make-dataset
(handler.clj)    (reporter.clj) <- (parser.clj)
                                -> format-dataset
              <-                <- (formatter.clj)

General idea
The program uses Incanter. Immediately, the parsed data is put into a Incanter dataset. Basically, since the data is tabular, I can just put it in there, and then perform operations on it via the Incanter api. Unfortunately, though, the api is lacking many useful functions, which I had to write my own (discussed below.) Another nice benefit of using Incanter's dataset, is that there's several functions (specifically to-list, to-map, to-matrix, to-vect) to instantly transform the dataset into different forms of clojure data.

General flow
make-dataset parses the psv or csv file, converts it into a dataset. format-dataset (called from reporter.clj as shown above) renames columns, converts money columns to floats, etc, and finally sorts the columns according to the argument passed via the url. Next, the dataset is returned to make-report. make-report, then, repeats this operation for the second dataset. Now with both datasets, it combines them into the format described in the expected-api-response.json. Finally, this is returned to handler, which converts the clojure data to json and sends the response.

Testing.
How: lein test
There's a few functional tests and no other tests. The functional tests use fixtures to start and hit the webserver getting json in return, which is parsed into clojure data formulating the actual result. I created a few json files, one for each test, which have the expected results. The tests compare these two results failing if they're not equal according to the clojure.core/= function.

New Incanter functions
There's a few functions that I wrote which I think deserve to be in the Incanter api: sort-rows, sum-columns, build-cns-cell-map, cns-cell-map-to-dataset. These last couple might be ify, but seem useful to me. These are described in more detail by their respective comments and moreso by the code itself. To be used in Incanter, I'll need to generalize them to accept other use cases, which I'd like to do sometime when I have time.

Abbreviations used
data = (usually or always) Incanter Dataset
cn = column-name
cns = column-names
re = regex
merch = the dataset created from the merchant-data
runa  = the dataset created from the runa-data
other abbreviations are pretty typical clojure style or somewhat intuitive
