UPDATE Course
SET infoJson = REPLACE(infoJson, '"type":"time"', '"type":"fa fa-clock-o"')
WHERE infoJson LIKE '%"type":"time"%';

UPDATE Course
SET infoJson = REPLACE(infoJson, '"type":"help"', '"type":"fa fa-question-circle"')
WHERE infoJson LIKE '%"type":"help"%';