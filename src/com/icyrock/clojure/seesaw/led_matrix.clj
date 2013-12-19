(ns com.icyrock.clojure.seesaw.led-matrix
  (:import [java.util Calendar])
  (:use seesaw.core
        seesaw.graphics))

(def lcd-dot-style-off
  (style
   :background "#181818"
   :stroke (stroke :width 3)))

(def lcd-dot-style-on
  (style
   :background "#00bc00"
   :stroke (stroke :width 3)))

(def lcd-dot-styles
  {false lcd-dot-style-off
   true lcd-dot-style-on})

(defn draw-lcd-dot [g width height is-on]
  (let [dot-style (lcd-dot-styles is-on)
        border (-> dot-style :stroke .getLineWidth)
        border2 (* 2 border)]
    (draw g
          (ellipse border border (- width border2) (- height border2)) dot-style)))

(def lcd-symbol-dots
  {\0
   [".***."
    "*...*"
    "*...*"
    "*...*"
    "*...*"
    "*...*"
    ".***."]
   \1
   ["..*.."
    ".**.."
    "..*.."
    "..*.."
    "..*.."
    "..*.."
    "*****"]
   \2
   [".***."
    "*...*"
    "....*"
    "...*."
    "..*.."
    ".*..."
    "*****"]
   \3
   [".***."
    "*...*"
    "....*"
    "..**."
    "....*"
    "*...*"
    ".***."]
   \4
   ["...*."
    "..**."
    ".*.*."
    "*..*."
    "*****"
    "...*."
    "...*."]
   \5
   ["*****"
    "*...."
    "****."
    "....*"
    "....*"
    "*...*"
    ".***."]
   \6
   [".***."
    "*...*"
    "*...."
    "****."
    "*...*"
    "*...*"
    ".***."]
   \7
   ["*****"
    "....*"
    "...*."
    "..*.."
    "..*.."
    "..*.."
    "..*.."]
   \8
   [".***."
    "*...*"
    "*...*"
    ".***."
    "*...*"
    "*...*"
    ".***."]
   \9
   [".***."
    "*...*"
    "*...*"
    ".****"
    "....*"
    "*...*"
    ".***."]
   \:
   ["....."
    "....."
    "..*.."
    "....."
    "..*.."
    "....."
    "....."]
   })

; Assuming all the symbol bitmaps have the same size
(def dot-spec-width (count (first (lcd-symbol-dots \0))))
(def dot-spec-height (count (lcd-symbol-dots \0)))

(defn draw-lcd-symbol [g width height symbol]
  (let [dots (lcd-symbol-dots symbol)
        dot-width (/ width dot-spec-width)
        dot-height (/ height dot-spec-height)]
    (doseq [row dots]
      (doseq [cell row]
        (draw-lcd-dot g dot-width dot-height (= cell \*))
        (translate g dot-width 0))
      (translate g (- width) dot-height))))

(defn get-time-string []
  (let [c (Calendar/getInstance)
        h (.get c Calendar/HOUR_OF_DAY)
        m (.get c Calendar/MINUTE)
        s (.get c Calendar/SECOND)]
    (format "%02d:%02d:%02d" h m s)))

(defn paint-lcd-symbol [c g]
  (try
    (let [symbols (get-time-string)
          symbol-count (count symbols)
          width (.getWidth c)
          height (.getHeight c)
          symbol-width (/ width symbol-count)]
      (doseq [symbol symbols]
        (push g
              (draw-lcd-symbol g (- symbol-width 20) height symbol))
        (translate g symbol-width 0)))
    (catch Exception e
      (println e))))

(defn content-panel []
  (border-panel
   :center (canvas :id :clock
                   :background "#000000"
                   :paint paint-lcd-symbol)))

(defn make-frame []
  (let [f (frame :title "com.icyrock.clojure.seesaw.led-matrix"
                 :width 1200 :height 300
                 :on-close :dispose
                 :visible? true
                 :content (content-panel))]
    (.setLocation f (java.awt.Point. 100 300))
    (timer (fn [e] (repaint! (select f [:#clock])) 1000))))

(defn -main [& args]
  (native!)
  (make-frame))
(-main)

