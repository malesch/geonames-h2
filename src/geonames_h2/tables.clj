(ns geonames-h2.tables)

(def base-url "http://download.geonames.org/export/dump/")

(def table-specs [{:url     (str base-url "allCountries.zip")
                   :file    "allCountries.txt"
                   :table   :geonames
                   :columns [[:id :int :primary :key]
                             [:name "varchar(200)"]
                             [:asciiname "varchar(200)"]
                             [:alternatenames "varchar(10000)"]
                             [:lat "decimal(10,7)"]
                             [:long "decimal(10,7)"]
                             [:fclass "char(1)"]
                             [:fcode "varchar(10)"]
                             [:country "varchar(2)"]
                             [:cc2 "varchar(256)"]
                             [:admin1 "varchar(20)"]
                             [:admin2 "varchar(80)"]
                             [:admin3 "varchar(20)"]
                             [:admin4 "varchar(20)"]
                             [:population :bigint]
                             [:elevation :int]
                             [:gtopo30 :int]
                             [:timezone "varchar(40)"]
                             [:moddate :date]]
                   :indexes [[:idx_geoname_name [:name]]
                             [:idx_geoname_feature [:fcode]]
                             [:idx_geoname_admin [:country :admin1 :admin2 :admin3 :admin4]]]}
                  {:url     (str base-url "alternateNames.zip")
                   :file    "alternateNames.txt"
                   :table   :alternateNames
                   :columns [[:id :int :primary :key]
                             [:geonameid :int]
                             [:isoLanguage "varchar(7)"]
                             [:alternateName "varchar(200)"]
                             [:isPreferredName :boolean]
                             [:isShortName :boolean]
                             [:isColloquial :boolean]
                             [:isHistoric :boolean]]
                   :indexes [[:idx_altnames_altname [:alternateName]]
                             [:idx_altnames_geonameid [:geonameid]]]}
                  {:url     (str base-url "hierarchy.zip")
                   :file    "hierarchy.txt"
                   :table   :hierarchy
                   :columns [[:parentId :int]
                             [:childId :int]
                             [:relationType "varchar(40)"]]}
                  {:url     (str base-url "admin1CodesASCII.txt")
                   :file    "admin1CodesASCII.txt"
                   :table   :admin1CodesAscii
                   :columns [[:code "varchar(40)"]
                             [:name "varchar(80)"]
                             [:nameAscii "varchar(80)"]
                             [:geonameid :int]]
                   :indexes [[:idx_admin1_name [:name]]
                             [:idx_admi1_geonameid [:geonameid]]]}
                  {:url     (str base-url "admin2Codes.txt")
                   :file    "admin2Codes.txt"
                   :table   :admin2Codes
                   :columns [[:code "varchar(40)"]
                             [:name_local "varchar(80)"]
                             [:name "varchar(80)"]
                             [:geonameid :int]]
                   :indexes [[:idx_admin2_name [:name]]
                             [:idx_admin2_geonameid [:geonameid]]]}
                  {:url     (str base-url "cities1000.zip")
                   :file    "cities1000.txt"
                   :table   :cities1000
                   :columns [[:id :int :primary :key]
                             [:name "varchar(200)"]
                             [:asciiname "varchar(200)"]
                             [:alternatenames "varchar(10000)"]
                             [:lat "decimal(10,7)"]
                             [:long "decimal(10,7)"]
                             [:fclass "char(1)"]
                             [:fcode "varchar(10)"]
                             [:country "varchar(2)"]
                             [:cc2 "varchar(256)"]
                             [:admin1 "varchar(20)"]
                             [:admin2 "varchar(80)"]
                             [:admin3 "varchar(20)"]
                             [:admin4 "varchar(20)"]
                             [:population :bigint]
                             [:elevation :int]
                             [:gtopo30 :int]
                             [:timezone "varchar(40)"]
                             [:moddate :date]]
                   :indexes [[:idx_c1000_name [:name]]
                             [:idx_c1000_feature [:fcode]]
                             [:idx_c1000_admin [:country :admin1 :admin2 :admin3 :admin4]]]}
                  {:url     (str base-url "cities5000.zip")
                   :file    "cities5000.txt"
                   :table   :cities5000
                   :columns [[:id :int :primary :key]
                             [:name "varchar(200)"]
                             [:asciiname "varchar(200)"]
                             [:alternatenames "varchar(10000)"]
                             [:lat "decimal(10,7)"]
                             [:long "decimal(10,7)"]
                             [:fclass "char(1)"]
                             [:fcode "varchar(10)"]
                             [:country "varchar(2)"]
                             [:cc2 "varchar(256)"]
                             [:admin1 "varchar(20)"]
                             [:admin2 "varchar(80)"]
                             [:admin3 "varchar(20)"]
                             [:admin4 "varchar(20)"]
                             [:population :bigint]
                             [:elevation :int]
                             [:gtopo30 :int]
                             [:timezone "varchar(40)"]
                             [:moddate :date]]
                   :indexes [[:idx_c5000_name [:name]]
                             [:idx_c5000_feature [:fcode]]
                             [:idx_c5000_admin [:country :admin1 :admin2 :admin3 :admin4]]]}
                  {:url     (str base-url "cities15000.zip")
                   :file    "cities15000.txt"
                   :table   :cities15000
                   :columns [[:id :int :primary :key]
                             [:name "varchar(200)"]
                             [:asciiname "varchar(200)"]
                             [:alternatenames "varchar(10000)"]
                             [:lat "decimal(10,7)"]
                             [:long "decimal(10,7)"]
                             [:fclass "char(1)"]
                             [:fcode "varchar(10)"]
                             [:country "varchar(2)"]
                             [:cc2 "varchar(256)"]
                             [:admin1 "varchar(20)"]
                             [:admin2 "varchar(80)"]
                             [:admin3 "varchar(20)"]
                             [:admin4 "varchar(20)"]
                             [:population :bigint]
                             [:elevation :int]
                             [:gtopo30 :int]
                             [:timezone "varchar(40)"]
                             [:moddate :date]]
                   :indexes [[:idx_c15000_name [:name]]
                             [:idx_c15000_feature [:fcode]]
                             [:idx_c15000_admin [:country :admin1 :admin2 :admin3 :admin4]]]}])