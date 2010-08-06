
function ContentWidget(name, url, target, update) {
	this.name = name;
	this.url = url;
	this.target = target;
	this.freq = update*1000;
	
	
	this.start = function() {
		this.update();
	}
	
	this.load = function() {
		var self = this
		$.ajax({
			type : 'GET',
			url : this.url,
			success : function(a,b,c){ self.fillTarget(a,b,c) },
			error : function(a,b,c){ self.handleError(a,b,c) },
			dataType : 'json'}
		);
	}
	
	this.update = function() {
		if(!stopped) {
			this.load();
			var self = this;

			//add up to 33% of freq to this timeout, so that all the requests don't hit at once
			var wobble = Math.floor( Math.random()*(this.freq/3) );
			
			setTimeout(function() { self.update(); }, this.freq+wobble);
		}
	}
	
	this.handleError = function(xhr, status, err) {
		Dashboard.warn("Could not load "+this.name+" content<br /><pre>"+status+"</pre>");
	}
	
	this.fillTarget = function(resp, status, xhr) {
		$(this.target).text(resp)
	}
}



