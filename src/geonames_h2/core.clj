(ns geonames-h2.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.java.jdbc :as jdbc]
            [hikari-cp.core :as hik]
            [clojure.set :as set]
            [taoensso.timbre :as log]
            [geonames-h2.tables :as tables]
            [geonames-h2.util :as util])
  (:import [java.io File]
           [java.net URL]
           [org.h2.tools Server]
           [clojure.lang ExceptionInfo]))

(def datasource-options {:adapter  "h2"
                         :url      "jdbc:h2:./geonames"
                         :username "sa"
                         :password ""
                         ;; more Hikari CP parameters
                         ;; :maximal-pool-size 10
                         ;; :auto-commit true
                         ;; :connection-timeout 30000
                         ;; :idle-timeout 1800000
                         ;; ...
                         })

(def h2-console (atom nil))

(defn config-logging! []
  (log/merge-config! {:level :info}))

(defn register-uncaught-exception-handler!
  "Register a handler for the uncaught exceptions, thrown e.g. in background threads (pmap)."
  []
  (Thread/setDefaultUncaughtExceptionHandler
    (reify Thread$UncaughtExceptionHandler
      (uncaughtException [_ thread ex]
        (log/errorf ex "Uncaught exception on %s" (.getName thread))))))

(defn create-table [datasource {:keys [table columns]}]
  (log/infof "Create table `%s`" (name table))
  (try
    (jdbc/db-do-commands {:datasource datasource}
                         (jdbc/create-table-ddl table columns))
    (catch Exception ex
      (throw (ex-info "SQL table cannot be created" {:reason (.getMessage ex)
                                                     :table  table})))))

(defn create-table-indexes [datasource {:keys [table indexes]}]
  (log/infof "Create indexes on table `%s`" (name table))
  (try
    (jdbc/with-db-connection [conn {:datasource datasource}]
                             (doseq [[idx-name idx-cols] indexes]
                               (when (seq indexes)
                                 (log/debugf "CREATE index %s ON %s (%s)"
                                             (name idx-name)
                                             (name table)
                                             (->> idx-cols
                                                  (map name)
                                                  (interpose ",")
                                                  (apply str)))
                                 (jdbc/execute! conn
                                                [(format "CREATE index %s ON %s (%s)"
                                                         (name idx-name)
                                                         (name table)
                                                         (->> idx-cols
                                                              (map name)
                                                              (interpose ",")
                                                              (apply str)))]))))
    (catch Exception ex
      (throw (ex-info "Table index cannot be created" {:reason (.getMessage ex)
                                                       :table  table})))))

(defn clean-col-val
  "Trim column string and replace all blank values with nil."
  [^String s]
  (when-not (string/blank? s) (string/trim s)))

(defn csv-row-values
  "Split a CSV row into a sequence of column values."
  [^String s]
  (when s
    (map clean-col-val (string/split s #"\t"))))

(defn import-data [datasource {:keys [table columns]} ^File import-file]
  (log/infof "Import data from file `%s`" import-file)
  (if (.exists import-file)
    (with-open [rdr (io/reader import-file)]
      (jdbc/with-db-connection [conn {:datasource datasource}]
                               (doall
                                 (pmap (fn [row]
                                         (let [data (zipmap (map first columns)
                                                            (csv-row-values row))]
                                           (try
                                             (jdbc/insert! conn table data)
                                             (catch Exception ex
                                               (log/errorf ex "JDBC insert failed")))))
                                       (line-seq rdr))))
      :OK)
    (throw (ex-info "Import data file not found" {:table table :message (str "Missing import file: " import-file)}))))


(defn perform-table-import
  "Create database table and import the geonames."
  [datasource table-spec ^File import-file]
  (create-table datasource table-spec)
  (create-table-indexes datasource table-spec)
  (import-data datasource table-spec import-file))

(defn import-all [datasource table-specs]
  (doseq [ts table-specs]
    (let [table-name (name (:table ts))
          file (:file ts)
          url (URL. (:url ts))]
      (try
        (log/infof "Download `%s` from `%s`" file url)
        (let [import-file (util/download-geonames-file url (util/make-local-directory "download") file)]
          (log/infof "Import `%s` into table `%s`" import-file table-name)
          (perform-table-import datasource ts import-file)
          (log/infof "Completed import for table `%s`" table-name)
          :OK)
        (catch ExceptionInfo ex
          (let [{:keys [table message]} (ex-data ex)]
            (log/errorf ex "Exception processing table `%s`: %s" (name table) message)))
        (catch Exception ex
          (log/error ex (.getMessage ex)))))))

(defn filter-table-specs
  "Filter the list of table specification maps by the sequence of
  table keywords."
  [table-specs selected-tables]
  (let [tset (into #{} selected-tables)]
    (filter (fn [{:keys [table]}] (tset table)) table-specs)))

(defn existing-tables []
  (into #{} (map :table tables/table-specs)))

(defn check-table-parameters [tables]
  (let [existing (existing-tables)
        non-existing (set/difference (into #{} tables) existing)]
    (when-not (empty? non-existing)
      (log/errorf "Non-existing tables specified: %s" (apply str (interpose ", " non-existing)))
      (log/infof "Valid values: %s" (apply str (interpose ", " existing)))
      (log/infof "Aborting")
      (System/exit 1))))

;;
;;
;;

(defn create-geonames-db
  ([]
    (apply create-geonames-db (existing-tables)))
  ([& tables]
   (let [datasource (hik/make-datasource datasource-options)]
     (try
       (config-logging!)
       (register-uncaught-exception-handler!)
       (check-table-parameters tables)
       (import-all datasource (filter-table-specs tables/table-specs tables))
       (finally
         (hik/close-datasource datasource))))))

(defn start-console
  "Start the H2 database console with default parameters (http://localhost:8082)."
  []
  (when-not @h2-console
    (reset! h2-console (.start (Server/createWebServer (into-array String ["-baseDir" (System/getProperty "user.dir")]))))
    (Server/openBrowser "http://localhost:8082")
    (log/infof "Enter in the `JDBC URL` field: %s" (:url datasource-options))))

(defn stop-console
  "Stop running H2 console"
  []
  (when @h2-console
    (reset! h2-console (.stop @h2-console))))
