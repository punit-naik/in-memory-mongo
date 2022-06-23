(ns org.clojars.punit-naik.in-memory-mongo-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [monger.collection :as mc]
            [monger.core :as mg]
            [org.clojars.punit-naik.in-memory-mongo :as imm]))

(defonce monger (atom nil))
(defonce mongo-coll "documents")
(defn db
  []
  (:db @monger))

(defn my-fixture
  [test-fn]
  (let [server (imm/start)
        conn (mg/connect {:host imm/localhost
                          :port (-> server
                                    .getLocalAddress
                                    .getPort)})
        db (mg/get-db conn "in-memory-mongo-test")]
    (reset! monger {:conn conn :db db})
    (test-fn)
    (reset! monger nil)
    (mg/disconnect conn)
    (imm/stop server)))

(use-fixtures :once my-fixture)

(defn generate-docs
  []
  (->> (range 10)
       (map #(assoc {} :_id % :name (str "John+" %) :age (+ 30 %)))))

(deftest insert!-test
  (doseq [doc (generate-docs)]
    (is (= (mc/insert-and-return
            (db)
            mongo-coll
            doc)
           doc))))

(deftest query-test
  (let [docs (generate-docs)]
    (is (= (->> (mc/find-maps (db) mongo-coll)
                (remove #(>= (:age %) 40)))
           docs))
    (doseq [{:keys [_id] :as doc} docs]
      (is (= (mc/find-one-as-map (db) mongo-coll {:_id _id}) doc)))))

(deftest update!-test
  (mc/update
   (db)
   mongo-coll
   {:name "Doe"}
   {:age 40 :name "Doe"}
   {:upsert true})
  (is (= (->> (mc/find-maps (db) mongo-coll {:age 40})
              (map #(dissoc % :_id)))
         [{:name "Doe" :age 40}]))
  (let [{:keys [_id] :as upserted-doc} (mc/find-one-as-map (db) mongo-coll {:name "Doe"})]
    (mc/update-by-id (db) mongo-coll _id {:name "Doe" :age 100})
    (is (= (mc/find-one-as-map (db) mongo-coll {:_id _id})
           (assoc upserted-doc :age 100)))))

(defn doc-count
  []
  (mc/count (db) mongo-coll))

(deftest delete!-test
  (mc/insert-and-return
   (db)
   mongo-coll
   {:_id 11 :name "Punit" :age 50})
  (let [old-count (doc-count)]
    (mc/remove (db) mongo-coll {:name "Punit"})
    (is (= (doc-count) (dec old-count)))))