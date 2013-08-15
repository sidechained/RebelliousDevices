TdefH : Tdef {

	// TdefH is a extended Tdef which keeps a record of its prior state
	// the aim is to be able to browse back through the history of a Tdef, in order to edit or re-execute its source (return to previous states)
	// history is a array of events, where each event stores the time the Tdef was executed and it's source function at that time

	// ToDO:
	// - collapsable flickbook GUI allowing access to the history of any named Tdef since the performance began
	// - ability to tag useful states (all/tagged switch)

	var <history;
	var <historyIndex;
	var <>updateState; // flag which if true will update the GUI when the Tdef's source is updated

	*new { arg key, item;
		var res = this.at(key);
		if(res.isNil) {
			// - OLD FUNCTIONALITY OF Tdef.new:
			// if the named Tdef doesn't exist already in the Tdef.all dictionary
			// - create a new TaskProxy using the 'item' function
			// - add a reference to the Tdef object to the Tdef.all dictionary
			// - MODIFIED FUNCTIONALITY OF TdefH.new:
			res = super.new(key, item); // make a new Tdef
			// - NEW FUNCTIONALITY OF TdefH.new:
			// - initalise item's history
			res.initHistory;
			// - add entry in item's history
			res.addHistoryEntry(item);
		} {
			// - OLD FUNCTIONALITY OF Tdef.new:
			// if the named Tdef does exist
			// - check if an 'item' function has been given, if so set the Tdef source as this 'item' function
			if(item.notNil) { res.source = item };
			// - NEW FUNCTIONALITY OF TdefH.new:
			// - append entry to the Tdef's history
			res.addHistoryEntry(item);
		}
		^res
	}

	*allHistory {
		^this.all.collect{arg v, k; v.history};
	}

	initHistory {
		history = Array.new;
		updateState = true;
	}

	addHistoryEntry {arg item;
		var newEntry;
		newEntry = TdefHistoryEntry.new(item);
		history = history.add(newEntry);
		if (updateState, {
			this.goToLastHistoryEntry;
		});
	}

	getCurrentHistoryEntry {
		^history[historyIndex]
	}

	goToNextHistoryEntry {
		if (historyIndex < (history.size-1), { historyIndex = historyIndex + 1});
	}

	goToPreviousHistoryEntry {
		if (historyIndex > 0, { historyIndex = historyIndex - 1});
	}

	goToFirstHistoryEntry {
		historyIndex = 0;
	}

	goToLastHistoryEntry {
		historyIndex = history.size-1
	}

	copyCode {
		\copyCodeFromWindow.postln;
	}

}

TdefHistoryEntry {

	var <timeExecuted, <source;

	*new {arg item;
		^super.new.init(item);
	}

	init {arg item;
		timeExecuted = 1; // temp for now, until I understand how to get a current timestamp
		source = item.asCompileString;
	}

	getCurrentTime {
		// ??
	}

}

TdefHGUI {

	var tdefH; // a tdefH that the GUI will display
	var codeRow, tdefHistoryIndexLabel;

	// a gui which allows the code history of a TdefH to be browsed through
	// think of it like a code flickbook

	*new {arg argTdefH;
		^super.new.init(argTdefH);
	}

	init {arg argTdefH;
		var gui;
		tdefH = argTdefH;
		gui = this.makeGUI;
		this.updateGUI; // initial update, to get things rolling
		^gui;
	}

	makeGUI {
		var buttonRow;
		buttonRow = this.makeButtonRow;
		codeRow = this.makeCodeRow;
		^View(nil, Rect(0, 0, 400, 20)).layout_(
			VLayout(*[buttonRow, codeRow]).margins_(0).spacing_(0);
		);
	}


	makeButtonRow {
		var buttonLayout;
		var tdefHNameLabel, nextEntryButton, previousEntryButton, firstEntryButton, lastEntryButton, updateButton, copyCodeButton;
		tdefHNameLabel = StaticText().string_(tdefH.key).background_(Color.grey);
		tdefHistoryIndexLabel = StaticText().background_(Color.grey);
		nextEntryButton = Button()
		.states_([["<"]])
		.action_({
			tdefH.goToPreviousHistoryEntry;
			this.updateGUI;
		});
		previousEntryButton = Button()
		.states_([[">"]])
		.action_({
			tdefH.goToNextHistoryEntry;
			this.updateGUI;
		});
		firstEntryButton = Button()
		.states_([["f"]])
		.action_({
			tdefH.goToFirstHistoryEntry;
			this.updateGUI;
		});
		lastEntryButton = Button()
		.states_([["l"]])
		.action_({
			tdefH.goToLastHistoryEntry;
			this.updateGUI;
		});
		updateButton = Button()
		.states_([["u", Color.white, Color.green], ["u", Color.white, Color.red]])
		.action_({arg butt;
			case
			{ butt = 1 } { tdefH.updateState_(true) }
			{ butt = 0 } { tdefH.updateState_(false) };
		});
		copyCodeButton = Button()
		.states_([["c"]])
		.action_({tdefH.copyCode});
		buttonLayout = HLayout(*[tdefHNameLabel, tdefHistoryIndexLabel, nextEntryButton, previousEntryButton, firstEntryButton, lastEntryButton, updateButton, copyCodeButton]).margins_(0).spacing_(0);
		^View().layout_(buttonLayout);
	}

	makeCodeRow {
		// currently consists of a single textview which displays the code
		^TextView(nil, Rect(0, 0, 400, 140));
	}

	updateGUI {
		codeRow.string_(tdefH.getCurrentHistoryEntry.source);
		tdefHistoryIndexLabel.string_(this.makeTdefHistoryIndexLabelString);
	}

	makeTdefHistoryIndexLabelString {
		var currentLine, maxLines;
		currentLine = (tdefH.historyIndex + 1).asString;
		maxLines = tdefH.history.size.asString;
		^currentLine +/+ maxLines;
	}

}

TdefHAllGUI {

	*new {
		^super.new.init;
	}

	init {
		var tdefHRows;
		tdefHRows = TdefH.all.values.collect{arg tdefH;
			TdefHGUI.new(tdefH);
		};
		tdefHRows.postln;
		^View(nil, Rect(0, 0, 400, 1000)).layout_(VLayout(*tdefHRows).margins_(0).spacing_(0))
		.front;
	}

}

+ Tdef {

	*new { arg key, item;
		var res = this.at(key);
		if(res.isNil) {
			res = super.new(item).prAdd(key);
		} {
			if(item.notNil) { res.source = item }
		}
		^res
	}

}