$(function($) {
 $('.nav .dropdown-parent').hover(function() {
	 if ($(window).width() >= 768) {
		 $(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
	 }
 
}, function() {
	if ($(window).width() >= 768) {
		$(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
	}
 
});

 
$('.nav .dropdown-parent a').click(function(){
	if ($(this)[0].target == undefined || $(this)[0].target != "_blank")
	{
		location.href = this.href;
	}
	$('.dropdown-menu').stop(true, true).delay(100).slideUp();
 });

	$('.nav>li>a').focus(function(){
		if ($(window).width() >= 768) {
			$('.dropdown-menu').stop(true, true).delay(100).slideUp();
			$(this).parent().find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
		}
	});

 
});