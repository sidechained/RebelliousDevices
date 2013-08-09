// Two Way OSC Test Between SuperCollider and Python
// - SuperCollider file

// -- sends '\led 0' or '\led 1' messages every second
// -- receives '\led' messages and prints to the screen

(
// 1. receiving from Python
// setup responder:
OSCFunc({arg msg;
	("receiving msg:" + msg[0] + msg[1]).postln;
}, '/led');

// 2. sending to Python
n = NetAddr("localhost", 9000);
Task({
	inf.do{
		var msg;
		msg = [\led, 2.rand];
		("sending msg:" + "/" ++ msg[0] + msg[1]).postln;
		n.sendMsg(*msg);
		1.wait;
	};
}).play;
)



