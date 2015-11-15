update Institution set assetsURL='__DEPRECATED__';
alter table Institution add column assetsRepositoryUUID char(36);
alter table Institution add constraint fk_assets_repo foreign key(assetsRepositoryUUID) references ContentRepository(uuid);
