// simple SCLang startup script which will boot the SuperCollider server, allowing it to be connect to remotely

Server.program= "/usr/local/bin/scsynth";
s= Server.default;
s.boot; 