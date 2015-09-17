(ns untangled.test.state
  (:require [untangled.application :as app]
            [untangled.state :as state]))

(defn app-state-changer
  "Create a function that can evolve the state of an application (from the top-level context). You supply a pure function
  that returns a new state (given the current state). The returned function, when called, causes this change to take
   effect in the given application. Used in tests where you want to apply a function to the application state
   as you would in a localized component. 
   
   Example:
   
   Assume you have a component whose state can be updated with function `wiggle`:
   
        (defn make-component [c] { :wiggling true })
        (defn wiggle [c] (update w :wiggling not))
        (defscomponent MyComponent ...)
   
        (specification \"My Component\"
          (let [renderer #(MyComponent %1 %2)
                initial-state (make-component)
                application (u/new-application renderer initial-state :test-mode true)
                current-view #(app/render application)
                ]
            (behavior \"can be tested through application state evolution.\"
                      (let [wiggle-my-thing (app-state-changer application wiggle)]
                        (is (not (:wiggling (app/current-state application))))
                        (wiggle-my-thing)
                        (is (:wiggling (app/current-state application)))
                        )
                      )

            ))
  "
  [application f]
  (let [context (app/top-context application)
        op (state/op-builder context)
        evolve (op f)]
    evolve))

