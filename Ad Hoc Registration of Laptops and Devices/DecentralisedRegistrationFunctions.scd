// Utopia - basic peer registration functions

////////////////////////
// decentralised discovery of participants
// find who's on the network
(
~win = Window("AdHocSociety").front;
~win.layout = VLayout.new.add(~listView = ListView.new);

~addrBook = AddrBook.new;
// to get updates, just add a dependant
~addrBook.addDependant({|addrBook, what, who|
	{~listView.items = addrBook.peers.collectAs({|peer|
		peer.name ++ " | " ++ peer.addr.ip ++ " | " ++ if(peer.online, "online", "offline");
	}, Array)}.defer;
});

~addrBook.addMe; // will automatically add you using your user name

~hail = Hail(~addrBook);
)

//fake another participant
~hail2 = Hail(me: Peer(\me2, NetAddr.localAddr));

// \me2 goes offline
~hail2.free;

//cleanup
~hail.free; ~win.close;
