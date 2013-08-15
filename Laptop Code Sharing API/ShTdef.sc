ShTdef {

	var <name, <func, <creatorName, isPlaying; // should creator be the peer or just the name of the peer
	classvar addrBook, creatorPeer, <pool; // confusing to have two creators

	*initClass {
		this.setupCreateUpdateResponder;
		this.setupRemoveResponder;
		this.setupPlayResponder;
		this.setupStopResponder;
	}

	*setupCreateUpdateResponder {
		OSCFunc({arg msg;
			// is it feasible to send large functions as osc messages?
			var tdefName, tdefFunc;
			\createUpdateResponder.postln;
			# tdefName, tdefFunc = msg;
			this.createOrUpdate(tdefName, tdefFunc);
		}, '/createUpdate')

	}

	*setupRemoveResponder {
		OSCFunc({arg msg;
			var tdefName;
			tdefName = msg;
			this.remove(tdefName);
		}, '/remove')
	}

	*setupPlayResponder {
		OSCFunc({arg msg;
			var tdefName;
			tdefName = msg;
			this.play(tdefName);
		}, '/play')
	}

	*setupStopResponder {
		OSCFunc({arg msg;
			var tdefName;
			tdefName = msg;
			this.stop(tdefName);
		}, '/stop')
	}

	*setupPool {arg mePeer, argAddrBook;
		creatorPeer = mePeer;
		addrBook = argAddrBook; // is this needed as a variable?
		pool = OSCObjectSpace(addrBook, oscPath: '/ShTdefSpace');
		pool.addDependant({|objectSpace, what, key, shTdef|
			// what to do when the objectspace is updated?
		});
	}

	*new {arg argName, argFunc;
		^this.checkIfExists(argName, argFunc);
	}

	*checkIfExists {arg argName, argFunc;
		if (pool.keys.includes(argName).not, { // if a ShTdef with this name doesn't exist in the pool, create it
			^this.createOrUpdate(argName, argFunc);
		}, { // if it does, send a message to the creator to update the
			var nameOfCreator;
			nameOfCreator = pool.at(argName).creatorName;
			^this.sendMsgToCreateOrUpdate(nameOfCreator, argName, argFunc);
		})
	}

	init {arg argName, argFunc;
		name = argName;
		func = argFunc;
		creatorName = creatorPeer.name;
		isPlaying = false;
	}

	*sendMsgToCreateOrUpdate {arg nameOfCreator, tdefName, tdefFunc;
		var tdefFuncString;
		\sendMsgToCreateOrUpdate.postln;
		tdefFuncString = tdefFunc.asCompileString;
		addrBook.send(nameOfCreator, \createOrUpdate, tdefName, tdefFuncString);
	}

	*sendMsgToRemove {arg nameOfCreator, tdefName;
		addrBook.send(nameOfCreator, \remove, tdefName);
	}

	*sendMsgToPlay {arg nameOfCreator, tdefName;
		addrBook.send(nameOfCreator, \play, tdefName);
	}

	*sendMsgToStop {arg nameOfCreator, tdefName;
		addrBook.send(nameOfCreator, \stop, tdefName);
	}

	*createOrUpdate {arg tdefName, tdefFunc;
		var shTdef;
		\createOrUpdate.postln;
		Tdef(tdefName, tdefFunc);
		shTdef = super.new.init(tdefName, tdefFunc);
		pool.put(tdefName, shTdef);
	}

	*remove {arg tdefName;
		\remove.postln;
		Tdef(tdefName).clear;
		pool.put(tdefName, nil);
	}

	*play {arg tdefName;
		var shTdef;
		\play.postln;
		Tdef(tdefName).play;
		shTdef = pool.at(tdefName);
		shTdef.isPlaying = true;
		pool.put(tdefName, shTdef);
	}

	*stop {arg tdefName;
		var shTdef;
		\stop.postln;
		Tdef(tdefName).stop;
		shTdef = pool.at(tdefName);
		shTdef.isPlaying = false;
		pool.put(tdefName, shTdef);
	}

}

// reply to Scott
// Q: do new peers automatically get updated on the state of the OSCObjectSpace

