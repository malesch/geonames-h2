(ns geonames-h2.util-test
  (:require [clojure.test :refer :all]
            [geonames-h2.util :as u]))

(defn falsey? [x]
  (or (nil? x) (false? x)))

(defn truly? [x]
  (not (falsey? x)))

(deftest test-is-zip?
  (is (falsey? (u/is-zip? nil)))
  (is (falsey? (u/is-zip? "")))
  (is (falsey? (u/is-zip? "foo")))
  (is (falsey? (u/is-zip? "foo.txt")))
  (is (false? (u/is-zip? "foo.zip.txt")))
  (is (truly? (u/is-zip? "foo.zip")))
  (is (truly? (u/is-zip? "foo.ZIP"))))

(deftest test-rename-ext
  (is (= (u/rename-ext "foo.zip" nil) "foo"))
  (is (= (u/rename-ext "foo" nil) "foo"))
  (is (= (u/rename-ext "foo.zip" "") "foo"))
  (is (= (u/rename-ext "foo" "") "foo"))
  (is (= (u/rename-ext "foo" "txt") "foo.txt"))
  (is (= (u/rename-ext "foo.zip" "txt") "foo.txt"))
  (is (= (u/rename-ext "foo.txt.zip" "txt") "foo.txt.txt")))

(deftest test-file-name-from-path
  (is (= (u/file-name-from-path "foo") "foo"))
  (is (= (u/file-name-from-path "foo.bar") "foo.bar"))
  (is (= (u/file-name-from-path "/foo.bar") "foo.bar"))
  (is (= (u/file-name-from-path "foo/bar.baz") "bar.baz"))
  (is (= (u/file-name-from-path "http://download.geonames.org/export/dump/") ""))
  (is (= (u/file-name-from-path "http://download.geonames.org/export/dump/foo") "foo"))
  (is (= (u/file-name-from-path "http://download.geonames.org/export/dump/foo.bar") "foo.bar")))
