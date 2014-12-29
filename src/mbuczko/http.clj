(ns mbuczko.immutant2
  {:boot/export-tasks true}
  (:require
   [boot.pod           :as pod]
   [boot.util          :as util]
   [boot.core          :as core]
   [boot.task.built-in :as task]))

(def ^:private deps
  '[[org.immutant/immutant "2.0.0-alpha2"]
    [compojure "1.3.1"]])

(core/deftask serve
  "Start an immutant2 web server on given address, blocking
   the boot task pipeline by default.

   If no url path is specified the root one ('/') is used. Listens
   on port 3000 by default."
  [h host     HOST     str  "The interface bind address"
   p port     PORT     int  "The port to listen on."
   t path     PATH     str  "Maps the handler to a prefix of the url path"
   b block             bool "Blocking (for standalone use)"]
  (let [worker   (pod/make-pod (assoc-in (core/get-env) [:dependencies] deps))
        host     (or host "localhost")
        port     (or port 3000)
        path     (or path "/")
        block    (or block false)]
    (core/cleanup
     (util/info "\n<< stopping Immutant2... >>\n")
     (pod/with-eval-in worker (.stop server)))
    (comp
     (core/with-pre-wrap fileset
       (pod/with-eval-in worker
         (require '[immutant.web       :refer [run]]
                  '[compojure.route    :refer [files]])
         (def server
           (run {:host ~host :port ~port :path ~path :join? false})))
       (util/info "<< started Immutant2 on http://%s:%d%s >>\n" host port path)
       fileset)
     (if block
       (task/wait)
       identity))))
