geonames-h2
===========

Build a H2 database with the GeoNames data.

## General

The source data can be downloaded from the [GeoNames] (http://download.geonames.org/export/dump/) web site as zipped CSV files.
The fetched data is unzipped and the text files stored in the `./download` directory. The H2 database is created under the local directory `./db`.
The data structure `geonames-h2.tables/table-specs` is a map with the processed data files and the information for creating the database table
for storing the data. Table indexes are also created for more efficient querying.


## Usage

The import can simply be started from the REPL with following steps:

```
> lein repl
> in-ns 'geonames-h2.core
> (create-geonames-db)
```

This will execute the download and import of all the files specified in the `table-specs` structure.


For checking the contents of the H2 DB, the integrated web console can be started directly from the REPL:

Starting:

```
> (start-console)
```

Stopping:

```
> (stop-console)
```
