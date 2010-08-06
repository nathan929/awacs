function ClockWidget(targ) {
	this.target = targ;
	
	this.start = function() {
		var self = this;
		setInterval(function() { $(self.target).text(ClockWidget.getReadable()); }, 1000);
	}


}

ClockWidget.getReadable = function() {
	var d = new Date();
	var hours = d.getHours();
	if(hours >= 12)
		pm = "pm"
	else
		pm = "am"
			
	hours = hours % 12;
	if(hours == 0)
		hours = 12;

	var mins = d.getMinutes();
	if (mins < 10)
		mins = "0"+mins;

	return hours + ":"+mins + pm
}

function DateWidget(targ) {
	this.target = targ;
	
	this.start = function() {
		var self = this;
		setInterval(function() { $(self.target).text(DateWidget.getReadable); }, 1000);
	}
	
} 

DateWidget.getReadable = function() {
	var d = new Date();
	return d.getMonth() +"/"+ d.getDate() + "/"+d.getFullYear();
}

function DateAndTimeWidget(targ) {
	this.target = targ;
	
	this.start = function() {
		var self = this;
		setInterval(function() { self.update(); }, 1000);
	}
	
	this.update = function() {
			$(this.target).text(DateWidget.getReadable() + " "+ ClockWidget.getReadable());
	}
} 