ShTdefOld {

	// The ShTdef class is an umbrella class which gathers together some aspects of state from Tdef's running on the machines of peers in a network
	// It can be seen as a global interface to Tdef's running on each node in the network
	// Instead of accessing Tdef's directly, in a shared context they are always accessed through the ShTdef interface
	// access means creating, updating source function, playing and stopping

	// A key point is that the Tdef's only exist locally on the machine of the peer that created them (the creator)
	// To play a Tdef we notify the creator, who then starts it playing locally
	// To clear a Tdef we notify the creator, who then removes it locally

	// ShTdef does not replicate the full state of a Tdef, as this contains open functions which cannot be archived and shared in an OSCObjectSpace
	// instead only the name and source function are taken from the Tdef
	// The name is important as this acts as global key shared by all peers in the network
	// For example, if one player creates a ShTdef called \x and another player later does the same, the latter will overwrite the former
	// The source function is important as it may be used by other players on the network to edit the Tdef

	// Normally, calling Tdef.all shows us the Tdef's created by a single peer
	// Additionally calling ShTdef.all provides us with a higher level of abstraction, showing us the ShTdef's running on the network

	// Setup
	// before new ShTdef's can be made a creator must be set (the local peer)

	// Creating / Updating / Removing / Playing / Stopping

	// When an ShTdef is created
	// it is added to the shared pool
	// each peer is notified
	// if the notified peer is the creating peer then a new Tdef is created	with the same name and func as the ShTdef

	// When an ShTdef is updated
	// it replaces the existing ShTdef in the shared pool
	// each peer is notified
	// if the notified peer is the creating peer then the existing Tdef with the same name as the ShTdef and func as the

	// creation and updating are essentially the space process and are handled by Tdef class

	// When an ShTdef is removed
	// it is removed from the shared pool
	// each peer is notified
	// if the notified peer is the creating peer then the Tdef with the same name as the ShTdef is cleared

	// When an ShTdef is played
	// it is looked up in the shared pool
	// it's playing state is set to true
	// it is placed back in the shared pool
	// each peer is notified
	// if the notified peer is the creating peer then the Tdef with the same name as the ShTdef is set to play

	// When an ShTdef is stopped
	// it is looked up in the shared pool
	// it's playing state is set to false
	// it is placed back in the shared pool
	// each peer is notified
	// if the notified peer is the creating peer then the Tdef with the same name as the ShTdef is set to play

	var <name, <func, isScheduledForCreation, isScheduledForRemoval, isScheduledToPlay, isScheduledToStop;
	classvar creator, <all;

	*setCreator {arg argCreator;
		// set once on successful peer registration
		creator = argCreator;
	}

	*setupObjectSpace {arg addrBook;
		all = OSCObjectSpace(addrBook, oscPath: '/ShTdefSpace');
		all.addDependant({|objectSpace, what, key, shTdef|
			// what to do when the objectspace is updated?
		});
	}

	*new {arg argName, argFunc;
		^this.checkIfExists(argName, argFunc);
	}

	*checkIfExists {arg argName, argFunc;
		// does a ShTdef with the same name already exist?
		if (all.keys.includes(argName).not, {
			// if not create a new ShTdef with the supplied name and func
			^this.createNew(argName, argFunc);
		}, {
			// if so replace existing ShTdef's func with this func
			^this.replaceExisting(argName, argFunc);
		})
	}

	*createNew {arg argName, argFunc;
		// create a new Tdef using the name and func provided
		var newShTdef;
		Tdef.new(argName, argFunc);
		// create a new ShTdef using the creator, name and func provided
		newShTdef = super.new.init(argName, argFunc);
		// share the ShTdef with the group
		all.put(argName, newShTdef); // should update all peers
		^newShTdef;
	}

	init {arg argName, argFunc;
		name = argName;
		func = argFunc;
		isScheduledForCreation = false;
		isScheduledForRemoval = false;
		isScheduledToPlay = false;
		isScheduledToStop = false;
	}

	replaceExisting {arg argName, argFunc;
		var existingShTdef;
		// lookup the existing ShTdef
		existingShTdef = all.at(argName);
		//
		existingShTdef.func = argFunc;
		// replace the existing ShTdef with the one containing the new func
		all.put(existingShTdef);
	}

	*clear {arg argName;
		// remove ShTdef from ObjectSpace (peers are notified)
		var existingShTdef, newShTdef;
		// lookup the existing ShTdef
		existingShTdef = all.at(argName);
		newShTdef = existingShTdef.isScheduledForRemoval_(true);
		// replace the existing ShTdef with the one containing the new func
		all.put(argName, newShTdef); // notifies peers
	}

	play {

	}

	stop {

	}

}