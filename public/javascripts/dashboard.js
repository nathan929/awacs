Dashboard.register = function(runThis) { _dashboard.register(runThis); } 
Dashboard.running = function() { return _dashboard.running(); }
Dashboard.fatal = function(error) { _dashboard.fatal(error); }
Dashboard.warn = function(error) { _dashboard.warn(error); }

function Dashboard(id, name, checkup, interactive) {
	this.name = name;
	this.id = id;
	this.checkup = checkup;
	this.interactive = interactive;
	this.stopped = true;
	this.hidden = false;
	this.broken = false;
	this.startup = [];
	this.freq = 30*1000;
	
	
	this.start = function(start_hidden) {
		stopped = false;
		if(start_hidden)
			this.hide();
		var self = this;
		
		for( var i in this.startup) {
			var doThis = function(k) {
				return function() {
				self.startup[k].start();
				}
			}
			setTimeout(doThis(i), (i*75+1));
		}
		if(this.id != "-1")
			setInterval(function() { self.check();}, this.freq);
	}

	this.running = function() {
		return !this.stopped;
	}
	
	this.register = function( runThis ) {
		this.startup[this.startup.length] = runThis;
	}
	
	this.check = function() {
		var self = this;
		$.ajax({
		type : 'GET',
		url : this.checkup,
		success : function(a,b,c) { self.checkback(a) },
		error : function(a,b,c) { self.broken = true; },
		dataType : 'json'}
		);
	}

	this.checkback = function(dash) {
		if(dash == null) {
			this.broken = true;
			return;
		}
		if(this.broken)
			this.reload(1000);
		
		var self = this;

		if( (!this.hidden) && dash.hide )
			this.hide();
	
		if( (dash.id != this.id) || (this.hidden && !dash.hide && !this.interactive) )
			this.reload(1000);
	
		if(!dash.hide && this.interactive) this.hidden = false;
	
	}

	this.hide = function() {
		if( !this.interactive ) {
			this.stopped = true;
			$("#dialog-close span.nointeract").text("Check again later");
			$("#dialog-content").html("<h2>This dashboard is currently unavailable</h2>");
			$("#blackout").fadeIn('slow');
			$("#dialogger").show();		
		} else {
			if(!this.hidden)
				this.warn("<h2>Dashboards have been hidden</h2>Protect any sensitive information displayed on your screen");
		}
		this.hidden = true;
	}

	this.warn = function(errorMsg) {
		this.showError(errorMsg,5000);	
	}

	this.fatal = function(errorMsg) {
		this.showError(errorMsg,10001);
		this.reload(10*1000);
	}

	this.reload = function(timeout) {
		$("#dialog-close").text("The dashboard will be reloaded shortly...");
		setTimeout("window.location.reload();", timeout);
	}

	this.showError = function(errorMsg, timeout) {
		if(!(timeout > 999))
			timeout = 5000;
		$("#dialog-content").html(errorMsg);
		$("#dialogger").fadeIn('fast');
	
		if(!$("#dashboard").hasClass("interactive")) {
			setTimeout(function() {$("#dialogger").fadeOut('fast');}, timeout);
		}
	}

	$(function(){
		$("#dialog-close #close").click(function(){
			$("#dialogger").fadeOut('fast');
		});
	});
}


var sec2min = function(seconds) {
	var minutes = Math.floor(seconds / 60);
	seconds = ""+seconds % 60;
	if(seconds.length < 2) seconds = "0"+seconds;

	return minutes+":"+seconds;
}