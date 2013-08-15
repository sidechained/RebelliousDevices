// 1. Globalising Tdefs
// - idea is that a Tdef is no longer local
// - instead it can be modified and re-executed by other players
// Q: to enable this, what object should be shared in the OSCObjectSpace?
// Tdef.all returns an IdentityDictionary, which we could put into the space
// every time the IdentityDictionary gets updated, it
// aim to to get updated each time a new tDef is added remotely

// *
// in Republic we can rip the source code of Tdefs from history and execute them again locally
// in this case two Tdefs with the same name can play independently on a number of machines
// an improvement to this system would be automatic awareness of what Tdefs are playing
// players should be able to override Tdefs
// a concrete example would be:
// 1. Jonas plays a Tdef (locally) that creates a 'blip' Synth every second
// 2. Graham updates this Tdef (remotely) to  this to every half second
// 3. Jonas

// a wider question is does it matter on which machine code is executed?


// Graham's modified Tdef
// - also executed on Jonas' machine
// - 0. code for \blipPlayer requested by Graham
// - 1. code for \blipPlayer sent to Graham's machine
// - 2. code for \blipPlayer edited by Graham
// - 3. new code for \blipPlayer sent back to Jonas
// - 4. new code for \blipPlayer executed on Jonas' machine

// Example:
// each time a Tdef is created (not just played), pass the following to all players:
//		- the addr of the machine of which the Tdef was created (i.e. where to send back for re-execution)
// 		- the name of the Tdef
//		- the source code for the Tdef

// Problem:
// we can't share Tdefs direct using an OSCObjectSpace, as they contain open Functions which cannot be archived
//

// a synthDef (in the end, should be created on all machines)

SynthDef(\blip, {
	Out.ar([0, 1], Line.kr(1, 0, 0.1) * SinOsc.ar(440))
}).add;

// Jonas' original Tdef
// - executed on his own machine

~tdefTracker = IdentityDictionary.new;

~tdefToShare = Tdef(\blipPlayer, {
	inf.do{
		Synth(\blip);
		1.wait;
	}
}).play

~tdefInfo = (playingOn: , name: ~tdefToShare.key, code: ~tdefToShare.source.asCompileString)

~tdefTracker.put(~tdefInfo);





a = Tdef(\blipPlayer, {
	inf.do{
		Synth(\blip);
		0.5.wait;
	}
}).play

/* Notes about OSCObjectSpace

1. Only objects which can be archived (i.e. those that do not contain open functions) can be sent
2. OSCObjectSpace does not track internal changes in the objects it holds. You can set the key again with the changed object, but this will result in a new object being made in other peer's object spaces, not the old one being changed. If you want to sync changes to remote objects the OpenObject quark might be better.

*/

Tdef.all.asArchive

// first, make some peers and address books
(
thisProcess.openUDPPort(3000);
~peer1 = Peer(\me1, NetAddr.localAddr);
~peer2 = Peer(\me2, NetAddr("127.0.0.1", 3000));
~addrBook1 = AddrBook().addMe(~peer1);
~addrBook2 = AddrBook().addMe(~peer2);
~addrBook1.add(~peer2);
~addrBook2.add(~peer1);
~tDefTracker1 = OSCObjectSpace(~addrBook1, oscPath:'/tDefTracker');
~tDefTracker1.acceptEvents_(true);
~tDefTracker2 = OSCObjectSpace(~addrBook2, oscPath:'/tDefTracker');
~tDefTracker2.acceptEvents_(true);
~tDefTracker1.addDependant({|objectSpace, val, key, what|
	if(key != ~addrBook1.me.name, {
		"new tDef added".postln;
		what.postln;
	});
});
)

(
// create a Tdef
~tDefToShare = Tdef(\x, {
	inf.do{
		"hi".postln;
		0.5.wait;
	}
});
~tDefInfoToShare = (peerCreatedOn: ~peer1, sourceCode: ~tDefToShare.source.asCompileString);
// extract relevant info
~tDefTracker1.put(~tDefToShare.key, ~peer1.name);
)

~tDefTracker1.getPairs