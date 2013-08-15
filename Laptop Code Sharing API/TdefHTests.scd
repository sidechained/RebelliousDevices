// TdefH: example of usage

(
// 1. a Tdef declared with no source function
TdefH(\x, {});
)

(
// 2. addition of a source function
TdefH(\x, {
	2.do{
		\hi.postln;
		0.5.wait;
	}
})
)

(
// 3. modification of the source function
TdefH(\x, {
	2.do{
		\hi.postln;
		0.5.wait;
	}
})
)

(
// 4. interrogation the history of the TdefH
TdefH(\x).history
)

(
// 5. extracting the source code of the second execution in history
TdefH(\x).history[2].source.asCompileString
)


// Using Tdef.all to get an IdentityDictionary containing the history of all:

(
// creation of two TdefH's:
TdefH(\y, {
	4.do{
		\hi.postln;
		1.wait;
	}
});

TdefH(\z, {
	2.do{
		\ho.postln;
		0.5.wait;
	}
})
)

(
// calling allHistory to retrieve the history for all Tdefs
TdefH.allHistory
)

// visualisation of allHistory
// what should the GUI show?
// should have some similarities to existing TdefAllGUI i.e.
// - show which Tdefs are playing
// - enable them to be stopped or started
// with other new features
// - collapsable window showing code from history (by default should show most current)
	// - buttons (forwards >, backwards <, start, end, update, copy)
	// - ability to copy code to clipboard
// for now work on visulisation a single Tdef

(
// creation of history for a single Tdef:
TdefH(\zz, {
	4.do{
		\hi.postln;
		1.wait;
	}
});

TdefH(\zz, {
	8.do{
		\hi.postln;
		1.wait;
	}
});

TdefH(\zz, {
	8.do{
		\hi.postln;
		0.5.wait;
	}
});

TdefH(\zz, {
	8.do{
		\hi.postln;
		0.25.wait;
	}
});

TdefH(\zz, {
	8.do{
		\ho.postln;
		0.25.wait;
	}
});

TdefH(\aa, {
	inf.do{
		\a.postln;
		0.5.wait;
	};
});

TdefH(\aa, {
	inf.do{
		\b.postln;
		0.5.wait;
	};
});

TdefH(\aa, {
	inf.do{
		\c.postln;
		0.5.wait;
	};
});

TdefH(\aa, {
	inf.do{
		\d.postln;
		0.5.wait;
	};
});

TdefH(\aa, {
	inf.do{
		\e.postln;
		0.5.wait;
	};
});

TdefHGUI.new(TdefH(\zz));
TdefHAllGUI.new;
)



// everytime allHistory is updated, a call should be made to the GUI (dependency structure)
// document problems - try Scott's hack

// currentEntry - index of
// a new execution



// Q: can the IdentityDictionary produced by TdefH.allHistory be shared?


// add discovery:

////////////////////////
// decentralised discovery of participants
// find who's on the network

//fake another participant
~hail2 = Hail(me: Peer(\me2, NetAddr.localAddr));

// \me2 goes offline
~hail2.free;

//cleanup
~hail.free; ~win.close;

(
~setupPeer = {arg name, port;
	var peer, addrBook, hail, objectSpace;
	thisProcess.openUDPPort(port);
	peer = Peer(name, NetAddr("127.0.0.1", port));
	addrBook.addDependant({|addrBook, what, who|
		[addrBook, what, who].postln;
	});
	addrBook = AddrBook().addMe(peer);
	hail = Hail(addrBook);
	objectSpace = OSCObjectSpace(addrBook, oscPath:'/objectSpace');
	objectSpace.addDependant({|objectSpace, what, key, val|
		[objectSpace, what, key, val].postln;
	});
}
)

//addrBook.add(peer); // add existing peers here

o = ~setupPeer.value(\me1, 3000);
p = ~setupPeer.value(\me2, 4000);
o.put(\me1, [1, 2, 3]);
p.put(\me2, [4, 5, 6]);


