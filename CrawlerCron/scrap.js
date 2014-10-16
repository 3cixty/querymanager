var args = require('system').args;
var url = 'https://plus.google.com/' + args[1] + '/reviews?hl=en'

var page = require('webpage').create();
page.settings.userAgent = 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.71 Safari/537.36';
page.onAlert = function (msg) {
	if (msg == 'done_clicking') {
		console.log(page.content);
		phantom.exit();
	}
};

page.open(url, function(status) {
	page.evaluate(function() {
		function mouseclick( element ) {
			// create a mouse click event
			var event = document.createEvent( 'MouseEvents' );
			event.initMouseEvent( 'click', true, true, window, 1, 0, 0 );

			// send click to element
			element.dispatchEvent( event );
		}

		function loadMoreItems() {
			var found = false;
			var buttons = document.querySelectorAll('span[role=button]');
			for (var i = 0; i < buttons.length; i ++) {
				var el = buttons[i];

				if (el.offsetParent == null) {
					continue;
				}

				if (el.textContent == 'More') {
					mouseclick(buttons[i]);
					found = true;
				}
				if (el.textContent.indexOf('Loading') > -1) {
					found = true;
				}
			}

			if (!found) {
				var allSpans = document.getElementsByTagName('span');
				for (var i = 0; i < allSpans.length; i ++) {
					var el = allSpans[i];
					if (el.offsetParent == null) {
						continue;
					}

					if (el.textContent == 'Loading...') {
						found = true;
					}
				}
			}

			if(found) {
				window.setTimeout(loadMoreItems, 100);
			} else {
				alert('done_clicking');
			}
		}

	        loadMoreItems();
	});
});
