STdef : Tdef {

	// prototype for a shared Tdef interface
	// PRINCIPLE: the Tdef will be active (i.e. play from) only on the PC on which it was first created
	// other peers simply send messages to the creatorPeer in order to play/stop or update the STdef

	// at the moment

	var <creatorPeer; // instance of Peer who created this STdef
	var isPlayingSomewhere; // boolean which tells if the
	//classvar allSTdefs; // keeps a record of all STdefs
	classvar <addrBook; // all Peers on the network

	*initClass {
		super.initClass; //
		// allSTdefs = IdentityDictionary.new;
		this.setupResponders;
	}

	*setupResponders {arg addr;
		// prepare to receive play and stop messages
		this.setupNewResponder;
		//this.setupPlayResponder;
		//this.setupStopResponder;
	}

	*updateAddrBook {arg argAddrBook;
		// should be called every time the address book changes
		addrBook = argAddrBook;
	}

	*new {arg creatorPeer, name, item;
		^this.sendNewMsg(name, item);
	}

	*sendNewMsg {arg name, item;
		// send a message to all peers to instantiate or replace a new STdef
		addrBook.sendAll(\new, name, item);
	}

	*createNew {arg argCreatorPeer, name, item;
		^super.new(name, item).setCreatorPeer(argCreatorPeer);
	}

	setCreatorPeer {arg argCreatorPeer;
		argCreatorPeer.postln;
		creatorPeer = argCreatorPeer;
	}

/*
	clear {
		// to do
	}

	play {arg name;
		// send play message for this STdef to all peers in address book
		addrBook.sendAll(\play, name)
	}

	setupPlayResponder {
		OSCFunc({|msg, time, senderAddr, recvPort|
			var name = msg[0];
			var sTdef = this.lookup(name);
			[msg, time, senderAddr, recvPort].postln;
			if (ownerPeer.addr == senderAddr, {
				this.playLocal(name);
			}, {
				this.playRemote(name);
			});
		}, '/play');
	}

	playLocal {
		super.play; // play the Tdef as normal
		isPlayingSomewhere == true; // note that the STdef is being played
	}

	playRemote {
		isPlayingSomewhere == true; // don't actually play, just note that the STdef is being played somewhere
	}

	stop {arg name;
		// send stop message for this STdef to all peers in address book
		addrBook.sendAll(\stop, name)
	}

	setupStopResponder {
		OSCFunc({|msg, time, senderAddr, recvPort|
			[msg, time, senderAddr, recvPort].postln;
			isPlayingSomewhere == true; // set playing state to true
			if (ownerPeer.addr == senderAddr, {
				this.playLocal;
			}, {
				this.playRemote;
			});
		}, '/stop');
	}

	stopLocal {
		super.stop; // stop the tdef
		isPlayingSomewhere = false; // note that the tdef has stopped playing
	}

	stopRemote {
		isPlayingSomewhere = false; // don't actually stop, just note that the tdef has stopped playing
	}*/

	*setupNewResponder {
		OSCFunc({|msg, time, senderAddr, recvPort|
			var name, item, sTdef;
			# name, item = msg;
			sTdef = this.lookup(name);
			[\new, msg, time, senderAddr, recvPort].postln;
			//^this.isNewOrExisting(creatorPeer, name, item);
		}, '/new');
	}

	*isNewOrExisting {arg creatorPeer, name, item;
		var res = all.at(name);
		if(res.isNil) { // if not existing
			\new.postln;
			^this.createNew(creatorPeer, name, item);
		} { // if existing
			\exists.postln;
			if(item.notNil) { ^this.sendReplaceMsg(name, item); }
		}
	}

	//this.update(name, item)

	update {arg name, item;
		var sTdef = all.at(name);
		sTdef.source = item;
	}

}



