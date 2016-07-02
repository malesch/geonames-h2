(ns geonames-h2.util
  (:require [clojure.java.io :as io]
            [clojure.string :as string])
  (:import [java.io File]
           [java.net URL]
           [java.util.zip ZipFile]))

(defn is-zip?
  "Return true if the file-path is a zip file (has the `zip` extension)."
  [file-path]
  (when file-path
    (-> file-path
        (string/lower-case)
        (string/ends-with? ".zip"))))

(defn rename-ext
  "Rename the file extension from the given file path string."
  [file-path ext]
  (when file-path
    (let [pos (or (string/last-index-of file-path ".")
                  (count file-path))
          base (subs file-path 0 pos)]
      (if (string/blank? ext)
        base
        (str base "." ext)))))

(defn make-local-directory
  "Create and return a local directory"
  [^String dir-name]
  (when-not (string/blank? dir-name)
    (let [current-dir (System/getProperty "user.dir")
          dir (File. current-dir dir-name)]
      (if (.exists dir)
        (if (.isFile dir)
          (throw (ex-info "Error creating local directory" {:current-dir current-dir
                                                            :name dir-name
                                                            :reason "File with identical name exists"}))
          dir)
        (when (.mkdirs dir)
          dir)))))

(defn file-name-from-path
  "Extract from the file path the file name."
  [^String file-path]
  (let [pos (or (string/last-index-of file-path "/") -1)]
    (subs file-path (inc pos))))

(defn extract-file
  "Extract a file from a zip archive and write it to the destination
  directory and return the file handle to it."
  [^File zip ^String file-name ^File dest-path]
  (when-not (.isDirectory dest-path)
    (throw (ex-info "Destination is not a directory" {:directory dest-path})))
  (let [zip-file (ZipFile. zip)
        zip-entry (.getEntry zip-file file-name)
        entry-name (file-name-from-path file-name)
        out-file (File. dest-path entry-name)]
    (when zip-entry
      (with-open [in (.getInputStream zip-file zip-entry)
                  out (io/output-stream out-file)]
        (io/copy in out))
      out-file)))

(defn download-file-to-tmp
  "Download the remote file from the given URL into the temporary directory and
  return the File object to it."
  [^URL url]
  (let [tmp-file (File/createTempFile "geonames" nil)]
    (with-open [in (io/input-stream url)
                out (io/output-stream tmp-file)]
      (io/copy in out))
    (when (pos? (.length tmp-file))
      tmp-file)))

(defn download-geonames-file
  "Download the remote GeoNames file under the given URL. If the URL specifies a Zip file, extract
   extract `file-name` from the archive and store it under `dest-path`. Otherwise, write the
   downloaded file as `file-name` into the directory `dest-path`. If the file retrieval was
   successful, return the File object to the created file."
  [^URL url ^File dest-path ^String file-name]
  (when-not (.isDirectory dest-path)
    (throw (ex-info "Non-existing output directory specified" {:directory dest-path})))
  (let [remote-file-name (file-name-from-path (.getFile url))
        downloaded-file (download-file-to-tmp url)]
    (when downloaded-file
      (if (is-zip? remote-file-name)
        (when-let [out-file (extract-file downloaded-file file-name dest-path)]
          (when (pos? (.length out-file))
            out-file))
        (let [out-file (File. dest-path file-name)]
          (io/copy downloaded-file out-file)
          out-file)))))

