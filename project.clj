(defproject nn "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.3.0"]
                 [http-kit "2.1.13"]
                 [hiccup "1.0.5"]
                 [compojure "1.1.6"]
                 [fogus/ring-edn "0.2.0"]
                 [com.facebook/react "0.8.0.1"]
                 [java-jdbc/dsl "0.1.0"]
                 [org.postgresql/postgresql "9.2-1003-jdbc4"]
                 [org.clojure/java.jdbc "0.3.2"]
                 [ring  "1.2.1"]
                 ]

  :plugins [[lein-cljsbuild "1.0.1"]]

  :main nn.core

  :cljsbuild {:builds
 [
  {:id "dev"
   :source-paths ["src/cljs"]
   :compiler{
    :output-to "resources/public/javascripts/nn.js"
    :output-dir "resources/public/javascripts/out"
    :source-map true
    :optimizations :none
    }}

  {:id "release"
            :source-paths ["src/cljs"]
            :compiler {
              :output-to "resources/public/javascripts/nn.js"
              :optimizations :advanced
              :pretty-print false
              :preamble ["react/react.min.js"]
              :externs ["react/externs/react.js"]}}
  ]

              }


  )