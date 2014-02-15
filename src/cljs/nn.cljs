(ns nn.core
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [goog.dom :as gdom]
            [cljs-webgl.context :as context]
            [cljs-webgl.shaders :as shaders]
            [cljs-webgl.constants :as constants]
            [cljs-webgl.buffers :as buffers]
            [cljs-webgl.typed-arrays :as ta]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]
           ))

(enable-console-print!)

(def cake (atom [0 0]))

(defn transform-matrix [rx, ry, rz]
  (let [cx (Math/cos rx)
        cy (Math/cos ry)
        cz (Math/cos rz)
        sx (Math/sin rx)
        sy (Math/sin ry)
        sz (Math/sin rz)]
    [(* cy cz)  (- (*   sx sy cz) (* cx sz ))  (+ (*  sx sz) (*  cx sy cz))  0
     (* cy sz) (+ (* sx sy sz) (* cx cz))  (- (*  cx sy sz) (*  sx cz))  0
     (- sy)  (* sx cy) (* cx cy) 0 0 0 0 1]))





(def triangle [
                                                  0.0,  0.5,  0.0,
                                                    -0.5, -0.5,  0.0,
                                                    0.5, -0.5,  0.0
                                                    ])

(defn triangulator [triangle key]
  (case key
        :plus (map #(+ % 0.5) (map #(* % 1) triangle))
    :minus (into [] (map #(- % 0.5) (map #(* % -1) triangle)))))

(def triangle-verts (into (triangulator triangle :minus) (into triangle (triangulator triangle :plus))))



(def vertex-shader-source
  "attribute vec3 vertex_position;
  uniform mat4 thing;

  void main() {
     gl_Position = thing * vec4(vertex_position, 1.0);
   }")

(def fragment-shader-source
  "uniform int frame;
   void main() {
     gl_FragColor.r = sin(float(frame) * 0.05) / 2.0 + 0.6;
     gl_FragColor.g = sin(float(frame) * 0.1) / 2.0 + 0.5;
     gl_FragColor.b = sin(float(frame) * 0.02) / 2.0 + 0.5;
     gl_FragColor.a = 1.0;
   }")


#_(defn vertexer [gl factor]
  (let [thing (into [] (map #(* 50 %) ) i-matrix)]

    (buffers/create-buffer gl (ta/float32 thing)
                                          constants/array-buffer
                                          constants/static-draw)))

(defn element-buffer [gl]
  (buffers/create-buffer gl (ta/unsigned-int16 (shuffle (range 9)))
                                           constants/element-array-buffer
                                           constants/static-draw))



(let
    [gl (context/get-context (gdom/getElement "grid"))
     shader (shaders/create-program gl [(shaders/create-shader gl constants/vertex-shader vertex-shader-source)
                                        (shaders/create-shader gl constants/fragment-shader fragment-shader-source)])
     vertex-buffer (buffers/create-buffer gl (ta/float32 triangle-verts)
                                          constants/array-buffer
                                          constants/static-draw)

     draw (fn [[frame thing] continue]
            (buffers/clear-color-buffer gl 0 0 0 1)
            (buffers/draw! gl
                           shader

                           {:buffer vertex-buffer
                            :attrib-array (shaders/get-attrib-location gl
                                                                       shader
                                                                       "vertex_position")
                            :mode constants/triangle-fan
                            :first 0 ;; what
                            :count 9 ;; huh
                            :components-per-vertex 3
                            :type constants/float
                            :normalized? false
                            :stride 0
                            :offset 0}

                           [{:name "frame" :type :int :values [frame]}
                            {:name "thing" :type :mat4 :values (transform-matrix frame (if (= 0 (mod thing 2)) thing  0) 0)}]

                           {:buffer (element-buffer gl)
                            :count 9
                            :type constants/unsigned-short
                            :offset 0})


            (.requestAnimationFrame js/window (fn [time-elapsed] (continue @cake continue))))]

  (js/setInterval #(reset! cake [(inc (@cake 0)) (mod (inc (@cake 1)) 20)]) 100)
  (draw @cake draw))









