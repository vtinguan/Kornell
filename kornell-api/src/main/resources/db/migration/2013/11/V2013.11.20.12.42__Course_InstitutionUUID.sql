alter table Course add column institution_uuid char(36) not null default 'FIXME-PLS' references Institution(uuid);
