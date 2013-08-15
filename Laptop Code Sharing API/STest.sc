STest {

	var <creatorPeer, <name, <>sourceFunc, <isPlaying;

	*new {arg argCreatorPeer, argName, argSourceFunc;
		^super.new.init(argCreatorPeer, argName, argSourceFunc)
	}

	init {arg argCreatorPeer, argName, argSourceFunc;
		sourceFunc = argSourceFunc;
		name = argName;
		creatorPeer = argCreatorPeer;
		isPlaying = false;
	}

	play {
		isPlaying = true;
	}

	stop {
		isPlaying = false;
	}

	replace {arg argSourceFunc;
		sourceFunc = argSourceFunc;
	}

}