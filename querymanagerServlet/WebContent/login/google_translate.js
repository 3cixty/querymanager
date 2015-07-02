function googleTranslateElementInit() {
    new google.translate.TranslateElement({
        pageLanguage: 'en',
        layout: google.translate.TranslateElement.InlineLayout.SIMPLE,
        multilanguagePage: true
    }, 'google_translate_element');
    //Register a click event, to activate our custom display, on the selected translate element.
    $('#google_translate_element').click(function () {
        setTimeout("translate.show();", 50);
    });

}
var translate = {
    frameSelector: "body >div.skiptranslate >iframe",
    listSelector: "body >iframe.goog-te-menu-frame.skiptranslate",
    wrapper: null,
    frame: null,
    /*Some complication with list selector:
    * Issue: Google translate return three iframes for the listSelector
    *
    * First: has list of languages activated the the selected element
    * Second: has same copy of list that is activated by the dropdown on Translate bar.
    * Third: is a iframe just to contain two options!!!*/
    list: null,

    //Check if translate is activated, if so set the wrapper and frame variable for future access.
    setWrapper: function () {
        translate.wrapper = $(translate.frameSelector).length > 0 ? $(translate.frameSelector).parent() : null;
        if (translate.wrapper != null)
            translate.frame = $(translate.frameSelector);
    },
    //Show the translate wrapper,frame and list
    show: function (e) {
        if (translate.list == null)
            translate.list = $($(translate.listSelector)[0]);
        if (translate.wrapper == null) translate.setWrapper();
        if (translate.wrapper == null) {
            translate.pullListUp();
            return;
        }
        if (translate.wrapper.css("display") == "none") translate.pullListUp();
        else translate.pushListDown();
        translate.frame.addClass("display");
    },
    //In-case the translate is not yet active, the list has to take up the space. for the translate bar.
    pullListUp: function () {
        translate.list.css("margin-top", "0px");
    },
    //If translate is active, the list show move down to respect the translate bar.
    pushListDown: function () {
        translate.list.css("margin-top", "40px");
    },
    // Hide the translate frame.
    // wrapper is not required to hide as its actually just the frame.
    // list is auto hidden by google translate
    hide: function (e) {
        if (translate.wrapper == null) return;
        translate.frame.removeClass("display");
        translate.frame.addClass("hidden");
    }
};
//We have too many "e.stopPropagation" so using click method won't work properly.
$(window).focus(function () {
    translate.hide();
});
