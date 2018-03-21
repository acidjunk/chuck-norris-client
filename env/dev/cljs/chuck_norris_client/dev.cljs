(ns ^:figwheel-no-load chuck-norris-client.dev
  (:require
    [chuck-norris-client.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
