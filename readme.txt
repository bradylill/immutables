Notes for devs:

- "lein ring server" will compile cljs and start a server
- "lein repl" then (browser-repl) will kick off a repl in a terminal
- refresh the page with js on it, then try (js/alert "bang!") - if all is good the browser should show an alert
- if using emacs you have to paste the full browser-repl command - see the code inside project.clj
- "lein cljsbuild clean", "lein cljsbuild once", "lein cljsbuild auto" are your friends

