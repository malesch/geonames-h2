(defproject geonames-h2 "1.0.0"
  :description "Load GeoNames data (geonames.org) into a H2 database."
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.h2database/h2 "1.4.191"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [hikari-cp "1.7.2"]
                 [com.taoensso/timbre "4.5.1"]])
