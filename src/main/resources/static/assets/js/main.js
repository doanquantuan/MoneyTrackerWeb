"use strict";

// Always use light mode theme
$("body").addClass("d2c_theme_light").removeClass("d2c_theme_dark");
localStorage.setItem("theme", "d2c_theme_light");

// Preloader
window.onload = function () {
	const $preloader = $(".preloader");

	if ($preloader.length) {
		$preloader.delay(800).fadeOut(200, function () {
			$(".d2c_wrapper").addClass("show");
		});
	} else {
		// Nếu không có preloader → vẫn phải show wrapper
		$(".d2c_wrapper").addClass("show");
	}
};
