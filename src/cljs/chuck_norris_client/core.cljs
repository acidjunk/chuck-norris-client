(ns chuck-norris-client.core
    (:require [json-html.core :refer [edn->hiccup]]
              [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [soda-ash.core :as sa]
              [re-graph.core :as re-graph]))

(re-graph/init {:http-url "https://itility-hackatrain-production-team8.azurewebsites.net/graphql" :ws-url nil})

;; -------------------------
;; State
(defonce state
  (reagent/atom {:jokes {}
                 :search ""
                 :order-prop :name
                 :current-page "Home"   ;; start sensible
                 }))


;; -------------------------
;; Data
(defn on-graph-data! [{:keys [data errors] :as payload}]
  (let [{:keys [jokes]} @state]
    (swap! state assoc :jokes data)
   )
  )

;; -------------------------
(defn menu [item]
  [:div {:class "ui large secondary inverted pointing menu"}
   [:div {:class "ui container"}
    [:div {:class (if(= "home" item) "item active" "item")} [:a {:href "/"} "Home"]]
    [:div {:class (if(= "about" item) "item active" "item")} [:a {:href "/about"} "About"]]]])

;; -------------------------
;; Views

(defn home-page []
  [:div
  [:div {:class "ui inverted vertical masthead center aligned segment"}
   (menu "home")
   [:div {:class "ui text container"}
    [:h2 {:class "ui inverted header"} "Welcome to the chuck-norris-client"]
    [:p "The best way to consume Chuck"]
    ]]

   [:div {:class "ui hidden divider"}]
   [:div {:class "ui container"}
    [:h2 {:class "ui header"} [:i {:class "feed icon"}] [:div {:class "content"} "Live feed"]]
    [:div {:class "ui raised segment"}
     [:div {:class "ui feed"}
      [:div {:class "event"}
       [:div {:class "label"} [:img {:src "https://semantic-ui.com/images/avatar/small/elliot.jpg"}]]
       [:div {:class "content"}
        [:div {:class "summary"} "When Chuck Norris throws exceptions, it's across the room."
         [:div {:class "date"} "1 hour ago"]
         [:div {:class "meta"} [:a {:class "like"}] [:i {:class "like icon"}] "4 likes"]
        ]
       ]
      ]

      [:div {:class "event"}
       [:div {:class "label"} [:img {:src "https://semantic-ui.com/images/avatar/small/elliot.jpg"}]]
       [:div {:class "content"}
        [:div {:class "summary"} "When Chuck Norris throws exceptions, it's across the room."
         [:div {:class "date"} "1 hour ago"]
         [:div {:class "meta"} [:a {:class "like"}] [:i {:class "like icon"}] "4 likes"]
         ]
        ]
       ]

      [:div {:class "event"}
       [:div {:class "label"} [:img {:src "https://semantic-ui.com/images/avatar/small/elliot.jpg"}]]
       [:div {:class "content"}
        [:div {:class "summary"} "When Chuck Norris throws exceptions, it's across the room."
         [:div {:class "date"} "1 hour ago"]
         [:div {:class "meta"} [:a {:class "like"}] [:i {:class "like icon"}] "150 likes"]
         ]
        ]
       ]


     ]
    ]
   ]
   [:div {:class "ui segment"} [:div {:class "content"} [edn->hiccup @state]]]


   ])

(defn about-page []
  [:div {:class "ui inverted vertical masthead center aligned segment"}
    (menu "about")

   [:div [:h2 {:class "ui inverted header"} "About chuck-norris-client wow ow"]
   [:div "Chuck Norris facts are satirical factoids about martial artist and actor Chuck Norris that have become an
          Internet phenomenon and as a result have become widespread in popular culture."]
   [:a {:href "https://en.wikipedia.org/wiki/Chuck_Norris_facts"} "Visit wikipedia"]]])

;; -------------------------
;; Routes

(defonce page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
;  (load-jokes! state)

  (re-graph/query "{ jokes { id, value } }"  ;; your graphql query
                  {:some "variable"}  ;; arguments map
                  on-graph-data!)           ;; callback event when response is recieved


  (mount-root))
