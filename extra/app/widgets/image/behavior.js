
function ImgWidget(id, baseUrl) {
	this.id = id;
	this.baseUrl = baseUrl;
	this.freq = 60 * 1000;
	
	
	this.start = function() {
		this.update();
	}
	
	this.reload = function() {
		var url = this.baseUrl;
		if(url.indexOf("?") == -1)
			url = url + "?"
		else
			url = url + "+"
		url = url + new Date().getTime() 
		$("#"+this.id).attr("src", url );
	}
	
	this.update = function() {
			this.reload();
			var self = this;
			var wobble = Math.floor( Math.random()*(this.freq/3) );
			setTimeout(function() { self.update(); }, this.freq + wobble);
	}
	
}



