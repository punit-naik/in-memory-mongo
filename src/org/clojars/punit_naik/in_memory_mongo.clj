(ns org.clojars.punit-naik.in-memory-mongo
  (:import [de.bwaldvogel.mongo MongoServer]
           [de.bwaldvogel.mongo.backend.memory MemoryBackend]
           [java.net ServerSocket])
  (:require [taoensso.timbre :as log]))

(defonce localhost "127.0.0.1")

(defn mongo-server
  [& [port]]
  (let [port (int (or port 27017))]
    (log/info "Creating MongoDB in-memory server at port" port)
    (let [server (MongoServer. (MemoryBackend.))]
      (.bind server localhost port)
      server)))

(defn start
  [& [port]]
  (loop [port (or port 27017)]
    (let [socket (try (ServerSocket. port)
                      (catch Exception _ nil))]
      (if socket
        (do (.close socket)
            (mongo-server port))
        (let [new-port (inc port)]
          (log/info (str "Port " port " is busy, retrying with port " new-port))
          (recur new-port))))))

(defn stop
  [^MongoServer server]
  (.shutdown server))
