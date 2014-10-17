alter table CourseVersion
	add column showProgress bool default true,
	add column showNavigation bool default true;