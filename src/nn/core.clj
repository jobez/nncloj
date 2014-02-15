(ns nn.core
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [file-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [clojure.core.async :refer [chan <! >! put! close! go-loop go]]
            [org.httpkit.server :as http-kit]
            [ring.middleware.reload :as reload]
            [hiccup.page :as html]
            [garden.core :refer [css]]
            [hiccup.element :as element]
            [ring.util.response :as resp]))

(defn css-er []
  (let [full-size {:width "500px" :height "500px" :z-index "-42"} ]
    (css [:html {:height "100%" :width "100%"}]
         [:body {:display "flex"
                 :justify-content "center"
                 :align-items "center"
                 :height "100%"
                 :background "black"
                 :flex-flow "column"}
          [:span {:color "white" :font-size "350%"}]
          [:canvas {:z-index "-99" :position "absolute" :top 0 :left "22%"}]]
         )))

(defn main-page []
  (html/html5
   [:head
    [:title "depth in field"]
    [:style (css-er)]
    ]
   [:body
    [:canvas#grid {:width "800" :height "800"}]
    [:span "coming in terms eventual forever"]]
   (html/include-js "/javascripts/nn.js")

   ))

(defn webgl-page []
  (html/html5
   [:head
    [:title "future"]]
   [:body
    [:canvas#grid {:width "500" :height "500"}]
    [:div#app]
    (html/include-js "http://fb.me/react-0.8.0.js")
    (html/include-js "/javascripts/out/goog/base.js")
    (html/include-js "/javascripts/nn.js")
    (element/javascript-tag "goog.require('nn.core')")]))

(defroutes nroutes
  (GET "/" [] (resp/response (main-page)))
  (GET "/webgl" [] (resp/response (webgl-page)))

  (route/resources "/" {:root "public"}))

(def app (-> nroutes wrap-edn-params reload/wrap-reload))

(defn -main
  ([port]
    (let [Port (Integer/parseInt port)]
      (println "server running w/ port" port)
      (http-kit/run-server app {:port Port})))
  ([]
   (let [strPort (System/getenv "PORT")
         Port (Integer/parseInt strPort)]
     (println "we're running on:" strPort)
     (http-kit/run-server app {:port Port}))))
