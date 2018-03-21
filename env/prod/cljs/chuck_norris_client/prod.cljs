(ns chuck-norris-client.prod
  (:require [chuck-norris-client.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
