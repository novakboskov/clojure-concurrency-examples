(ns concurrency.core
  (:gen-class))

(def state (ref {:num 0}))

(defn increment-num
  [name what]
  (let [print-inc (fn [s]
                    (println "Retry from " name ", state: " s)
                    (update s :num inc))]
    (dosync
     (Thread/sleep 1000)
     (println "Worker name: " name)
     (case what
       ::alter   (alter state print-inc)
       ::commute (commute state print-inc)))))

;; with alter - if there is retry, whole dosync is done again.
#_(do
    (future (increment-num "A" ::alter))
    (future (increment-num "B" ::alter)))

;; with commute - there are always two tries for every commute. ONLY
;; the commute's function is retried NOT the whole dosync
#_(do
    (future (increment-num "A" ::commute))
    (future (increment-num "B" ::commute)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;;
;; from http://www.braveclojure.com/zombie-metaphysics/
;;

;; (defn sleep-print-update
;;      [sleep-time thread-name update-fn]
;;      (fn [state]
;;        (Thread/sleep sleep-time)
;;        (println (str thread-name ": " state))
;;        (update-fn state)))

;; (def counter (ref 0))

;; (future (dosync (commute counter (sleep-print-update 100 "Thread A" inc))))
;; (future (dosync (commute counter (sleep-print-update 150 "Thread B" inc))))
