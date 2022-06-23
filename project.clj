(defproject org.clojars.punit-naik/in-memory-mongo "1.0.0"
  :description "A Clojure library designed to work with an in-memory version of MongoDB for testing purposes"
  :url "https://github.com/punit-naik/in-memory-mongo"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[com.taoensso/timbre "5.2.1"]
                 [de.bwaldvogel/mongo-java-server "1.40.0"]
                 [org.clojure/clojure "1.11.1"]
                 [org.mongodb/mongo-java-driver "3.12.11"]]
  :profiles {:uberjar {:aot :all}
             :test {:dependencies [[com.novemberain/monger "3.6.0"]]}}
  :repl-options {:init-ns org.clojars.punit-naik.in-memory-mongo})
