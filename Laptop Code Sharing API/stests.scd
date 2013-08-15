
(
// STest
~peer1 = Peer(\me1, NetAddr.localAddr);
~addrBook1 = AddrBook().addMe(~peer1);

)

(
~aFunc = {\test.postln};
thisProcess.openUDPPort(3000);
thisProcess.openUDPPort(3001);
~peer1 = Peer(\me1, NetAddr("127.0.0.1", 3000));
~peer2 = Peer(\me2, NetAddr("127.0.0.1", 3001));
~addrBook1 = AddrBook().addMe(~peer1);
~addrBook2 = AddrBook().addMe(~peer2);
~addrBook1.add(~peer2);
~addrBook2.add(~peer1);
~objSpace1 = OSCObjectSpace(~addrBook1, oscPath:'/objSpace'); // me1's local copy
~objSpace2 = OSCObjectSpace(~addrBook2, oscPath:'/objSpace'); // me2's local copy
~objSpace1.addDependant({|objectSpace, what, key, val|
	[objectSpace, what, key, val].postln;
});
~objSpace2.addDependant({|objectSpace, what, key, val|
	[objectSpace, what, key, val].postln;
});
~sTest = STest(~peer1, \x, {1.postln});
)
~objSpace1.put(~sTest.name, ~sTest);
~objSpace2.put(~sTest.name, ~sTest);
)

// when we modify some aspect of the STest contained in the OSCObjectSpace, does it update?
~objSpace1.at(\x).play.isPlaying
~objSpace1.at(\x).isPlaying
// it does, but no notification occurs (does this matter?)
~objSpace2.at(\x).isPlaying

(
~peer1 = Peer(\me1, NetAddr.localAddr);
~addrBook1 = AddrBook().addMe(~peer1);
STdef.updateAddrBook(~addrBook1);
// create anew
a = STdef(~peer1, \x, {1.postln});
)

(
// update
a = STdef(~peer1, \x, {2.postln});
)

a.creatorPeer
STdef.addrBook.peers


// Creating a Shared Tdef:
// - Tdefs always contain open functions, so cannot be archived e.g.
Tdef(\x, {1.postln}).asArchive

// so send the function itself
// but it turns out also this cannot be archived in Utopia
// so send as a compile string and interpret on the other side (hacky for now, but should work)

(
// example showing how to store a function as a compile string in an OSCObjectSpace
~aFunc = {\test.postln};
~peer1 = Peer(\me1, NetAddr.localAddr);
~addrBook1 = AddrBook().addMe(~peer1);
~objSpace1 = OSCObjectSpace(~addrBook1, oscPath:'/objSpace');
~objSpace1.addDependant({|objectSpace, what, key, val|
	[objectSpace, what, key, val].postln;
});
~objSpace1.put(\me, ~aFunc.asCompileString);
)

// test with two peers

// make some peers and address books
(
~aFunc = {\test.postln};
thisProcess.openUDPPort(3000);
~peer1 = Peer(\me1, NetAddr.localAddr);
~peer2 = Peer(\me2, NetAddr("127.0.0.1", 3000)); // I'll use a different port and simulate a different machine
~addrBook1 = AddrBook().addMe(~peer1);
~addrBook2 = AddrBook().addMe(~peer2);
~addrBook1.add(~peer2);
~addrBook2.add(~peer1);
~objSpace1 = OSCObjectSpace(~addrBook1, oscPath:'/objSpaces'); // me1's local copy
~objSpace2 = OSCObjectSpace(~addrBook2, oscPath:'/objSpaces'); // me2's local copy
~objSpace1.addDependant({|objectSpace, what, key, val|
	[objectSpace, what, key, val].postln;
});
~objSpace2.addDependant({|objectSpace, what, key, val|
	[objectSpace, what, key, val].postln;
});
~objSpace1.put(\me, ~aFunc.asCompileString);
~objSpace2.put(\me, ~aFunc.asCompileString);
)





