geonames-h2
===========

Build a H2 database with the GeoNames information.

## General

The source data can be downloaded from the [GeoNames] (http://www.geonames.org) web site from the
[download] (http://download.geonames.org/export/dump/) area.
During processing the fetched data is automatically unzipped, if existing in an archive, and the text
files finally stored in the local directory `./download`. The H2 database is created in the project root directory.

The data structure `geonames-h2.tables/table-specs` is a list of configuration maps, holding the information of the source data
and the definition of the database table (with indexes) for storing the data.


## Usage

The import can simply be started from the REPL with following steps:

```
> lein repl
user=> (use 'geonames-h2.core)
user=> (in-ns 'geonames-h2.core)
geonames-h2.core=> (create-geonames-db)
```

This will execute the download and import of all the files specified in the `table-specs` structure.

It is possible to selectively import specific tables from the existing configuration. For this the `create-geonames-db` function can be called with the desired table keywords. <br/>
Valid keywords are currently: <br/>
```
:geonames, :alternateNames, :hierarchy, :cities5000, :cities1000, :cities15000, :admin2Codes, :admin1CodesAscii
```

Example for importing only the _cities1000_ and _admin2Codes_ data sets:

```
geonames-h2.core=> (create-geonames-db :cities1000 :admin2Codes)
```

__Note__:
 * The database will automatically be created and should not exist when running the import.
 * The import into the H2 database takes quite long (about 2 hours on my machine) and the size of final database with all tables is about 15 GB!

## Web Console

For checking the contents of the H2 DB, the integrated web console can be started directly from the REPL:

Starting:

```
geonames-h2.core=> (start-console)
```

After starting the console, the web browser should automatically open and access the H2 web console page under `http://localhost:8082`.
The generated database is accessible by using the JDBC URL `jdbc:h2:./geonames`.

Stopping:

```
geonames-h2.core=> (stop-console)
```
