(ns 23min.immutant2
  {:boot/export-tasks true}
  (:require
   [boot.pod           :as pod]
   [boot.util          :as util]
   [boot.core          :as core]
   [boot.task.built-in :as task]))

(def ^:private deps
  '[[aleph     "0.4.0-beta2"]
    ; [compojure "1.3.1"]]
  )

(core/deftask serve
  "Start an Aleph web server on given address, blocking
   the boot task pipeline by default.

   If no url path is specified the root one ('/') is used. Listens
   on port 3000 by default."
  [n handler  HANDLER  sym   "Ring handler"
   i host     HOST     str   "The interface bind address"
   p port     PORT     int   "The port to listen on."
   u path     PATH     str   "Maps the handler to a prefix of the url path"
   b block             bool  "Blocking (for standalone use)"]
  (let [worker   (pod/make-pod (assoc-in (core/get-env) [:dependencies] deps))
        host     (or host "localhost")
        port     (or port 3000)
        path     (or path "/")
        block    (or block false)]
    (core/cleanup
     (util/info "\n<< stopping Aleph... >>\n")
     (pod/with-eval-in worker (.stop server)))
    (comp
     (core/with-pre-wrap fileset
       (pod/with-eval-in worker
         (require '[aleph.http :refer [start-server]])
         (def server
           (start-server handler {:host ~host :port ~port :path ~path})))
       (util/info "<< started Aleph on http://%s:%d%s>>\n%s\n" host port path handler)
       fileset)
     (if block
       (task/wait)
       identity))))
