google.load("visualization", "1", {packages:["corechart", 'table', 'barchart']});

function ChartWidget(name, url, element) {
	this.name = name;
	this.url = url;
	this.chart = new google.visualization.LineChart(element);
	this.freq = 60*1000*5;
    this.data = new google.visualization.DataTable();

	this.start = function() {
		this.update();
	}
	
	this.load = function() {
		var self = this
		$.ajax({
			type : 'GET',
			url : this.url,
			success : function(a,b,c){ self.updateChart(a,b,c) },
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
		eval("var resp = "+xhr.responseText);
		Dashboard.warn("Could not load "+this.name+" chart:<br /><pre>"+resp.message+"</pre>");
	}
	
	this.updateChart = function(resp, status, xhr) {
		cols = resp.columns;
		for( i in cols ) {
			this.data.addColumn(cols[i].type, cols[i].name);
		}
		this.data.addRows(resp.rows);
		
        this.chart.draw(this.data, {height: 300, title: this.name});
	}
}



