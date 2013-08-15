TestReg {

	// if a ******* errno 49 occurs, turn wifi off and on again
	// it relates to not being able to successfully broadcast
	// diagnostics:
	// NMLNetAddrMP("255.255.255.255", 57120 + (0..7)).sendMsg(\1)
	// NetAddr("255.255.255.255", 57120).sendMsg(\2)

	var <hailer;
	var <myPeer, <addrBook;

	*new {arg playerName, port;
		^super.new.join(playerName, port)
	}

	join {arg playerName, portNum;
		(playerName ++ " joined (local)").postln;
		// set up an empty address book:
		addrBook = AddrBook();
		// open a port
		thisProcess.openUDPPort(portNum);
		// make a new peer for myeslf:
		myPeer = Peer(playerName, NetAddr("localhost", portNum));
		// add my peer to address book explicitly:
		addrBook.addMe(myPeer);
		// register to receive updates from other peers: (shoudn't this come earlier?)
		addrBook.addDependant({arg addrBook, what, who;
			addrBook.names.asArray.postln;
		});
		// broadcast my existence:
		hailer = Hail(addrBook, me: myPeer);
	}

	leave {arg playerName;
		hailer.free;
		(playerName ++ " left (local)").postln;
	}

}