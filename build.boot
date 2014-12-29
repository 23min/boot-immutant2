(set-env!
 :src-paths    #{"src"}
 :dependencies '[[org.clojure/clojure "1.6.0"     :scope "provided"]
                 [boot/core           "2.0.0-rc1" :scope "provided"]
                 [adzerk/bootlaces    "0.1.5"     :scope "test"]])

(require
 '[adzerk.bootlaces :refer :all]
 '[boot.pod         :as pod]
 '[boot.util        :as util]
 '[boot.core        :as core])

(def +version+ "0.1.0")

(bootlaces! +version+)

(task-options!
pom {:project 'mbuczko/boot-immutant2
      :version +version+
      :description "Boot task to start/stop Immutant2 server."
      :url         "https://github.com/mbuczko/boot-immutant2"
      :scm         {:url "https://github.com/mbuczko/boot-immutant2"}
      :license     {:name "Eclipse Public License"
                    :url  "http://www.eclipse.org/legal/epl-v10.html"}})

(require '[mbuczko.immutant2 :refer :all])

