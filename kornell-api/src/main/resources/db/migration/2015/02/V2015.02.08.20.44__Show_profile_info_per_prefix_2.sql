
SET @defaultPrefixUUID = 'c84bc4ee-afcd-11e4-ae3c-00ff3b62bf45';
update InstitutionRegistrationPrefix set uuid = @defaultPrefixUUID, showEmailOnProfile = 0, showCPFOnProfile = 0, showContactInformationOnProfile = 0 where uuid = 'FIX-ME-PLEASE';
update CourseClass set institutionRegistrationPrefixUUID = @defaultPrefixUUID where institutionRegistrationPrefixUUID is not null;

update Person set registrationType = 'email'
where uuid in (
	select p.uuid from (select * from Person) p
	join Password pwd on p.uuid = pwd.person_uuid
	where pwd.username like '%@%'
);

update Person set registrationType = 'username'
where uuid in (
	select p.uuid from (select * from Person) p
	join Password pwd on p.uuid = pwd.person_uuid
	where pwd.username like '%/%'
);
update Person set registrationType = 'cpf'
where uuid in (
	select p.uuid from (select * from Person) p
	join Password pwd on p.uuid = pwd.person_uuid
	where p.registrationType is null or p.registrationType = 'FIX-ME-PLEASE'
);
update Person set institutionREgistrationPrefixUUID = @defaultPrefixUUID where registrationType = 'username';
